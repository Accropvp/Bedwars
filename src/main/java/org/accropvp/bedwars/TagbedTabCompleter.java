package org.accropvp.bedwars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TagbedTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return null; // No suggestions for non-players
        }

        Block targetBlock = player.getTargetBlockExact(100);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            return null;
        }
        Location targetBlockLocation = targetBlock.getLocation();
        // Get the player's current location
        double x = targetBlockLocation.getX();
        double y = targetBlockLocation.getY();
        double z = targetBlockLocation.getZ();

        // Create a list of suggestions based on the arguments
        List<String> suggestions = new ArrayList<>();
        switch (args.length) {
            case 1: // Suggest X coordinate
                suggestions.add(String.valueOf((int) x));
                suggestions.add((int) x + " " + (int) y);
                suggestions.add((int) x + " " + (int) y + " " + (int) z);
                break;
            case 2: // Suggest Y coordinate
                suggestions.add(String.valueOf((int) y));
                suggestions.add((int) y + " " + (int) z);
                break;
            case 3: // Suggest Z coordinate
                suggestions.add(String.valueOf((int) z));
                break;
            default:
                break;
        }

        return suggestions;
    }
}

