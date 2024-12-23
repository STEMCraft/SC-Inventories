package com.stemcraft.storage;

import com.stemcraft.Inventories;
import com.stemcraft.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class YamlPlayerDataStorage implements PlayerDataStorage {
    private final Inventories plugin;

    public YamlPlayerDataStorage(Inventories instance) {
        plugin = instance;
    }


    @Override
    public void save(PlayerState profile) {
        File playerFile = new File(plugin.getDataFolder() + "/worlds/" + profile.getWorld().getName(), profile.getPlayer().getUniqueId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        String gameModeName = profile.getGameMode().name().toLowerCase();

        // Save inventory
        for (int i = 0; i < profile.getInventory().length; i++) {
            config.set(gameModeName + ".inventory." + i, profile.getInventory()[i]);
        }

        // Save ender chest
        for (int i = 0; i < profile.getEnderChest().length; i++) {
            config.set(gameModeName + ".ender_chest." + i, profile.getEnderChest()[i]);
        }

        // Save potion effects
        for (int i = 0; i < profile.getEffects().size(); i++) {
            PotionEffect effect = profile.getEffects().get(i);
            String path = gameModeName + ".effects." + i;
            config.set(path + ".type", effect.getType().getKey().value());
            config.set(path + ".duration", effect.getDuration());
            config.set(path + ".amplifier", effect.getAmplifier());
            config.set(path + ".ambient", effect.isAmbient());
            config.set(path + ".particles", effect.hasParticles());
            config.set(path + ".icon", effect.hasIcon());
        }

        // Save other properties
        config.set(gameModeName + ".selected_slot", profile.getSelectedSlot());
        config.set(gameModeName + ".xp", profile.getExp());
        config.set(gameModeName + ".xp_level", profile.getLevel());
        config.set(gameModeName + ".air", profile.getRemainingAir());
        config.set(gameModeName + ".air_max", profile.getMaximumAir());
        config.set(gameModeName + ".fire", profile.getFireTicks());
        config.set(gameModeName + ".fall_distance", profile.getFallDistance());
        config.set(gameModeName + ".health", profile.getHealth());
        config.set(gameModeName + ".xp_total", profile.getTotalExperience());
        config.set(gameModeName + ".starvation", profile.getStarvationRate());
        config.set(gameModeName + ".saturation", profile.getSaturation());
        config.set(gameModeName + ".absorption", profile.getAbsorptionAmount());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save inventory for player " + profile.getPlayer().getUniqueId(), e);
        }
    }

    @Override
    public PlayerState load(Player player, World world, GameMode gameMode) {
        PlayerState state = new PlayerState();

        state.setPlayer(player);
        state.setWorld(world);
        state.setGameMode(gameMode);
        String worldName = world.getName().toLowerCase();
        String gameModeName = gameMode.name().toLowerCase();

        File playerFile = new File(plugin.getDataFolder() + "/worlds/" + worldName, player.getUniqueId() + ".yml");
        if (playerFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            // Load inventory
            ConfigurationSection inventorySection = config.getConfigurationSection(gameModeName + ".inventory");
            if (inventorySection != null) {
                ItemStack[] inventory = new ItemStack[player.getInventory().getSize()];
                for (String slotString : inventorySection.getKeys(false)) {
                    int slot = Integer.parseInt(slotString);
                    inventory[slot] = inventorySection.getItemStack(slotString);
                }
                state.setInventory(inventory);
            }

            // Load ender chest
            ConfigurationSection enderSection = config.getConfigurationSection(gameModeName + ".ender_chest");
            if (enderSection != null) {
                ItemStack[] enderChest = new ItemStack[player.getEnderChest().getSize()];
                for (String slotString : enderSection.getKeys(false)) {
                    int slot = Integer.parseInt(slotString);
                    enderChest[slot] = enderSection.getItemStack(slotString);
                }
                state.setEnderChest(enderChest);
            }

            // Load potion effects
            List<PotionEffect> effects = new ArrayList<>();
            ConfigurationSection effectsSection = config.getConfigurationSection(gameModeName + ".effects");
            if (effectsSection != null) {
                for (String key : effectsSection.getKeys(false)) {
                    String path = gameModeName + ".effects." + key;
                    String effectName = config.getString(path + ".type");
                    assert effectName != null;
                    NamespacedKey nsKey = NamespacedKey.fromString(effectName.toLowerCase());
                    if (nsKey != null) {
                        PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(nsKey);
                        if (type != null) {
                            int duration = config.getInt(path + ".duration");
                            int amplifier = config.getInt(path + ".amplifier");
                            boolean ambient = config.getBoolean(path + ".ambient");
                            boolean particles = config.getBoolean(path + ".particles");
                            boolean icon = config.getBoolean(path + ".icon");

                            PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
                            effects.add(effect);
                        }
                    }
                }
            }
            state.setEffects(effects);

            // Load other properties
            state.setSelectedSlot(config.getInt(gameModeName + ".selected_slot", 0));
            state.setExp((float) config.getDouble(gameModeName + ".xp", 0));
            state.setLevel(config.getInt(gameModeName + ".xp_level", 0));
            state.setRemainingAir(config.getInt(gameModeName + ".air", 300));
            state.setMaximumAir(config.getInt(gameModeName + ".air_max", 300));
            state.setFireTicks(config.getInt(gameModeName + ".fire", 0));
            state.setFallDistance((float) config.getDouble(gameModeName + ".fall_distance", 0));
            state.setHealth(config.getDouble(gameModeName + ".health", 20));
            state.setTotalExperience(config.getInt(gameModeName + ".xp_total", 0));
            state.setStarvationRate(config.getInt(gameModeName + ".starvation", 80));
            state.setSaturation((float) config.getDouble(gameModeName + ".saturation", 5));
            state.setAbsorptionAmount(config.getDouble(gameModeName + ".absorption", 0));
        }

        return state;
    }

    @Override
    public boolean exists(Player player, World world, GameMode gameMode) {
        String worldName = world.getName().toLowerCase();
        String gameModeName = gameMode.name().toLowerCase();

        File playerFile = new File(plugin.getDataFolder() + "/worlds/" + worldName, player.getUniqueId() + ".yml");
        if(playerFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            return config.isConfigurationSection(gameModeName + ".inventory");
        }

        return false;
    }
}
