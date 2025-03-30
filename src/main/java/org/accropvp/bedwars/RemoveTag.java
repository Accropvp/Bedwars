package org.accropvp.bedwars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RemoveTag implements CommandExecutor {

    private final PersistentStorageHandler storage;

    public RemoveTag(PersistentStorageHandler storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length != 1){
            commandSender.sendMessage("Usage: /removetag <tag>");
        }
        try {
            String tag = args[0];
            storage.removeKey(tag);
            commandSender.sendMessage("tag removed successfully");

        } catch (Exception e) {
            commandSender.sendMessage("§cAn error occurred: " + e.getMessage());
            return false;
        }
        return true;
    }
}
