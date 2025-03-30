package org.accropvp.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


// import static org.bukkit.Bukkit.getLogger;

public class TagBedCommand implements CommandExecutor {
    private final PersistentStorageHandler storage;

    public TagBedCommand(PersistentStorageHandler storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        //getLogger().info("Tag Bed has been called");

        if (args.length < 4) {
            sender.sendMessage("Usage: /tagbed <x> <y> <z> <tag>");
            return true;
        }

        //getLogger().info("Il y a le bon nombre argument");


        try {
            // Parse the arguments
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            String tag = args[3];

            // Get the location
            Location location = new Location(Bukkit.getWorlds().getFirst(), x, y, z); // Default to the first world
            Block block = location.getBlock();

            // Check if the block is a bed
            if (block.getType().name().endsWith("_BED")) {
                // Save the tag to persistent storage
                storage.saveTag(location, tag);
                sender.sendMessage("Tag '" + tag + "' has been applied to the bed at " + location.x() + " " + location.y() + " " + location.z());
            } else {
                sender.sendMessage("The block at the specified location is not a bed.");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid coordinates. Please enter numeric values for x, y, and z.");
        } catch (Exception e) {
            sender.sendMessage("An error occurred: " + e.getMessage());
        }

        return true;
    }
}
