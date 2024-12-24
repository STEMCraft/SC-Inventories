package com.stemcraft;

import com.stemcraft.commands.ClearInventory;
import com.stemcraft.common.STEMCraftPlugin;
import com.stemcraft.listeners.PlayerGameModeChangeListener;
import com.stemcraft.listeners.PlayerQuitListener;
import com.stemcraft.listeners.PlayerWorldChangeListener;
import com.stemcraft.storage.PlayerDataStorage;
import com.stemcraft.storage.YamlPlayerDataStorage;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class Inventories extends STEMCraftPlugin {
    private static Inventories instance;
    private PlayerDataStorage storage;

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;

        // Currently this plugin only supports YAML storage
        storage = new YamlPlayerDataStorage(instance);

        registerEvents(new PlayerWorldChangeListener(instance));
        registerEvents(new PlayerGameModeChangeListener(instance));
        registerEvents(new PlayerQuitListener(instance));

        registerCommand(new ClearInventory(instance));
    }

    public Inventories getInstance() {
        return instance;
    }

    /**
     * Save a player state to storage
     * @param player The player to save
     */
    public void savePlayerState(Player player) {
        savePlayerState(player, player.getWorld(), player.getGameMode());
    }

    /**
     * Save a player state to storage.
     *
     * @param player   The player to save
     * @param world    The world to save
     * @param gameMode The player game mode to save
     */
    public void savePlayerState(Player player, World world, GameMode gameMode) {
        PlayerState state = new PlayerState(player);
        storage.save(state);
    }

    /**
     * Load player state from storage.
     *
     * @param player   The player to load.
     * @param world    The world to load.
     * @param gameMode The game mode to load.
     */
    public void loadPlayerState(Player player, World world, GameMode gameMode) {
        PlayerState profile = storage.load(player, world, gameMode);
        profile.set();
    }

    /**
     * Checks if a players state exists in storage
     *
     * @param player   The player to load.
     * @param world    The world to load.
     * @param gameMode The game mode to load.
     * @return If the player inventory exists in storage.
     */
    public boolean playerStateExists(Player player, World world, GameMode gameMode) {
        return storage.exists(player, world, gameMode);
    }
}
