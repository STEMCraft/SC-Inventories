package com.stemcraft;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.attribute.Attribute;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A player state which contains all the details on a player at a particular point in time
 */
@Getter @Setter
public class PlayerState {
    private Player player;
    private World world;
    private GameMode gameMode;
    private ItemStack[] inventory;
    private ItemStack[] enderChest;
    private List<PotionEffect> effects = new ArrayList<>();
    private int selectedSlot = 1;
    private float exp = 0;
    private int level = 0;
    private int remainingAir = 20;
    private int maximumAir = 20;
    private int fireTicks = 0;
    private float fallDistance = 0;
    private double health = 20;
    private int totalExperience = 0;
    private int starvationRate = 0;
    private float saturation = 0;
    private double absorptionAmount = 0;

    public PlayerState() {
        // empty
    }

    public PlayerState(Player player) {
        get(player);
    }

    /**
     * Reset the player state (takes affect immediately)
     * @param player The player to reset
     */
    static public void reset(Player player) {
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.setExp(0);
        player.setLevel(0);
        player.setRemainingAir(player.getMaximumAir());
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getDefaultValue());
        player.setTotalExperience(0);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setAbsorptionAmount(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    /**
     * Set this player state to a players current state
     * @param player The player to retrieve
     */
    public void get(Player player) {
        get(player, player.getWorld(), player.getGameMode());
    }

    /**
     * Set this player state to a players current state
     * @param player The player to retrieve
     * @param world The world to allocate the state
     * @param gameMode The game mode to allocate the state
     */
    public void get(Player player, World world, GameMode gameMode) {
        this.player = player;
        this.world = world;
        this.gameMode = gameMode;
        inventory = player.getInventory().getContents();
        enderChest = player.getEnderChest().getContents();
        effects = new ArrayList<>(player.getActivePotionEffects());
        selectedSlot = player.getInventory().getHeldItemSlot();
        exp = player.getExp();
        level = player.getLevel();
        remainingAir = player.getRemainingAir();
        maximumAir = player.getMaximumAir();
        fireTicks = player.getFireTicks();
        fallDistance = player.getFallDistance();
        health = player.getHealth();
        totalExperience = player.getTotalExperience();
        starvationRate = player.getStarvationRate();
        saturation = player.getSaturation();
        absorptionAmount = player.getAbsorptionAmount();
    }

    /**
     * Set the player to this current state
     */
    public void set() {
        player.getInventory().clear();
        player.getEnderChest().clear();

        if(inventory != null) {
            player.getInventory().setContents(inventory);
        }

        if(enderChest != null) {
            player.getEnderChest().setContents(enderChest);
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if(effects != null) {
            for (PotionEffect effect : effects) {
                player.addPotionEffect(effect);
            }
        }

        player.getInventory().setHeldItemSlot(selectedSlot);
        player.setExp(exp);
        player.setLevel(level);
        player.setRemainingAir(remainingAir);
        player.setFireTicks(fireTicks);
        player.setFallDistance(fallDistance);
        player.setHealth(health);
        player.setTotalExperience(totalExperience);
        player.setStarvationRate(starvationRate);
        player.setSaturation(saturation);
        player.setAbsorptionAmount(absorptionAmount);
    }
}
