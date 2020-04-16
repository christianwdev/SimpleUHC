package com.Emile2250.SimpleUHC.Stats;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.Util.ConfigUtil;
import org.bukkit.entity.Player;

public class StatsHandler {

    public static void createPlayer(Player player) {
        ConfigUtil config = SimpleUHC.getInstance().getStats();

        if (!config.isSection("Users." + player.getUniqueId())) {
            config.set("Users." + player.getUniqueId() + ".kills", 0);
            config.set("Users." + player.getUniqueId() + ".wins", 0);
            config.set("Users." + player.getUniqueId() + ".games", 0);
            config.set("Users." + player.getUniqueId() + ".deaths", 0);
        }
    }

    public static void addKill(Player player) {
        ConfigUtil config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.has("Users." + player.getUniqueId() + ".kills"))
            current = config.getInt("Users." + player.getUniqueId() + ".kills");

        config.set("Users." + player.getUniqueId() + ".kills", current + 1);
    }

    public static void addWin(Player player) {
        ConfigUtil config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.has("Users." + player.getUniqueId() + ".wins"))
            current = config.getInt("Users." + player.getUniqueId() + ".wins");

        config.set("Users." + player.getUniqueId() + ".wins", current + 1);
    }

    public static void addGame(Player player) {
        ConfigUtil config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.has("Users." + player.getUniqueId() + ".games"))
            current = config.getInt("Users." + player.getUniqueId() + ".games");

        config.set("Users." + player.getUniqueId() + ".games", current + 1);
    }

    public static void addDeath(Player player) {
        ConfigUtil config = SimpleUHC.getInstance().getStats();
        int current = 0;

        if (config.has("Users." + player.getUniqueId() + ".deaths"))
            current = config.getInt("Users." + player.getUniqueId() + ".deaths");

        config.set("Users." + player.getUniqueId() + ".deaths", current + 1);
    }

}
