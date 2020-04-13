package com.Emile2250.SimpleUHC.Commands;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.UHC.GameState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands {


    public static boolean onCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) { // Makes sure it was a player instead of the console.
            Player player = (Player) sender; // Sets a variable for future use
            if (args[0].equalsIgnoreCase("start")) { // Checks if they are trying to queue
                if (player.hasPermission("uhc.forcestart")) {
                    for (Game game : SimpleUHC.getGames()) { // Runs through the list of available game
                        if (game.getPlayers().contains(player)) {
                            if (game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING) {
                                game.forceStart();
                                return false; // Stops the method call as we did what we needed.
                            } else {
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
