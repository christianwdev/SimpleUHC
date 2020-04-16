package com.Emile2250.SimpleUHC;

import com.Emile2250.SimpleUHC.Commands.CommandHandler;
import com.Emile2250.SimpleUHC.Listeners.*;
import com.Emile2250.SimpleUHC.UHC.Game;
import com.Emile2250.SimpleUHC.Util.ConfigUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class SimpleUHC extends JavaPlugin {

    private ConfigUtil settings;
    private ConfigUtil stats;
    private ArrayList<Game> games;
    private static SimpleUHC instance;

    public void onEnable() {

        // Variable setup

        // Creates configs
        settings = new ConfigUtil("settings.yml");
        stats = new ConfigUtil("stats.yml");

        games = new ArrayList<>();
        instance = this;
        createGames();

        // Commands

        getCommand("uhc").setExecutor(new CommandHandler()); // Hub to navigate sub commands to appropriate class

        // Events

        Bukkit.getPluginManager().registerEvents(new LeaveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCDeath(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPvp(), this);
        Bukkit.getPluginManager().registerEvents(new WorldCreation(), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
    }

    // Getters

    public ConfigUtil getSettings() {
        return settings;
    }

    public ConfigUtil getStats() {
        return stats;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public static SimpleUHC getInstance() {
        return instance;
    }

    // Other stuff?

    private void createGames() {
        if (stats.isSection("Games")) {
            Set<String> gameNames = stats.getSection("Games").getKeys(false);
            deleteWorlds(gameNames);

            for (String game : gameNames) {
                games.add(new Game(game));
            }

        } else {
            System.out.println("Uh oh! There are no games to load, maybe try creating some");
        }
    }

    private void deleteWorlds(Set<String> worlds) {
        String[] directories = this.getServer().getWorldContainer().list(); // Grabs a list of all files in the server

        if (directories != null) {
            for (String folder : directories) { // Loops through all the files
                for (String world : worlds) {
                    if (folder.contains(world)) { // Checks if we had any left over UHC crashes in case of shutdown mid game
                        try {
                            FileUtils.deleteDirectory(new File(this.getServer().getWorldContainer(), folder)); // Tries to delete the world.
                        } catch (IOException e) {
                            System.out.println("Oh no! We had an issue deleting left over worlds");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
