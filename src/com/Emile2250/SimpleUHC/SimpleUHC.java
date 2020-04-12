package com.Emile2250.SimpleUHC;

import com.Emile2250.SimpleUHC.UHC.Game;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SimpleUHC extends JavaPlugin {

    private static File settingsFile;
    private static FileConfiguration settingsConfig;
    private static ArrayList<Game> games;
    private static SimpleUHC instance;

    public void onEnable() {
        createSettingsConfig();
        games = new ArrayList<>();
    }

    public static FileConfiguration getSettings() {
        return settingsConfig;
    }

    public static ArrayList<Game> getGames() {
        return games;
    }

    public static SimpleUHC getInstance() {
        return instance;
    }

    public static void saveSettings() {
        try {
            settingsConfig.save(settingsFile);
        } catch (IOException e) {
            System.out.println("Uh oh! You had an issue saving your settings configuration.");
            e.printStackTrace();
        }
    }

    private void createSettingsConfig() {
        settingsFile = new File(getDataFolder(), "settings.yml"); // Initializes the settingsFile

        if (!settingsFile.exists()) { // Checks if the file exists
            settingsFile.getParentFile().mkdirs(); // Creates the directories if they don't exist
            saveResource("settings.yml", false); // Saves the settings.yml file without replacing it.
        }

        settingsConfig = new YamlConfiguration(); // Initializes the base config object.
        try {
            settingsConfig.load(settingsFile); // Actually tries to load the configuration
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Uh oh! Your configuration loaded incorrectly.");
            e.printStackTrace();
        }
    }

}
