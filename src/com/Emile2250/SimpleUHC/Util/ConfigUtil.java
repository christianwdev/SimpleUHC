package com.Emile2250.SimpleUHC.Util;

import com.Emile2250.SimpleUHC.SimpleUHC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ConfigUtil {

    private File file;
    private FileConfiguration config;

    public ConfigUtil(String fileName) {
        try {
            file = new File(SimpleUHC.getInstance().getDataFolder(), fileName); // Creates a new file object.

            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Makes folders / directories if needed.
                SimpleUHC.getInstance().saveResource(fileName, false); // Saves .yml in folder
            }

            config = new YamlConfiguration(); // Creates YAML object
            config.load(file); // Loads YAML config

        } catch (IOException | InvalidConfigurationException e) {

            System.out.println("We had an error setting up your " + fileName + " configuration.");

            if (e instanceof InvalidConfigurationException) {

                if (file.renameTo(new File(SimpleUHC.getInstance().getDataFolder(), "broken_" + fileName))) { // Tries to rename their file
                    System.out.println("We renamed your configuration to broken_" + fileName);
                    SimpleUHC.getInstance().saveResource(fileName, false); // Creates a new default configuration for them
                } else {
                    System.out.println("We had an issue renaming your configuration!");
                }
            }
            e.printStackTrace(); // Prints the error
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean has(String path) {
        return config.contains(path); // Makes sure the path exists
    }

    public Object get(String path) {
        return config.get(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean isInt(String path) {
        return config.isInt(path);
    }

    public boolean getBool(String path) {
        return config.getBoolean(path);
    }

    public boolean isBool(String path) {
        return config.isBoolean(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public boolean isDouble(String path) {
        return config.isDouble(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public boolean isString(String path) {
        return config.isString(path);
    }

    public List<?> getList(String path) {
        return config.getList(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public boolean isList(String path) {
        return config.isString(path);
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public boolean isSection(String path) {
        return config.isConfigurationSection(path);
    }

    public Set<String> getSectionSet(String path, boolean hasKeys) {
        return getSection(path).getKeys(hasKeys);
    }

    // Makes it so setting a value both sets it and saves it in the configuration instead of saving it manually.
    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    private void save() {
        try {
            config.save(file); // Just saves the config.
        } catch (IOException e) {
            System.out.println("We had an issue saving your " + config.getName() + " configuration.");
            e.printStackTrace();
        }
    }
}
