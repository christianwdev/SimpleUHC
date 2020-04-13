package com.Emile2250.SimpleUHC.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldCreation implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void worldInit(WorldInitEvent e) {
        if (e.getWorld().getName().contains("UHC-")) {
            e.getWorld().setKeepSpawnInMemory(false); // Prevents spawn chunks from loading to prevent massive lag spike in main thread.
            e.getWorld().setAutoSave(false);
        }
    }

}
