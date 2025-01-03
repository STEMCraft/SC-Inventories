package com.stemcraft.listener;

import com.stemcraft.Inventories;
import com.stemcraft.event.WorldDeleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldDeleteListener implements Listener {
    private final Inventories plugin;

    public WorldDeleteListener(Inventories instance) {
        plugin = instance;
    }

    @EventHandler
    public void onWorldDelete(WorldDeleteEvent event) {
        String worldName = event.getWorldName();

        plugin.deleteWorldData(worldName);
    }
}
