package com.Emile2250.SimpleUHC.Util;

import com.Emile2250.SimpleUHC.UHC.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {

    public static void broadcastGame(Game g, String msg) {
        for (Player p : g.getPlayers()) {
            p.sendMessage(color(msg));
        }
    }

    public static void broadcast(String msg) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(color(msg));
        }
    }

    public static void sendMessage(Player p, String msg) {
        p.sendMessage(color(msg));
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
