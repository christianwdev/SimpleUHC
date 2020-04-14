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

        for (int i = 0; i < 15; i++) {
            lines.add(new ScoreboardLine(objective, "", i));
        }
    }

    // Updates the players stats
    private void updateBoard() {
        // Changes the scoreboard for each state, as they all display something a little different

        String status = "";
        if (game.getState() == GameState.STARTING) {
            if (game.getCountdown() > 0) {
                status = "&a" + game.getCountdown() + "s";
            } else {
                status = "&aStarting now";
            }
        } else if (game.getState() == GameState.LOBBY) {
            status = "&aWaiting for players";
        }

        switch (game.getState()) {
            case LOBBY:
            case STARTING:

                if (game.isTeamGame()) {
                    lines.get(7).update(" ");
                    lines.get(6).update("&fQueued: &a" + game.numPlayers());
                    lines.get(5).update("    ");
                    lines.get(4).update("&fTeammates: &a" + game.getTeam(player).getMembers().size());
                } else {
                    lines.get(5).update(" ");
                    lines.get(4).update("&fQueued: &a" + game.numPlayers());
                }

                lines.get(3).update("  ");
                lines.get(2).update("&fStatus");
                lines.get(1).update(status);
                lines.get(0).update("   ");

                break;
            case GRACE:
            case PVP:
            case FINISHED:

                if (game.isTeamGame()) {

                    lines.get(11).update(" ");
                    lines.get(10).update("&fAlive: &a" + game.numPlayers());
                    lines.get(9).update("  ");
                    lines.get(8).update("&fTeams: &a" + game.getTeams().size());
                    lines.get(7).update("   ");
                    lines.get(6).update("&fTeammates: &a" + game.getTeam(player).getMembers().size());
                    lines.get(5).update("    ");
                    lines.get(4).update("&fKills: &a" + game.getKills(player));
                    lines.get(3).update("     ");
                    lines.get(2).update("&aBorder");
                    lines.get(1).update("&7(" + (-game.getBorderSize() / 2) + ", " + (game.getBorderSize() / 2) + ")");
                    lines.get(0).update("     ");

                } else {

                    lines.get(8).update(" ");
                    lines.get(7).update("&fAlive: &a" + game.numPlayers());
                    lines.get(6).update("  ");
                    lines.get(5).update("    ");
                    lines.get(4).update("&fKills: &a" + game.getKills(player));
                    lines.get(3).update("     ");
                    lines.get(2).update("&aBorder");
                    lines.get(1).update("&7(" + (-game.getBorderSize() / 2) + ", " + (game.getBorderSize() / 2) + ")");
                    lines.get(0).update("     ");

                }

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
