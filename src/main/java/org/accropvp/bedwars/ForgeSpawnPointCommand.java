package org.accropvp.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class ForgeSpawnPointCommand implements CommandExecutor {

    private final PersistentStorageHandler storage;
    private final Scoreboard scoreboard;

    public ForgeSpawnPointCommand(PersistentStorageHandler storage) {
        this.storage = storage;
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("Usage: /forgeSpawnPoint <x> <y> <z> <team>");
            return true;
        }
        try {
            // Parse the arguments
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            String teamName = args[3];
            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                throw new Exception("Invalid team, The team you input does not exist");
            }
            // Get the location
            Location location = new Location(Bukkit.getWorlds().getFirst(), x, y, z); // Default to the first world
            // Save the tag to persistent storage
            storage.addLocationToTag(teamName, location);
            sender.sendMessage("Tag '" + teamName + "' has been applied to the block at " + location.x() + " " + location.y() + " " + location.z());

        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid coordinates. Please enter numeric values for x, y, and z.");
        } catch (Exception e) {
            sender.sendMessage("An error occurred: " + e.getMessage());
        }

        return true;
    }
}
