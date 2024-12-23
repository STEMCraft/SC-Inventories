package com.stemcraft;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Inventories extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        getLogger().log(Level.INFO, "onWorldChange");

        Player player = event.getPlayer();
        String gameMode = player.getGameMode().name();
        String fromWorldName = event.getFrom().getName().toLowerCase();
        String toWorldName = player.getWorld().getName().toLowerCase();

        if(!areWorldsInSameGroup(fromWorldName, toWorldName)) {
            saveProfile(player, fromWorldName, gameMode);
            loadProfile(player, toWorldName, gameMode);
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        getLogger().log(Level.INFO, "onGameModeChange");

        Player player = event.getPlayer();
        String fromGameMode = player.getGameMode().toString();
        String toGameMode = event.getNewGameMode().name();
        String worldName = player.getWorld().getName().toLowerCase();

        saveProfile(player, worldName, fromGameMode);
        loadProfile(player, worldName, toGameMode);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        getLogger().log(Level.INFO, "onPlayerQuit");
        Player player = event.getPlayer();
        saveProfile(player, player.getWorld().getName().toLowerCase(), player.getGameMode().toString().toLowerCase());
    }

    private boolean areWorldsInSameGroup(String from, String to) {
        from = from.toLowerCase();
        to = to.toLowerCase();

        if (from.equals(to)) {
            return true;
        }

        return from.replace("_nether", "").replace("_the_end", "").equals(
            to.replace("_nether", "").replace("_the_end", "")
        );
    }
    private void saveProfile(Player player, String world, String gameMode) {
        world = world.toLowerCase();
        gameMode = gameMode.toLowerCase();

        File playerFile = new File(getDataFolder() + "/worlds/" + world, player.getUniqueId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                config.set(gameMode + ".inventory." + i, contents[i]);
            } else {
                config.set(gameMode + ".inventory." + i, null);
            }
        }

        ItemStack[] enderChest = player.getEnderChest().getContents();

        for (int i = 0; i < enderChest.length; i++) {
            if (contents[i] != null) {
                config.set(gameMode + ".ender_chest." + i, enderChest[i]);
            } else {
                config.set(gameMode + ".ender_chest." + i, null);
            }
        }

        List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());
        for (int i = 0; i < effects.size(); i++) {
            PotionEffect effect = effects.get(i);
            String path = gameMode + ".effects." + i;
            config.set(path + ".type", effect.getType().getName());
            config.set(path + ".duration", effect.getDuration());
            config.set(path + ".amplifier", effect.getAmplifier());
            config.set(path + ".ambient", effect.isAmbient());
            config.set(path + ".particles", effect.hasParticles());
            config.set(path + ".icon", effect.hasIcon());
        }

        config.set(gameMode + ".selected_slot", player.getInventory().getHeldItemSlot());
        config.set(gameMode + ".xp", player.getExp());
        config.set(gameMode + ".xp_level", player.getLevel());
        config.set(gameMode + ".air", player.getRemainingAir());
        config.set(gameMode + ".air_max", player.getMaximumAir());
        config.set(gameMode + ".fire", player.getFireTicks());
        config.set(gameMode + ".fall_distance", player.getFallDistance());
        config.set(gameMode + ".health", player.getHealth());
        config.set(gameMode + ".xp_total", player.getTotalExperience());
        config.set(gameMode + ".starvation", player.getStarvationRate());
        config.set(gameMode + ".saturation", player.getSaturation());
        config.set(gameMode + ".absorption", player.getAbsorptionAmount());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            getLogger().warning("Failed to save inventory for player " + player.getName());
            e.printStackTrace();
        }
    }

    private void loadProfile(Player player, String world, String gameMode) {
        world = world.toLowerCase();
        gameMode = gameMode.toLowerCase();

        File playerFile = new File(getDataFolder() + "/worlds/" + world, player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            player.getInventory().clear();
            getLogger().log(Level.INFO, "No player inventory set");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        player.getInventory().clear();
        player.getEnderChest().clear();

        ConfigurationSection inventorySection = config.getConfigurationSection(gameMode + ".inventory");
        if (inventorySection != null) {
            for (String slotString : inventorySection.getKeys(false)) {
                int slot = Integer.parseInt(slotString);
                ItemStack item = inventorySection.getItemStack(slotString);
                player.getInventory().setItem(slot, item);
            }
        }

        ConfigurationSection enderSection = config.getConfigurationSection(gameMode + ".ender_chest");
        if (enderSection != null) {
            for (String slotString : enderSection.getKeys(false)) {
                int slot = Integer.parseInt(slotString);
                ItemStack item = enderSection.getItemStack(slotString);
                player.getEnderChest().setItem(slot, item);
            }
        }

        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        ConfigurationSection effectsSection = config.getConfigurationSection(gameMode + ".effects");
        if (effectsSection != null) {
            for (String key : effectsSection.getKeys(false)) {
                String path = gameMode + ".effects." + key;
                PotionEffectType type = PotionEffectType.getByName(config.getString(path + ".type"));
                if (type != null) {
                    int duration = config.getInt(path + ".duration");
                    int amplifier = config.getInt(path + ".amplifier");
                    boolean ambient = config.getBoolean(path + ".ambient");
                    boolean particles = config.getBoolean(path + ".particles");
                    boolean icon = config.getBoolean(path + ".icon");

                    PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
                    player.addPotionEffect(effect);
                }
            }
        }

        player.getInventory().setHeldItemSlot(config.getInt(gameMode + ".selected_slot", 1));
        player.setExp((float)config.getDouble(gameMode + ".xp", 0));
        player.setLevel(config.getInt(gameMode + ".xp_level", 1));
        player.setRemainingAir(config.getInt(gameMode + ".air", 0));
        player.setMaximumAir(config.getInt(gameMode + ".air_max", 0));
        player.setFireTicks(config.getInt(gameMode + ".fire", 0));
        player.setFallDistance((float)config.getDouble(gameMode + ".fall_distance", 0));
        player.setHealth(config.getDouble(gameMode + ".health", 20));
        player.setTotalExperience(config.getInt(gameMode + ".xp_total", 0));
        player.setStarvationRate(config.getInt(gameMode + ".starvation", 80));
        player.setSaturation((float)config.getDouble(gameMode + ".saturation", 0));
        player.setAbsorptionAmount(config.getDouble(gameMode + ".absorption", 0));
    }
}
