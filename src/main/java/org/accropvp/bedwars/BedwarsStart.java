package org.accropvp.bedwars;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BedwarsStart implements CommandExecutor {
    private final Bedwars bedwars;

    public BedwarsStart(Bedwars bedwars){
        this.bedwars = bedwars;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        //try {
        bedwars.start();
        commandSender.sendMessage("Bedwars plugin successfully started");
        //} catch (Exception e) {
        //    commandSender.sendMessage("An error occured " + e.getMessage());
        //    bedwars.getLogger().severe("An error occured " + e.getMessage());
        //}
        return true;
    }
}
