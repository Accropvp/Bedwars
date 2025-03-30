package org.accropvp.bedwars;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;


public class DynamicScoreboard {

    private final Scoreboard scoreboard;
    private final Objective objective;
    public String[] scoreboardLines = {
            ChatColor.AQUA + "Diamond II",
            ChatColor.GREEN + "5:30",
            "",
            ChatColor.RED + "RED",
            ChatColor.BLUE + "BLUE",
            ChatColor.GREEN + "GREEN",
            ChatColor.YELLOW + "YELLOW",
            ChatColor.AQUA + "CYAN",
            ChatColor.WHITE + "WHITE",
            ChatColor.LIGHT_PURPLE + "PINK",
            ChatColor.GRAY + "GRAY",
    };

    public DynamicScoreboard(){
        // Create the scoreboard and objective if not already done
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Criteria criteria = Criteria.DUMMY;
        Component component = Component.text("BED WARS");
        RenderType renderType = RenderType.INTEGER;
        objective = scoreboard.registerNewObjective("dynamicSidebar", criteria, component, renderType);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setCustomScoreboard(Player player) {
        // Set the scoreboard for the player
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setCustomScoreboard(player); // Ensure each player has the scoreboard

            objective.getScore(scoreboardLines[0]).setScore(11);
            objective.getScore(scoreboardLines[1]).setScore(10);
            objective.getScore(scoreboardLines[2]).setScore(9);

            addTeamStatus(ChatColor.RED + "RED", 8, false, 0);
            addTeamStatus(ChatColor.BLUE + "BLUE", 7, false, 0);
            addTeamStatus(ChatColor.GREEN + "GREEN", 6, false, 0);
            addTeamStatus(ChatColor.YELLOW + "YELLOW", 5, false, 0);
            addTeamStatus(ChatColor.AQUA + "CYAN", 4, false, 0);
            addTeamStatus(ChatColor.WHITE + "WHITE", 3, false, 0);
            addTeamStatus(ChatColor.LIGHT_PURPLE + "PINK", 2, false, 0);
            addTeamStatus(ChatColor.GRAY + "GRAY", 1, false, 0);
        }
    }

    public void updateScorboardTeam(Team team){
        String teamName = team.getName();
        boolean isBedIntact = Bedwars.isTeamBedHere.get(team);
        int alivePlayer = Bedwars.alivePlayer.get(team);
        switch (teamName){
            case "RED" -> addTeamStatus(ChatColor.RED + teamName, 8, isBedIntact, alivePlayer);
            case "BLUE" -> addTeamStatus(ChatColor.BLUE + teamName, 7, isBedIntact, alivePlayer);
            case "GREEN" -> addTeamStatus(ChatColor.GREEN + teamName, 6, isBedIntact, alivePlayer);
            case "YELLOW" -> addTeamStatus(ChatColor.YELLOW + teamName, 5, isBedIntact, alivePlayer);
            case "CYAN" -> addTeamStatus(ChatColor.AQUA + teamName, 4, isBedIntact, alivePlayer);
            case "WHITE" -> addTeamStatus(ChatColor.WHITE + teamName, 3, isBedIntact, alivePlayer);
            case "PINK" -> addTeamStatus(ChatColor.LIGHT_PURPLE + teamName, 2, isBedIntact, alivePlayer);
            case "GRAY" -> addTeamStatus(ChatColor.GRAY + teamName, 1, isBedIntact, alivePlayer);
            default -> { return; }
        }
    }

    public void updateScoreboardNextEvent(String event){
        scoreboard.resetScores(scoreboardLines[0]);
        scoreboardLines[0] = event;
        objective.getScore(event).setScore(11);
    }

    public void updateScoreboardTimer(int time){
        scoreboard.resetScores(scoreboardLines[1]);
        String readableTime = ChatColor.GREEN + "" + (time/60) + ":" + String.format("%02d",time%60);
        scoreboardLines[1] = readableTime;
        objective.getScore(readableTime).setScore(10);
    }

    private void addTeamStatus(String teamName, int position,Boolean isBedIntact, int playerAlive) {
        scoreboard.resetScores(scoreboardLines[11 - position]); // remove score
        if (isBedIntact){
            String status = teamName + " : " + ChatColor.GREEN + "✔";
            scoreboardLines[11 - position] = status;
            objective.getScore(status).setScore(position);
            return;
        }
        if (playerAlive <= 0) {
            String status = teamName + " : " + ChatColor.RED + "❌";
            scoreboardLines[11 - position] = status;
            objective.getScore(status).setScore(position);
            return;
        }
        String status = teamName + " : " + ChatColor.WHITE + playerAlive;
        scoreboardLines[11 - position] = status;
        objective.getScore(status).setScore(position);
    }
}
