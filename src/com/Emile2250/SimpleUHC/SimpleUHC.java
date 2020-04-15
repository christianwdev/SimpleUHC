package com.Emile2250.SimpleUHC;

import com.Emile2250.SimpleUHC.Commands.CommandHandler;
import com.Emile2250.SimpleUHC.Listeners.*;
import com.Emile2250.SimpleUHC.UHC.Game;
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

    private File settingsFile;
    private FileConfiguration settingsConfig;
    private File statsFile;
    private FileConfiguration statsConfig;
    private ArrayList<Game> games;
    private static SimpleUHC instance;

    public void onEnable() {

        // Variable setup

        createConfigs();
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

    public FileConfiguration getSettings() {
        return settingsConfig;
    }

    public FileConfiguration getStats() {
        return statsConfig;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public static SimpleUHC getInstance() {
        return instance;
    }

    // Other stuff?

    public void saveSettings() {
        try {
            settingsConfig.save(settingsFile);
        } catch (IOException e) {
            System.out.println("Uh oh! You had an issue saving your settings configuration.");
            e.printStackTrace();
        }
    }

    public void saveStats() {
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            System.out.println("Uh oh! You had an issue saving your settings configuration.");
            e.printStackTrace();
        }
    }

    private void createConfigs() {
        settingsFile = new File(getDataFolder(), "settings.yml"); // Initializes the settingsFile
        statsFile = new File(getDataFolder(), "stats.yml");

        if (!settingsFile.exists()) { // Checks if the file exists
            settingsFile.getParentFile().mkdirs(); // Creates the directories if they don't exist
            saveResource("settings.yml", false); // Saves the settings.yml file without replacing it.
        }

        if (!statsFile.exists()) { // Makes sure it doesnt exist
            statsFile.getParentFile().mkdirs(); // Creates dir
            saveResource("stats.yml", false); // Saves file
        }

        settingsConfig = new YamlConfiguration(); // Initializes the base config object.
        statsConfig = new YamlConfiguration(); // Creates object
        try {
            settingsConfig.load(settingsFile); // Actually tries to load the configuration
            statsConfig.load(statsFile); // Loads config
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Uh oh! Your configuration loaded incorrectly.");
            e.printStackTrace();
        }
    }

    private void createGames() {
        if (settingsConfig.isConfigurationSection("Games")) {
            Set<String> gameNames = settingsConfig.getConfigurationSection("Games").getKeys(false);
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
