package com.Emile2250.SimpleUHC.Commands;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.UHC.GameState;
import com.Emile2250.SimpleUHC.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands {


    public static boolean onCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) { // Makes sure it was a player instead of the console.
            Player player = (Player) sender; // Sets a variable for future use
            if (args[0].equalsIgnoreCase("start")) { // Checks if they are trying to queue
                if (player.hasPermission("uhc.forcestart")) { // Checks for permission to start.
                    for (Game game : SimpleUHC.getGames()) { // Runs through the list of available game
                        if (game.getPlayers().contains(player)) { // Makes sure they're in a game
                            if ((game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING) && game.getPlayers().size() > 1) { // Makes sure they're not already in a running game and has 2 players
                                ChatUtil.sendMessage(player, " &a&lUHC > &7Force starting &b" + game.getGameName());
                                game.forceStart(); // Force starts the game
                                return false; // Stops the method call as we did what we needed.
                            } else {
                                ChatUtil.sendMessage(player, " &a&lUHC > &7Unable to force start &b" + game.getGameName());
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

}
