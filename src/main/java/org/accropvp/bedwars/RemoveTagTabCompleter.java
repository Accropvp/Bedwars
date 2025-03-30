package org.accropvp.bedwars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoveTagTabCompleter implements TabCompleter {

    private final PersistentStorageHandler storage;

    public RemoveTagTabCompleter(PersistentStorageHandler storage) {
        this.storage = storage;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return storage.getAllKeys().stream().toList();
    }
}
