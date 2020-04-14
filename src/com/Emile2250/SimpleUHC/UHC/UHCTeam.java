package com.Emile2250.SimpleUHC.UHC;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class UHCTeam {

    private ArrayList<Player> members;

    public UHCTeam(Player p) {
        members = new ArrayList<>();
        members.add(p);
    }

    public void addMember(Player p) {
        members.add(p);
    }

    public void removeMember(Player p) {
        members.remove(p);
    }

    public ArrayList<Player> getMembers() {
        return members;
    }
}
