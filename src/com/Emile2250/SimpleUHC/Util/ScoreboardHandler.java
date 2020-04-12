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

    private Score scoreOne;
    private String scoreOnePrevious;
    private Score scoreTwo;
    private String scoreTwoPrevious;

    private Game game;

    public ScoreboardHandler(Game g) {

        game = g;

        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard(); // Creates a score board

        objective = board.registerNewObjective("Queue", "dummy"); // Sets the scoreboard name to Queue and it can only change with commands
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set the position to be the sidebar
        objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + g.getGameName() + " Queue"); // Added the scoreboard header of UHC-? Queue

        scoreTwo = objective.getScore(g.getState().toString()); // Shows the game state
        scoreTwoPrevious = g.getState().toString();
        scoreTwo.setScore(0); // Line numbering

        scoreOne = objective.getScore("Users: " + g.numPlayers() + "/" + g.getMaxPlayers()); // Displays number of players
        scoreOnePrevious = "Users: " + g.numPlayers() + "/" + g.getMaxPlayers();
        scoreOne.setScore(1); // Line numbering
    }

    // Updates the number of players as well as the game state
    private void updateBoard() {

        board.resetScores(scoreOnePrevious);
        board.resetScores(scoreTwoPrevious);

        switch (game.getState()) {
            case LOBBY:

                scoreTwo = objective.getScore(game.getState().toString());
                scoreTwoPrevious = game.getState().toString();
                scoreTwo.setScore(0);

                scoreOne = objective.getScore("Users: " + game.numPlayers() + "/" + game.getMaxPlayers());
                scoreOnePrevious = "Users: " + game.numPlayers() + "/" + game.getMaxPlayers();
                scoreOne.setScore(1);

                break;
            case STARTING:

                scoreOne = objective.getScore("Alive: " + game.numPlayers() + "/" + game.getMaxPlayers());
                scoreOnePrevious = "Alive: " + game.numPlayers() + "/" + game.getMaxPlayers();
                scoreOne.setScore(1);

                scoreTwo = objective.getScore("Starting in " + game.getCountdown());
                scoreTwoPrevious = "Starting in " + game.getCountdown();
                scoreTwo.setScore(0);

                break;
            case GRACE:
                scoreOne = objective.getScore("Alive: " + game.numPlayers() + "/" + game.getMaxPlayers());
                scoreOnePrevious = "Alive: " + game.numPlayers() + "/" + game.getMaxPlayers();
                scoreOne.setScore(1);

                scoreTwo = objective.getScore("Grace Period : " + game.getGracePeriod());
                scoreTwoPrevious = "Grace Period : " + game.getGracePeriod();
                scoreTwo.setScore(0);
                break;
            case PVP:
                scoreOne = objective.getScore("Alive: " + game.numPlayers() + "/" + game.getMaxPlayers());
                scoreOnePrevious = "Alive: " + game.numPlayers() + "/" + game.getMaxPlayers();
                scoreOne.setScore(1);

                scoreTwo = objective.getScore("Border : TODO");
                scoreTwoPrevious = "Border : TODO";
                scoreTwo.setScore(0);
                break;
            case FINISHED:
                scoreTwo = objective.getScore("Winner " + game.getPlayers().get(0).getName());
                scoreTwoPrevious = "Winner " + game.getPlayers().get(0).getName();
                scoreTwo.setScore(0);
                break;
        }
    }

    // Sends an updated scoreboard to all players
    public void sendToPlayers() {
        updateBoard();
        for (Player p : game.getPlayers()) {
            p.setScoreboard(board);
        }
    }
}
