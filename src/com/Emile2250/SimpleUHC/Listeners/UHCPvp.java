package com.Emile2250.SimpleUHC.Listeners;

import com.Emile2250.SimpleUHC.SimpleUHC;
import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.UHC.UHCTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class UHCPvp implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) { // Makes sure it was two players attacking one another
            Player hit = (Player) e.getEntity();
            Player attacker = (Player) e.getDamager();

            for (Game game : SimpleUHC.getInstance().getGames()) { // Goes through all games
                if (game.getPlayers().contains(hit)) { // Makes sure that user is in that game
                    if (game.isTeamGame()) { // Makes sure that its a team game
                        for (UHCTeam team : game.getTeams()) { // Checks all teams in that game
                            if (team.getMembers().contains(hit) && team.getMembers().contains(attacker)) { // Makes sure that the two players are on the same team
                                e.setCancelled(true); // Sets the damage event to null to prevent team damage
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
