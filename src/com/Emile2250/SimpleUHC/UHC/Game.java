package com.Emile2250.SimpleUHC.UHC;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.Util.ActionBar;
import com.Emile2250.SimpleUHC.Util.ScoreboardHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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

    // Game constructor
    public Game(boolean teamGame) {
        FileConfiguration settings = SimpleUHC.getSettings();

        players = new ArrayList<>();
        state = GameState.LOBBY;
        this.teamGame = teamGame;
        gameName = "UHC-" + SimpleUHC.getGames().size();
        scoreboard = new ScoreboardHandler(this);

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
            scoreboard.sendToPlayers(); // Updates the players as well as game state
            startCountdown(); // Starts the countdown to start the game
        }
    }

    // Removes a player from the current UHCGame
    public void removePlayer(Player player) {
        players.remove(player);

        if (players.size() == minPlayers - 1 && state == GameState.LOBBY) { // Checks if player count is less than amount needed to start & we are in lobby state.
            if (task != null) { // Makes sure there is an actual task running
                scoreboard.sendToPlayers(); // Updates the players
                state = GameState.LOBBY;
                task.cancel(); // Cancels the countdown
                return;
            }
        }

        if (numPlayers() == 1 && state == GameState.RUNNING) {
            state = GameState.FINISHED; // Tells us the game is finished
            scoreboard.sendToPlayers(); // Updates the players and game state
            // TODO WIN GAME
        }
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

    public void startCountdown() {
        // Essentially creates a "loop" that runs every X ticks (in this case 1 second) and runs the run() code
        task = SimpleUHC.getInstance().getServer().getScheduler().runTaskTimer(SimpleUHC.getInstance(), new Runnable() {

            int countdown = 20; // Sets the countdown timer to the duration we input

            @Override
            public void run() {
                countdown--;

                if (countdown == 0) {
                    task.cancel(); // Stops running the Bukkit runnable
                    state = GameState.RUNNING; // Changes game state to running
                    start(); // Starts the game
                    task = null; // Sets task to null
                }
            }
        }, 0L, 20L);
    }

    public void startGracePeriod() {
        // Essentially creates a "loop" that runs every X ticks (in this case 1 second) and runs the run() code
        task = SimpleUHC.getInstance().getServer().getScheduler().runTaskTimer(SimpleUHC.getInstance(), new Runnable() {

            int countdown = gracePeriod; // Sets the count down to the length of the grace period.

            @Override
            public void run() {
                countdown--; // Removes a second from countdown

                if (countdown == 0) { // Checks if countdown is over.
                    task.cancel(); // Stops running the Bukkit runnable
                    world.setPVP(true); // Enables the PVP
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
            int x = ThreadLocalRandom.current().nextInt(-borderSize / 2, borderSize / 2); // Grabs a random X value
            int z = ThreadLocalRandom.current().nextInt(-borderSize / 2, borderSize / 2); // Grabs a random Z value
            Location loc = world.getHighestBlockAt(x,z).getLocation(); // Gets the highest block in the world at our random coordinates
            loc.getChunk().load(true); // Tries to load the chunk before hand

            tpMSG.sendToPlayer(player); // Sends a Actionbar packet to the player.

            // Runs the teleportation 2 seconds later to attempt to let the chunk to load.
            SimpleUHC.getInstance().getServer().getScheduler().runTaskLater(SimpleUHC.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.teleport(loc);
                }
            }, 40L);
        }
        startGracePeriod(); // Starts the grace period
    }
}
