package com.Emile2250.SimpleUHC.UHC;

import org.bukkit.entity.Player;

public class UHCPlayer {

    private Player player;
    private UHCTeam team;

    public UHCPlayer(Player player) {
        this.player = player;
    }

    // Setters

    public void setTeam(UHCTeam team) {
        this.team = team;
    }

    // Getters

    public Player getPlayer() {
        return player;
    }

    public UHCTeam getTeam() {
        return team;
    }
}
