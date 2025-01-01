package com.stemcraft.command;

import com.stemcraft.STEMCraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ClearInventory extends STEMCraftCommand {

    @Override
    public void execute(CommandSender sender, String command, List<String> args) {
        if (!sender.hasPermission("stemcraft.inventory.clear")) {
            error(sender, "You do not have permission to use this command");
            return;
        }

        if (args.isEmpty() && !(sender instanceof Player)) {
            error(sender, "A player name is required when using this command from the console");
            return;
        }

        Player target;
        if (!args.isEmpty()) {
            target = Bukkit.getServer().getPlayerExact(args.getFirst());
            if (target == null) {
                error(sender, "The player {player} was not found or online", "player", args.getFirst());
                return;
            }
        } else {
            target = (Player) sender;
        }

        target.getInventory().clear();
        success(sender, "Cleared the inventory of {player}", "player", target.getName());
    }
}
