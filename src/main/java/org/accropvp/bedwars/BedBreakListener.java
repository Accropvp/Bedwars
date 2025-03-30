package org.accropvp.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

public class BedBreakListener implements Listener {
    private final PersistentStorageHandler storage;
    private final DynamicScoreboard dynamicScoreboard;
    Set<Team> teams;

    public BedBreakListener(PersistentStorageHandler storage, DynamicScoreboard dynamicScoreboard) {
        this.storage = storage;
        this.dynamicScoreboard = dynamicScoreboard;
        // Initialize the scoreboard safely during onEnable()
        Bukkit.getScoreboardManager();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        // Step 2: Get all the teams
        teams = scoreboard.getTeams();
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if the broken block is a bed
        if (block.getType().name().endsWith("_BED")) {
            Location location = block.getLocation();

            // Check if this bed has a tag in the persistent storage
            String tag = storage.getTag(location);
            if (tag == null) {
                location.setX(location.x() + 1);
                tag = storage.getTag(location);
            }
            if (tag == null) {
                location.setX(location.x() - 2);
                tag = storage.getTag(location);
            }
            if (tag == null) {
                location.setX(location.x() + 1);
                location.setZ(location.z() + 1);
                tag = storage.getTag(location);
            }
            if (tag == null) {
                location.setZ(location.z() - 1);
                tag = storage.getTag(location);
            }
            if (tag == null) {
                getLogger().severe("The bed in " + location.x() + " " + location.y() + " " + location.z() + " Has no tag");
                return;
            }

            for (Team team : teams) {
                if (team.getName().equals(tag)){
                    Bedwars.isTeamBedHere.put(team, false);
                    Bukkit.broadcastMessage(ChatColor.WHITE + "The " + team.getName() + " team bed has been destroyed");
                    dynamicScoreboard.updateScorboardTeam(team);
                }
            }
            for (Player player1 : Bukkit.getOnlinePlayers()){
                player1.playSound(player1.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            }
            // Optional: Remove the tag from storage after the bed is broken
            //storage.removeTag(location);

        }
    }
}
