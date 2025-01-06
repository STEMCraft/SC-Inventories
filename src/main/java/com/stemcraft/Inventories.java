package com.stemcraft;

import com.stemcraft.command.ClearInventory;
import com.stemcraft.listener.*;
import com.stemcraft.storage.PlayerDataStorage;
import com.stemcraft.storage.YamlPlayerDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Inventories extends STEMCraftPlugin {
    private static Inventories instance;
    private PlayerDataStorage storage;
    private final List<List<String>> sharedWorlds = new ArrayList<>();


    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;

        File configFile = new File(this.getDataFolder(), "config.yml");
        if(configFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            // Load shared inventory worlds
            ConfigurationSection shared = config.getConfigurationSection("shared");
            if(shared != null) {
                shared.getKeys(false).forEach(key -> {
                    List<String> worlds = shared.getStringList(key);
                    if(worlds.size() < 2) {
                        getLogger().warning("Shared inventory worlds must have at least 2 worlds.");
                    } else {
                        sharedWorlds.add(worlds);
                    }
                });
            }
        }

        // Currently this plugin only supports YAML storage
        storage = new YamlPlayerDataStorage(instance);

        registerEvents(new PlayerWorldChangeListener(instance));
        registerEvents(new PlayerGameModeChangeListener(instance));
        registerEvents(new PlayerQuitListener(instance));
        registerEvents(new WorldDeleteListener(instance));
        registerEvents(new PlayerJoinListener(instance));

        registerCommand(new ClearInventory(), "clearinventory");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        for(Player player : Bukkit.getOnlinePlayers()) {
            savePlayerState(player);
        }
    }

    public Inventories getInstance() {
        return instance;
    }

    /**
     * Returns if the two worlds share inventories
     * @param source The source world name
     * @param target The target world name
     * @return If the worlds share inventories
     */
    public boolean areSharedWorlds(String source, String target) {
        for(List<String> worlds : sharedWorlds) {
            if(worlds.contains(source) && worlds.contains(target)) {
                return true;
            }
        }

        return false;
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
        PlayerState state = new PlayerState(player, world, gameMode);
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

    /**
     * Delete world data from config as world is deleted.
     *
     * @param worldName The world name.
     */
    public void deleteWorldData(String worldName) {
        storage.delete(worldName);
    }
}
