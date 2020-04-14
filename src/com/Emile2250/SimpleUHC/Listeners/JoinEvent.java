package com.Emile2250.SimpleUHC.Listeners;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.Util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getWorld().getName().contains("UHC-")) {
            String name = e.getPlayer().getWorld().getName();
            Player player = e.getPlayer();

            World world = Bukkit.getWorld("world");
            if (SimpleUHC.getSettings().isString("main-world") && Bukkit.getWorld(SimpleUHC.getSettings().getString("main-world")) != null)
                world = Bukkit.getWorld(SimpleUHC.getSettings().getString("main-world")); // Sets it to preferred main world if it is in the config and is a world

            player.teleport(world.getSpawnLocation()); // Teleports any existing players to the main world to prepare for world deletion
            ChatUtil.sendMessage(player, " &a&lUHC > &7We removed you from &b" + name);
        }
    }

}
