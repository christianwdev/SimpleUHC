package com.Emile2250.SimpleUHC.Commands;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.UHC.GameState;
import com.Emile2250.SimpleUHC.Util.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands {

    public static boolean onCommand(CommandSender sender, String[] args) {

        if (sender instanceof Player) { // Makes sure it was a player instead of the console.
            Player player = (Player) sender; // Sets a variable for future use
            switch (args[0]) {
                case "join":

                    for (Game game : SimpleUHC.getGames()) {
                        if (game.getPlayers().contains(player)) { // Makes sure they're not currently in a queue
                            ChatUtil.sendMessage(player, " &a&lUHC > &7You're already in &b" + game.getGameName());
                            return false;
                        }
                    }

                    boolean teamGame = (args.length == 2 && args[1].equalsIgnoreCase("team"));
                    boolean isSpecific = (args.length == 2) && !teamGame;

                    for (Game game : SimpleUHC.getGames()) { // Runs through the list of available game
                        if (game.isJoinable() && ((teamGame && game.isTeamGame()) || (!isSpecific && !teamGame) || game.getGameName().equalsIgnoreCase(args[1]))) { // Finds the first available game to queue in
                            game.addPlayer(player); // Adds player to the game to queue in
                            ChatUtil.sendMessage(player, " &a&lUHC > &7You successfully joined &b" + game.getGameName());
                            return false; // Stops the method call as we did what we needed.
                        }
                    }

                    // If they get to this point there are NO available games to join so we will create a new game
                    if (isSpecific)
                        ChatUtil.sendMessage(player, " &a&lUHC > &7There is no games with that name, please try using /uhc games.");
                    else
                        ChatUtil.sendMessage(player, " &a&lUHC > &7There are no games available to join at this time, please wait.");

                    break;
                case "quit":
                case "leave":

                    for (Game game : SimpleUHC.getGames()) {
                        if (game.getPlayers().contains(player)) { // Makes sure they're not currently in a queue
                            game.removePlayer(player);
                            ChatUtil.sendMessage(player, " &a&lUHC > &7You successfully left &b" + game.getGameName());
                        }
                    }

                    break;
                case "games":
                case "list":

                    ChatUtil.sendMessage(player, " ");
                    if (SimpleUHC.getGames().size() > 0) {
                        String gameList = "";
                        for (Game game : SimpleUHC.getGames()) {
                            if (game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING)
                                gameList += "&a" + game.getGameName() + " ";
                            else
                                gameList += "&c" + game.getGameName() + " ";
                        }
                        ChatUtil.sendMessage(player, "&a&lUHC > " + gameList);
                    } else {
                        ChatUtil.sendMessage(player, " &a&lUHC > &7There are no games, go create some.");
                    }
                    ChatUtil.sendMessage(player, " ");

                    break;
            }
        }

        return false;
    }
}
