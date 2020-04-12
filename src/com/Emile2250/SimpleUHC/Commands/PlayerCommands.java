package com.Emile2250.SimpleUHC.Commands;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.UHC.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) { // Makes sure it was a player instead of the console.
            Player player = (Player) sender; // Sets a variable for future use
            if (command.getLabel().equalsIgnoreCase("uhc")) { // Makes sure the label is UHC
                if (args.length == 1) { // Makes sure that they only typed one argument
                    if (args[0].equalsIgnoreCase("join")) { // Checks if they are trying to queue

                        for (Game game : SimpleUHC.getGames()) {
                            if (game.getPlayers().contains(player)) { // Makes sure they're not currently in a queue
                                return false;
                            }
                        }

                        for (Game game : SimpleUHC.getGames()) { // Runs through the list of available game
                            if ((game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING) && game.numPlayers() < game.getMaxPlayers()) { // Finds the first available game to queue in
                                game.addPlayer(player); // Adds player to the game to queue in
                                return false; // Stops the method call as we did what we needed.
                            }
                        }

                        // If they get to this point there are NO available games to join so we will create a new game
                        Game game = new Game(false); // TODO add team implementation
                        game.addPlayer(player); // Adds the player to the game
                        SimpleUHC.getGames().add(game); // Adds the game to the game list
                    }
                }
            }
        }

        return false;
    }
}
