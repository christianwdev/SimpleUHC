package com.Emile2250.SimpleUHC.Listeners;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.UHC.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        for (Game game : SimpleUHC.getGames()) {
            if (game.getPlayers().contains(player)) { // Checks if the player quit in a UHC game
                game.removePlayer(player); // Removes the player from the game.
                return; // Since we handled the death there is no need to continue the code
            }
        }
    }

}
