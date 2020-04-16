package com.Emile2250.SimpleUHC.Util;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {

    public static void fixPlayer(Player p) {
        p.setHealth(20); // Makes sure they're max health
        p.setFoodLevel(20); // Makes sure they're max food
        p.setSaturation(20); // Sets saturation to max
        p.setExp(0); // Resets experience

        p.getInventory().clear(); // Clears inventory
        clearArmor(p); // Clears armor
        p.updateInventory(); // Updates inventory for the player based off our changes

        p.setGameMode(GameMode.SURVIVAL); // Forces them to survival
        p.setFlying(false); // Disables their fly
        p.setAllowFlight(false); // Makes it so they're not allowed to fly

        // Removes all potion effects
        for (PotionEffect potion : p.getActivePotionEffects()) {
            p.removePotionEffect(potion.getType());
        }
    }

    private static void clearArmor(Player p) {
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
    }

}
