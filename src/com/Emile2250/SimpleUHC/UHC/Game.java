package com.Emile2250.SimpleUHC.UHC;

import com.Emile2250.SimpleUHC.SimpleUHC;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class Game {

    // Game Settings
    private int maxPlayers;
    private int minPlayers;
    private int borderSize;
    private int gracePeriod; // In Seconds
    private int pvpPeriod; // In Seconds

    // Game Variables
    private ArrayList<UHCPlayer> players;
    private boolean teamGame;
    GameState state;

    // Game constructor
    public Game() {
        FileConfiguration settings = SimpleUHC.getSettings();

        players = new ArrayList<>();
        state = GameState.LOBBY;

        // Tries to load the actual game setting variables, if it errors it will use the defaults which is set in the catch statement.
        try {

            maxPlayers = settings.getInt("max-players");
            minPlayers = settings.getInt("min-players");
            borderSize = settings.getInt("border-size");
            gracePeriod = settings.getInt("grace-period");
            pvpPeriod = settings.getInt("pvp-period");

        } catch (Exception e) {

            // Sets defaults values in case of a configuration issue.
            maxPlayers = 50;
            minPlayers = 10;
            borderSize = 500;
            gracePeriod = 600;
            pvpPeriod = 1200;

            System.out.println("Uh oh! You had an error with your settings configuration.");
            e.printStackTrace();
        }
    }

    // Adds a player to the UHC Game
    public void addPlayer(UHCPlayer player) {
        players.add(player);
    }

    // Returns number of players in the queue
    public int numPlayers() {
        return players.size();
    }

}
