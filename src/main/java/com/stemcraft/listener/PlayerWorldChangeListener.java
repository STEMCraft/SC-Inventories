package com.stemcraft.listener;

import com.stemcraft.Inventories;
import com.stemcraft.util.SCWorld;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerWorldChangeListener implements Listener {
    private final Inventories plugin;

    public PlayerWorldChangeListener(Inventories instance) {
        plugin = instance;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World fromWorld = event.getFrom();
        World toWorld = player.getWorld();
        GameMode gameMode = player.getGameMode();

        if (!plugin.areSharedWorlds(fromWorld.getName(), toWorld.getName()) && !SCWorld.sameRealm(fromWorld, toWorld)) {
            plugin.savePlayerState(player, fromWorld, gameMode);
            plugin.loadPlayerState(player, toWorld, gameMode);
        }
    }
}
