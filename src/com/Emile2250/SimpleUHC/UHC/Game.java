package com.Emile2250.SimpleUHC.UHC;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.Util.ActionBar;
import com.Emile2250.SimpleUHC.Util.ScoreboardHandler;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    // Game Settings
    private int maxPlayers;
    private int minPlayers;
    private int borderSize;
    private int gracePeriod; // In Seconds
    private int teamSize;
    private boolean hasNights;
    private boolean naturalHealing;

    // Game Variables
    private ArrayList<Player> players;
    private ArrayList<UHCTeam> teams;
    private boolean teamGame;
    private World world;
    private GameState state;
    private BukkitTask task;
    private String gameName;
    private ArrayList<ScoreboardHandler> scoreboards;
    HashMap<Player, Integer> kills;
    private int countdown;

    // Game constructor
    public Game(String name) {
        FileConfiguration settings = SimpleUHC.getSettings();

        players = new ArrayList<>();
        state = GameState.LOBBY;
        gameName = name;
        teams = new ArrayList<>();
        scoreboards = new ArrayList<>();
        kills = new HashMap<>();
        countdown = 20;

        // Tries to load the actual game setting variables, if it errors it will use the defaults which is set in the catch statement.
        try {

            maxPlayers = settings.getInt("Games." + name + ".max-players");
            minPlayers = settings.getInt("Games." + name + ".min-players");
            borderSize = settings.getInt("Games." + name + ".border-size");
            gracePeriod = settings.getInt("Games." + name + ".grace-period");
            naturalHealing = settings.getBoolean("Games." + name + ".natural-healing");
            hasNights = settings.getBoolean("Games." + name + ".has-nights");
            teamGame = settings.getBoolean("Games." + name + ".team-game");

            if (teamGame)
                teamSize = settings.getInt("Games." + name + ".team-size");
            else
                teamSize = 0;

        } catch (Exception e) {

            // Sets defaults values in case of a configuration issue.
            maxPlayers = 50;
            minPlayers = 10;
            borderSize = 2000;
            gracePeriod = 600;
            hasNights = true;
            naturalHealing = true;
            teamGame = false;
            teamSize = 0;

            System.out.println("Uh oh! You had an error with your settings configuration for " + gameName);
            e.printStackTrace();
        }
    }

    // Adds a player to the UHC Game
    public void addPlayer(Player player) {
        players.add(player); // Adds player to the game
        kills.put(player, 0); // Sets player kills to 0

        if (teamGame) {
            UHCTeam team = getAvailableTeam(); // Tries to find first available team
            if (team != null) {
                team.addMember(player); // Adds player to available team
            } else {
                team = new UHCTeam(player); // If there is no team to join it will create a new one
                teams.add(team); // Adds the team to the teams arraylist
            }
        }

        if (players.size() == minPlayers && state == GameState.LOBBY) { // Checks if we are in lobby and we have minimum number of players to start.
            if (teamGame) // Checks if it's a team game
                if (teams.size() < 2) // Makes sure there's at least two teams
                    return;

            state = GameState.STARTING;
            startCountdown(); // Starts the countdown to start the game
        }

        sendToPlayers(); // Updates the players as well as game state
    }

    // Removes a player from the current UHCGame
    public void removePlayer(Player player) {
        players.remove(player); // Removes player from the game
        removeBoard(player); // Removes scoreboard from player
        kills.remove(player); // Removes player from kill hashmap

        if (teamGame) {
            UHCTeam team = getTeam(player); // Gets the players team
            if (team != null) { // Makes sure its not null for some reason
                team.removeMember(player); // Removes the player
                if (team.getMembers().size() == 0) // Checks if the team is all dead
                    teams.remove(team); // Removes team from the game
            }
        }

        if (players.size() == minPlayers - 1 && state == GameState.STARTING) { // Checks if player count is less than amount needed to start & we are in lobby state.
            if (task != null) { // Makes sure there is an actual task running
                state = GameState.LOBBY;
                countdown = 20;
                task.cancel(); // Cancels the countdown
                task = null;
            }
        } else if ((numPlayers() == 1 || teams.size() == 1) && (state == GameState.GRACE || state == GameState.PVP)) {
            if (task != null) {
                task.cancel();
                task = null;
            }

            state = GameState.FINISHED; // Tells us the game is finished
            stop(); // Finishes game by deleting world and teleporting everyone out
            SimpleUHC.getGames().remove(this); // Removes game from list to be garbage collected
        }

        sendToPlayers();
    }

    // Updates boards for all players
    private void sendToPlayers() {
        for (Player p : players) {
            getBoard(p).sendToPlayer();
        }
    }

    // Finds the board associated with the player
    private ScoreboardHandler getBoard(Player p) {
        for (ScoreboardHandler s : scoreboards) {
            if (s.getPlayer().equals(p)) {
                return s;
            }
        }

        scoreboards.add(new ScoreboardHandler(this, p));
        return scoreboards.get(scoreboards.size() - 1);
    }

    // Removes the board from the player and list
    private void removeBoard(Player p) {
        scoreboards.remove(getBoard(p));
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public UHCTeam getTeam(Player p) {
        for (UHCTeam t : teams) {
            if (t.getMembers().contains(p)) {
                return t;
            }
        }
        return null;
    }

    // Finds the first available team, if there is none it returns null
    private UHCTeam getAvailableTeam() {
        for (UHCTeam t : teams) {
            if (t.getMembers().size() < teamSize) {
                return t;
            }
        }

        return null;
    }

    // Adds one kill
    public void addKill(Player p) {
        kills.put(p, kills.get(p) + 1);
    }

    // Returns a players kills
    public int getKills(Player p) {
        return kills.get(p);
    }

    // GETTERS

    public int numPlayers() {
        return players.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public boolean isTeamGame() {
        return teamGame;
    }

    public ArrayList<UHCTeam> getTeams() {
        return teams;
    }

    public String getGameName() {
        return gameName;
    }

    public int getCountdown() {
        return countdown;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public int getBorderSize() {
        return borderSize;
    }

    // Allows for force start with admins
    public void forceStart() {
        if (task != null) {
            task.cancel();
        }

        countdown = 0;
        sendToPlayers();
        start();
    }

    public void startCountdown() {
        // Essentially creates a "loop" that runs every X ticks (in this case 1 second) and runs the run() code
        task = SimpleUHC.getInstance().getServer().getScheduler().runTaskTimer(SimpleUHC.getInstance(), new Runnable() {

            @Override
            public void run() {
                countdown--;
                sendToPlayers(); // Updates everyones scoreboard

                if (countdown == 0) {
                    task.cancel(); // Cancels loop
                    task = null; // Sets task to null
                    start(); // Starts the game
                }
            }
        }, 0L, 20L);
    }

    public void startGracePeriod() {
        // Essentially creates a "loop" that runs every X ticks (in this case 1 second) and runs the run() code
        task = SimpleUHC.getInstance().getServer().getScheduler().runTaskTimer(SimpleUHC.getInstance(), new Runnable() {

            @Override
            public void run() {
                gracePeriod--; // Removes a second from countdown
                sendToPlayers(); // Updates everyones scoreboard

                if (gracePeriod == 0) { // Checks if countdown is over.
                    task.cancel(); // Cancels loop
                    task = null; // Sets task to null
                    world.setPVP(true); // Enables the PVP
                    state = GameState.PVP; // Changes game state
                    startPvpPeriod(); // Starts PVP period
                }
            }
        }, 0L, 20L);
    }

    public void startPvpPeriod() {
        // Essentially creates a "loop" that runs every X ticks (in this case 1 second) and runs the run() code
        task = SimpleUHC.getInstance().getServer().getScheduler().runTaskTimer(SimpleUHC.getInstance(), new Runnable() {

            @Override
            public void run() {
                if (borderSize > 50) { // Changes the world border until there's a radius of 50
                    borderSize--; // Decrements world border radius
                    world.getWorldBorder().setSize(borderSize); // Updates world border
                    sendToPlayers(); // Updates scoreboard
                } else {
                    task.cancel(); // Stops running loop if radius is 25 blocks
                    task = null; // Sets tasks to null
                }
            }
        }, 0L, 20L);
    }

    public void start() {
        FileConfiguration settings = SimpleUHC.getSettings(); // Gets updated configuration

        // Sets the world creation values
        WorldCreator creator = new WorldCreator(gameName); // Creates a world with the game name
        creator.generateStructures(false); // Disables villages, dungeons, etc.

        // If they have a list of strings in the configuration it will choose a random one, if not it will default to a random Minecraft seed.
        if (settings.isList("Seeds")) { // Makes sure its a list
            List<String> seeds = settings.getStringList("Seeds"); // Gets the list from the settings configuration
            if (seeds.size() > 0) { // Makes sure that there's at least one seed
                String stringSeed = seeds.get(ThreadLocalRandom.current().nextInt(0, seeds.size())); // Chooses a random seed out of the list.
                try {
                    long seed = Long.parseLong(stringSeed); // Parses the long out of the string
                    creator.seed(seed); // Sets the seed for the world generation.
                } catch (NumberFormatException e) {
                    System.out.println("Uh oh! One of your seeds is invalid"); // If it gets here the String wasn't a long value.
                    e.printStackTrace();
                }
            }
        } else {
            creator.seed(); // Sets a random default seed if none supplied in configuration.
        }

        world = creator.createWorld(); // Actually creates the world with the values we set and sets it to the Game world.
        world.getWorldBorder().setSize(borderSize); // Sets world border of desired size
        world.getWorldBorder().setDamageAmount(2); // Damage from border
        world.getWorldBorder().setDamageBuffer(1); // How far outside the border they can be
        world.setPVP(false); // Disables PVP for the world
        world.setGameRuleValue("doDaylightCycle", String.valueOf(hasNights));
        world.setGameRuleValue("naturalRegeneration", String.valueOf(naturalHealing));

        // For each player it chooses a random location and places them at the highest block.
        ActionBar tpMSG = new ActionBar(ChatColor.GREEN + "Teleporting in 2 seconds"); // Creates a actionbar packet to send to the player.

        if (!teamGame) {
            for (Player player : players) {
                Location loc = generateLocation();
                world.getChunkAt(loc).load(true);

                tpMSG.sendToPlayer(player); // Sends a Actionbar packet to the player.

                teleportPlayer(player, loc);
            }
        } else {
            for (UHCTeam team : teams) {
                Location loc = generateLocation();
                world.getChunkAt(loc).load(true);

                for (Player player : team.getMembers()) {
                    teleportPlayer(player, loc);
                }
            }
        }
    }

    public void stop() {

        ActionBar tpMSG = new ActionBar(ChatColor.GREEN + "Teleporting back to main world in 10 seconds"); // Creates a actionbar packet to send to the player.
        for (Player p : world.getPlayers()) {
            tpMSG.sendToPlayer(p);
        }

        SimpleUHC.getInstance().getServer().getScheduler().runTaskLater(SimpleUHC.getInstance(), new Runnable() {
            @Override
            public void run() {

                for (Player p : players) {
                    removeBoard(p);
                }

                players.clear(); // Removes all players from the game before deleting to allow for garbage collection
                World mainWorld = Bukkit.getWorld("world"); // Default main world

                if (SimpleUHC.getSettings().isString("main-world") && Bukkit.getWorld(SimpleUHC.getSettings().getString("main-world")) != null)
                    mainWorld = Bukkit.getWorld(SimpleUHC.getSettings().getString("main-world")); // Sets it to preferred main world if it is in the config and is a world

                for (Player p : world.getPlayers()) {
                    fixPlayer(p);
                    p.teleport(mainWorld.getSpawnLocation()); // Teleports any existing players to the main world to prepare for world deletion
                }

                try {
                    Bukkit.unloadWorld(world, false); // Unloads world to prepare to delete
                    FileUtils.deleteDirectory(new File(SimpleUHC.getInstance().getServer().getWorldContainer(), gameName)); // Deletes the world
                } catch (IOException e) {
                    System.out.println("Oh no! We had an issue deleting left over worlds");
                    e.printStackTrace();
                }
            }
        }, 200L);
    }

    private void teleportPlayer(Player player, Location loc) {
        // Runs the teleportation 2 seconds later to attempt to let the chunk to load.
        SimpleUHC.getInstance().getServer().getScheduler().runTaskLater(SimpleUHC.getInstance(), new Runnable() {
            @Override
            public void run() {

                fixPlayer(player);
                player.teleport(loc);

                if (state != GameState.GRACE) { // Starts grace period whenever the first player teleports
                    state = GameState.GRACE; // Sets game to running as all players have teleported correctly.
                    sendToPlayers(); // Updates the scoreboard for all the players
                    startGracePeriod(); // Starts the grace period
                }
            }
        }, 40L);
    }

    private Location generateLocation() {
        Location loc;
        Biome biome;

        // Makes sure the player doesn't spawn in falling sand or an ocean
        do {
            int x = ThreadLocalRandom.current().nextInt(borderSize) - borderSize / 2; // Grabs a random X value
            int z = ThreadLocalRandom.current().nextInt(borderSize) - borderSize / 2; // Grabs a random Z value
            loc = getActualHighestBlock(x, z); // Gets actual highest block (hopefully)
            biome = world.getBiome(loc.getBlockX(), loc.getBlockZ()); // Makes sure they dont spawn in a water biome such as an ocean
        } while (loc.subtract(0, 2, 0).getBlock().getType() == Material.WATER ||
                biome == Biome.OCEAN || biome == Biome.DEEP_OCEAN || biome == Biome.RIVER || biome == Biome.BEACH || biome == Biome.COLD_BEACH || biome == Biome.STONE_BEACH);

        return loc;
    }

    private Location getActualHighestBlock(int x, int z) {
        Location loc = world.getHighestBlockAt(x, z).getLocation();
        for (int y = 255; y > 0; y--) {
            if (world.getBlockAt(x, y, z).getType() != Material.AIR) { // Makes sure they're above sea level (not in a cave) and that they are on a solid block
                return new Location(world, x, y + 2, z); // Returns location two blocks above just in case.
            }
        }
        return loc;
    }

    private void fixPlayer(Player p) {
        p.setHealth(20); // Makes sure they're max health
        p.setFoodLevel(20); // Makes sure they're max food
        p.setSaturation(20); // Sets saturation to max

        p.getInventory().clear(); // Clears inventory
        p.getInventory().setArmorContents(null); // Clears armor
        p.updateInventory(); // Updates inventory for the player based off our changes

        p.setGameMode(GameMode.SURVIVAL); // Forces them to survival
        p.setFlying(false); // Disables their fly
        p.setAllowFlight(false); // Makes it so they're not allowed to fly

        // Removes all potion effects
        for (PotionEffect potion : p.getActivePotionEffects()) {
            p.removePotionEffect(potion.getType());
        }
    }
}
