package com.stemcraft.listener;

import com.stemcraft.Inventories;
import com.stemcraft.event.WorldDeleteEvent;
import com.stemcraft.util.SCWorld;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldDeleteListener implements Listener {
    private final Inventories plugin;

    public WorldDeleteListener(Inventories instance) {
        plugin = instance;
    }

    @EventHandler
    public void onWorldDelete(WorldDeleteEvent event) {
        String worldName = event.getWorldName();

    }
}
