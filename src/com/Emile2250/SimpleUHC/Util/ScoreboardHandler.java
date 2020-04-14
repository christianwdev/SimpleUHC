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
    private Player player;

    public ScoreboardHandler(Game g, Player p) {

        game = g;
        player = p;

        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard(); // Creates a score board

        objective = board.registerNewObjective(game.getGameName(), "dummy"); // Sets the scoreboard name to game name and it can only change with commands
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set the position to be the sidebar
        objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + g.getGameName()); // Added the scoreboard header of UHC-?

        lines = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            lines.add(new ScoreboardLine(objective, "", i));
        }
    }

    // Updates the players stats
    private void updateBoard() {

        if (game.getState() != GameState.LOBBY && game.getState() != GameState.STARTING && game.getState() != GameState.GRACE) {
            lines.get(9).update(" ");
            lines.get(8).update("&aAlive");
            lines.get(7).update("&7" + game.numPlayers() + "/" + game.getMaxPlayers());
            lines.get(6).update("  ");
            lines.get(3).update("   ");
            lines.get(0).update("    ");
        }

        // Changes the scoreboard for each state, as they all display something a little different
        switch (game.getState()) {
            case LOBBY:

                lines.get(6).update("  ");
                lines.get(5).update("&aUsers");
                lines.get(4).update("&7" + game.numPlayers() + "/" + game.getMaxPlayers());
                lines.get(3).update("   ");
                lines.get(2).update("&aStatus");
                lines.get(1).update("&7Waiting for players");
                lines.get(0).update("    ");

                break;
            case STARTING:

                lines.get(6).update("  ");
                lines.get(5).update("&aUsers");
                lines.get(4).update("&7" + game.numPlayers() + "/" + game.getMaxPlayers());
                lines.get(3).update("   ");
                lines.get(2).update("&aStatus");

                if (game.getCountdown() > 0)
                    lines.get(1).update("&7Starting in " + game.getCountdown());
                else
                    lines.get(1).update("&7Starting now!");

                lines.get(0).update("    ");

                break;
            case GRACE:

                lines.get(6).update("  ");
                lines.get(5).update("&aAlive");
                lines.get(4).update("&7" + game.numPlayers() + "/" + game.getMaxPlayers());
                lines.get(3).update("   ");
                lines.get(2).update("&aGrace Period");
                lines.get(1).update("&7" + game.getGracePeriod() / 60 + "m " + game.getGracePeriod() % 60 + "s");
                lines.get(0).update("    ");

                break;
            case PVP:

                lines.get(5).update("&aKills");
                lines.get(4).update("&7" + game.getKills(player));
                lines.get(2).update("&aBorder");
                lines.get(1).update("&7(" + -(game.getBorderSize() / 2) + ", " + (game.getBorderSize() / 2) + ")");

                break;
            case FINISHED:

                lines.get(8).update("&aWinner");
                lines.get(7).update("&7" + game.getPlayers().get(0).getName());
                lines.get(5).update("&aKills");
                lines.get(4).update("&7" + game.getKills(player));
                lines.get(2).update("&aStatus");
                lines.get(1).update("&7Finished");

                break;
        }
    }

    // Sends an updated scoreboard to all players
    public void sendToPlayer() {
        updateBoard();
        player.setScoreboard(board);
    }

    public Player getPlayer() {
        return player;
    }
}
