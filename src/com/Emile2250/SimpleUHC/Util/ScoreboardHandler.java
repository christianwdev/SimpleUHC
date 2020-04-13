package com.Emile2250.SimpleUHC.Util;

import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.UHC.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

public class ScoreboardHandler {

    private ScoreboardManager manager;
    private Scoreboard board;

    private Objective objective;

    private ArrayList<ScoreboardLine> lines;

    private Game game;

    public ScoreboardHandler(Game g) {

        game = g;

        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard(); // Creates a score board

        objective = board.registerNewObjective(game.getGameName(), "dummy"); // Sets the scoreboard name to gamename and it can only change with commands
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set the position to be the sidebar
        objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + g.getGameName()); // Added the scoreboard header of UHC-?

        lines = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            lines.add(new ScoreboardLine(objective, " ", i));
        }

        updateBoard();
    }

    // Updates the number of players as well as the game state
    private void updateBoard() {

        // These lines will never changed

        lines.get(6).update(" ");
        lines.get(3).update("  ");
        lines.get(0).update("   ");


        // This is the same for every state except finished
        if (game.getState() != GameState.FINISHED) {
            lines.get(5).update("&aUsers");
            lines.get(4).update("&7" + game.numPlayers() + "/" + game.getMaxPlayers());
        }

        // Changes the scoreboard for each state, as they all display something a little different
        switch (game.getState()) {
            case LOBBY:

                lines.get(2).update("&aStatus");
                lines.get(1).update("&7Waiting for players");

                break;
            case STARTING:

                lines.get(2).update("&aStatus");

                if (game.getCountdown() > 0)
                    lines.get(1).update("&7Starting in " + game.getCountdown());
                else
                    lines.get(1).update("&7Starting now!");

                break;
            case GRACE:

                lines.get(2).update("&aGrace Period");
                lines.get(1).update("&7" + game.getGracePeriod() / 60 + "m " + game.getGracePeriod() % 60 + "s");

                break;
            case PVP:

                lines.get(2).update("&aBorder");
                lines.get(1).update("&7(" + -(game.getBorderSize() / 2) + ", " + (game.getBorderSize() / 2) + ")");

                break;
            case FINISHED:

                lines.get(5).update("&aWinner");
                lines.get(4).update("&7" + game.getPlayers().get(0).getName());
                lines.get(2).update("&aStatus");
                lines.get(1).update("&7Finished");

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
