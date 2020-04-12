package com.Emile2250.SimpleUHC.UHC;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.Util.ActionBar;
import com.Emile2250.SimpleUHC.Util.ScoreboardHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    // Game Settings
    private int maxPlayers;
    private int minPlayers;
    private int borderSize;
    private int gracePeriod; // In Seconds

    // Game Variables
    private ArrayList<Player> players;
    private ArrayList<UHCTeam> teams;
    private boolean teamGame;
    private World world;
    private GameState state;
    private BukkitTask task;
    private String gameName;
    private ScoreboardHandler scoreboard;
    private int countdown;

    // Game constructor
    public Game(boolean teamGame) {
        FileConfiguration settings = SimpleUHC.getSettings();

        players = new ArrayList<>();
        state = GameState.LOBBY;
        this.teamGame = teamGame;
        gameName = "UHC-" + SimpleUHC.getGames().size();
        scoreboard = new ScoreboardHandler(this);
        countdown = 20;

        // Tries to load the actual game setting variables, if it errors it will use the defaults which is set in the catch statement.
        try {

            maxPlayers = settings.getInt("max-players");
            minPlayers = settings.getInt("min-players");
            borderSize = settings.getInt("border-size");
            gracePeriod = settings.getInt("grace-period");

        } catch (Exception e) {

            // Sets defaults values in case of a configuration issue.
            maxPlayers = 50;
            minPlayers = 10;
            borderSize = 2000;
            gracePeriod = 600;

            System.out.println("Uh oh! You had an error with your settings configuration.");
            e.printStackTrace();
        }
    }

    // Adds a player to the UHC Game
    public void addPlayer(Player player) {
        players.add(player);

        if (players.size() == minPlayers && state == GameState.LOBBY) { // Checks if we are in lobby and we have minimum number of players to start.
            state = GameState.STARTING;
            startCountdown(); // Starts the countdown to start the game
        }

        scoreboard.sendToPlayers(); // Updates the players as well as game state
    }

    // Removes a player from the current UHCGame
    public void removePlayer(Player player) {
        players.remove(player);

        if (players.size() == minPlayers - 1 && state == GameState.LOBBY) { // Checks if player count is less than amount needed to start & we are in lobby state.
            if (task != null) { // Makes sure there is an actual task running
                state = GameState.LOBBY;
                task.cancel(); // Cancels the countdown
            }
        } else if (numPlayers() == 1 && (state == GameState.GRACE || state == GameState.PVP)) {
            state = GameState.FINISHED; // Tells us the game is finished
            // TODO WIN GAME
        }

        scoreboard.sendToPlayers();
    }

    // Gets the number of players in the game
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

    public void startCountdown() {
        // Essentially creates a "loop" that runs every X ticks (in this case 1 second) and runs the run() code
        task = SimpleUHC.getInstance().getServer().getScheduler().runTaskTimer(SimpleUHC.getInstance(), new Runnable() {

            @Override
            public void run() {
                countdown--;
                scoreboard.sendToPlayers();

                if (countdown == 0) {
                    start(); // Starts the game
                    task = null; // Sets task to null
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
                scoreboard.sendToPlayers();

                if (gracePeriod == 0) { // Checks if countdown is over.
                    world.setPVP(true); // Enables the PVP
                    state = GameState.PVP;
                    task = null; // Sets task to null
                }
            }
        }, 0L, 20L);
    }

    public void start() {
        FileConfiguration settings = SimpleUHC.getSettings(); // Gets updated configuration

        // Sets the world creation values
        WorldCreator creator = new WorldCreator(gameName);
        creator.generateStructures(false);

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
        world.setPVP(false); // Disables PVP for the world

        // For each player it chooses a random location and places them at the highest block.
        ActionBar tpMSG = new ActionBar(ChatColor.GREEN + "Teleporting in 2 seconds"); // Creates a actionbar packet to send to the player.

        for (Player player : players) {
            Location loc;

            // Makes sure the player doesn't spawn in falling sand or an ocean
            do {
                int x = ThreadLocalRandom.current().nextInt(borderSize) - borderSize / 2; // Grabs a random X value
                int z = ThreadLocalRandom.current().nextInt(borderSize) - borderSize / 2; // Grabs a random Z value
                loc = world.getHighestBlockAt(x, z).getLocation(); // Gets the highest block in the world at our random coordinates
            } while (loc.getBlock().getType() == Material.WATER || loc.subtract(0, 1, 0).getBlock().getType() == Material.WATER ||
                    loc.getBlock().getType() == Material.SAND || loc.subtract(0, 1, 0).getBlock().getType() == Material.SAND);

            loc.getChunk().load(true); // Tries to load the chunk before hand
            final Location tpLoc = loc; // Creates a final variable that we can use inside the runnable

            tpMSG.sendToPlayer(player); // Sends a Actionbar packet to the player.

            // Runs the teleportation 2 seconds later to attempt to let the chunk to load.
            SimpleUHC.getInstance().getServer().getScheduler().runTaskLater(SimpleUHC.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.setHealth(20); // Makes sure they're max health
                    player.setFoodLevel(20); // Makes sure they're max food
                    player.teleport(tpLoc);
                }
            }, 40L);
        }
        state = GameState.GRACE; // Sets game to running as all players have teleported correctly.
        startGracePeriod(); // Starts the grace period
        scoreboard.sendToPlayers();
    }
}
