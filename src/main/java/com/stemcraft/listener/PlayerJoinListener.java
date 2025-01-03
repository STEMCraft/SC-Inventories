package com.stemcraft.listener;

import com.stemcraft.Inventories;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final Inventories plugin;

    public PlayerJoinListener(Inventories instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.loadPlayerState(player, player.getWorld(), player.getGameMode());
    }
}
