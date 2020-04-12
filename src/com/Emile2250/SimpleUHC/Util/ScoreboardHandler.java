package com.Emile2250.SimpleUHC.Util;

import com.Emile2250.SimpleUHC.UHC.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    private ScoreboardManager manager;
    private Scoreboard board;
    private Objective objective;
    private Score users;
    private Score starting;
    private Game game;

    public ScoreboardHandler(Game g) {

        game = g;

        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard(); // Creates a score board
        objective = board.registerNewObjective("Queue", "dummy"); // Sets the scoreboard name to Queue and it can only change with commands
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set the position to be the sidebar
        objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + g.getGameName() + " Queue"); // Added the scoreboard header of UHC-? Queue
        users = objective.getScore("Users: " + g.numPlayers() + "/" + g.getMaxPlayers()); // Displays number of players
        users.setScore(0); // Line numbering in this case
        starting = objective.getScore(g.getState().toString()); // Shows the game state
        starting.setScore(1); // Line numbering again
    }

    // Updates the number of players as well as the game state
    private void updateBoard() {
        users = objective.getScore("Users: " + game.numPlayers() + "/" + game.getMaxPlayers());
        users.setScore(0);
        starting = objective.getScore(game.getState().toString());
        starting.setScore(1);
    }

    public void sendToPlayers() {
        updateBoard();
        for (Player p : game.getPlayers()) {
            p.setScoreboard(board);
        }
    }
}
