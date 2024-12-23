package com.stemcraft;

import com.stemcraft.listeners.PlayerGameModeChangeListener;
import com.stemcraft.listeners.PlayerQuitListener;
import com.stemcraft.listeners.PlayerWorldChangeListener;
import com.stemcraft.storage.PlayerDataStorage;
import com.stemcraft.storage.YamlPlayerDataStorage;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class Inventories extends JavaPlugin implements Listener {
    private static Inventories instance;
    private PlayerDataStorage storage;

    @Override
    public void onEnable() {
        instance = this;
        storage = new YamlPlayerDataStorage(instance);

        getServer().getPluginManager().registerEvents(new PlayerWorldChangeListener(instance), this);
        getServer().getPluginManager().registerEvents(new PlayerGameModeChangeListener(instance), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(instance), this);
    }

    public Inventories getInstance() {
        return instance;
    }

    /**
     * Are worlds in the same dimension?
     *
     * @param worldA World A to test
     * @param worldB World B to test
     * @return If both world names are in the same group of worlds
     */
    public boolean worldsInSameDimension(World worldA, World worldB) {
        String worldAName = worldA.getName().toLowerCase();
        String worldBName = worldB.getName().toLowerCase();

        if (worldAName.equals(worldBName)) {
            return true;
        }

        return worldAName.replace("_nether", "").replace("_the_end", "").equals(
                worldBName.replace("_nether", "").replace("_the_end", "")
        );
    }

    public void savePlayerState(Player player) {
        savePlayerState(player, player.getWorld(), player.getGameMode());
    }

    /**
     * Save a player profile from disk.
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
     * Load player profile from disk.
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
     * Checks if a players inventory exists
     *
     * @param player   The player to load.
     * @param world    The world to load.
     * @param gameMode The game mode to load.
     * @return If the player inventory exists on disk.
     */
    public boolean playerStateExists(Player player, World world, GameMode gameMode) {
        return storage.exists(player, world, gameMode);
    }
}
