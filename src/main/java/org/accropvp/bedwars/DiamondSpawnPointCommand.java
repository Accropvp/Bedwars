package org.accropvp.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiamondSpawnPointCommand implements CommandExecutor {

    private final PersistentStorageHandler storage;

    public DiamondSpawnPointCommand(PersistentStorageHandler storage) {
        this.storage = storage;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /diamondSpawnPoint <x> <y> <z>");
            return true;
        }
        try {
            // Parse the arguments
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);

            // Get the location
            Location location = new Location(Bukkit.getWorlds().getFirst(), x, y, z); // Default to the first world
            String tag = "DIAMOND_SPAWNER";
            // Save the tag to persistent storage
            storage.addLocationToTag(tag, location);
            sender.sendMessage("Tag '" + tag + "' has been applied to the block at " + location.x() + " " + location.y() + " " + location.z());

        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid coordinates. Please enter numeric values for x, y, and z.");
        } catch (Exception e) {
            sender.sendMessage("An error occurred: " + e.getMessage());
        }

        return true;
    }
}
