package org.accropvp.bedwars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public class GetUpgradeRadius implements CommandExecutor {

    private final PersistentStorageHandler storage;

    public GetUpgradeRadius(PersistentStorageHandler storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /upgradeRadius <radius>");
            return true;
        }

        try {
            // Parse the arguments
            TeamUpgrade.UpgradeRadius = Double.parseDouble(args[0]);
            storage.addKeyValue("UpgradeRadius", args[0]);
            sender.sendMessage("upgrade radius is updated");

        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number. Please enter integer values for the radius.");
        } catch (Exception e) {
            sender.sendMessage("An error occurred: " + e.getMessage());
        }

        return true;
    }
}
