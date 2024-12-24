package com.stemcraft.commands;

import com.stemcraft.Inventories;
import com.stemcraft.common.STEMCraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearInventory extends STEMCraftCommand {

    public ClearInventory(Inventories instance) {
        alias("clearinv");
        tabComplete("{player}");
    }

    @Override
    public void execute(CommandSender sender, String command, List<String> args) {
        if (!sender.hasPermission("stemcraft.inventory.clear")) {
            message(sender, "You do not have permission to use this command");
            return;
        }

        if (args.isEmpty() && !(sender instanceof Player)) {
            message(sender, "A player name is required when using this command from the console");
            return;
        }

        Player target;
        if (!args.isEmpty()) {
            target = Bukkit.getServer().getPlayerExact(args.getFirst());
            if (target == null) {
                message(sender, "The player {player} was not found or online", "player", args.getFirst());
                return;
            }
        } else {
            target = (Player) sender;
        }

        target.getInventory().clear();
        message(sender, "Cleared the inventory of {player}", "player", target.getName());
    }
}
