package com.stemcraft.listener;

import com.stemcraft.Inventories;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final Inventories plugin;

    public PlayerQuitListener(Inventories instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.savePlayerState(player);
    }
}
