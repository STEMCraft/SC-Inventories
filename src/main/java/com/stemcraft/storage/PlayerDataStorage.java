package com.stemcraft.storage;

import com.stemcraft.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface PlayerDataStorage {

    /**
     * Checks if a players state exists in storage
     *
     * @param player   The player to load.
     * @param world    The world to load.
     * @param gameMode The game mode to load.
     * @return If the player inventory exists in storage.
     */
    boolean exists(Player player, World world, GameMode gameMode);

    /**
     * Load player state from storage.
     *
     * @param player   The player to load.
     * @param world    The world to load.
     * @param gameMode The game mode to load.
     * @return The player state or null.
     */
    PlayerState load(Player player, World world, GameMode gameMode);

    /**
     * Save a player state to storage.
     *
     * @param state   The player state to save
     */
    void save(PlayerState state);
}
