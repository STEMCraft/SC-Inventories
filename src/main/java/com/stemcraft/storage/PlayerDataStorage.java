package com.stemcraft.storage;

import com.stemcraft.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface PlayerDataStorage {

    boolean exists(Player player, World world, GameMode gameMode);

    void save(PlayerState profile);

    PlayerState load(Player player, World world, GameMode gameMode);
}
