package com.Emile2250.SimpleUHC.Commands;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.UHC.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands {

    public static boolean onCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) { // Makes sure it was a player instead of the console.
            Player player = (Player) sender; // Sets a variable for future use
            if (args[0].equalsIgnoreCase("join")) { // Checks if they are trying to queue

                for (Game game : SimpleUHC.getGames()) {
                    if (game.getPlayers().contains(player)) { // Makes sure they're not currently in a queue
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &a&lUHC > &7You're already in &b" + game.getGameName()));
                        return false;
                    }
                }

                for (Game game : SimpleUHC.getGames()) { // Runs through the list of available game
                    if ((game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING) && game.numPlayers() < game.getMaxPlayers()) { // Finds the first available game to queue in
                        game.addPlayer(player); // Adds player to the game to queue in
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &a&lUHC > &7You successfully joined &b" + game.getGameName()));
                        return false; // Stops the method call as we did what we needed.
                    }
                }

                // If they get to this point there are NO available games to join so we will create a new game
                Game game = new Game(false); // TODO add team implementation
                game.addPlayer(player); // Adds the player to the game
                SimpleUHC.getGames().add(game); // Adds the game to the game list
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &a&lUHC > &7You successfully joined &b" + game.getGameName()));

            } else if (args[0].equalsIgnoreCase("quit")) {
                for (Game game : SimpleUHC.getGames()) {
                    if (game.getPlayers().contains(player)) { // Makes sure they're not currently in a queue
                        if (game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING) {
                            game.removePlayer(player);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &a&lUHC > &7You successfully left &b" + game.getGameName()));
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &a&lUHC > &7You cannot leave mid game."));
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }
}
