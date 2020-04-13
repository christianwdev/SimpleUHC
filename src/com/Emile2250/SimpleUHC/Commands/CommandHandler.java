package com.Emile2250.SimpleUHC.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 1) {
            switch(args[0]) {
                case "join":
                    return PlayerCommands.onCommand(sender, args);
                case "start":
                    return AdminCommands.onCommand(sender, args);
            }
        }

        return false;
    }
}
