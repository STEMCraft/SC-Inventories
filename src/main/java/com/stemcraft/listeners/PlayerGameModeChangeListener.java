package com.stemcraft.listeners;

import com.stemcraft.Inventories;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerGameModeChangeListener implements Listener {
    private final Inventories plugin;

    public PlayerGameModeChangeListener(Inventories instance) {
        plugin = instance;
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        GameMode fromGameMode = player.getGameMode();
        GameMode toGameMode = event.getNewGameMode();

        if(!fromGameMode.equals(toGameMode)) {
            plugin.savePlayerState(player);
            plugin.loadPlayerState(player, player.getWorld(), toGameMode);
        }
    }
}
