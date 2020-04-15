package com.Emile2250.SimpleUHC.Stats;

import com.Emile2250.SimpleUHC.SimpleUHC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class StatsHandler {

    public static void createPlayer(Player player) {
        FileConfiguration config = SimpleUHC.getInstance().getStats();

        if (!config.isConfigurationSection("Users." + player.getUniqueId())) {
            config.set("Users." + player.getUniqueId() + ".kills", 0);
            config.set("Users." + player.getUniqueId() + ".wins", 0);
            config.set("Users." + player.getUniqueId() + ".games", 0);
            config.set("Users." + player.getUniqueId() + ".deaths", 0);
        }

        SimpleUHC.getInstance().saveStats();
    }

    public static void addKill(Player player) {
        FileConfiguration config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.contains("Users." + player.getUniqueId() + ".kills"))
            current = config.getInt("Users." + player.getUniqueId() + ".kills");

        config.set("Users." + player.getUniqueId() + ".kills", current + 1);
        SimpleUHC.getInstance().saveStats();
    }

    public static void addWin(Player player) {
        FileConfiguration config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.contains("Users." + player.getUniqueId() + ".wins"))
            current = config.getInt("Users." + player.getUniqueId() + ".wins");

        config.set("Users." + player.getUniqueId() + ".wins", current + 1);
        SimpleUHC.getInstance().saveStats();
    }

    public static void addGame(Player player) {
        FileConfiguration config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.contains("Users." + player.getUniqueId() + ".games"))
            current = config.getInt("Users." + player.getUniqueId() + ".games");

        config.set("Users." + player.getUniqueId() + ".games", current + 1);
        SimpleUHC.getInstance().saveStats();
    }

    public static void addDeath(Player player) {
        FileConfiguration config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.contains("Users." + player.getUniqueId() + ".deaths"))
            current = config.getInt("Users." + player.getUniqueId() + ".deaths");

        config.set("Users." + player.getUniqueId() + ".deaths", current + 1);
        SimpleUHC.getInstance().saveStats();
    }

}
