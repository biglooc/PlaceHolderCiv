package com.avrgaming.civcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class TagUtil {
    private TagUtil() {}
    private final static Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    public static void refreshPlayers() {
        for(Player _player : Bukkit.getOnlinePlayers()) {
            _player.setScoreboard(scoreboard);
        }
    }

    public static void createTeam(String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if(team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        String prefix = "[" + teamName.substring(0, 3).toUpperCase() + "] " + ChatColor.RESET;
        team.setPrefix(ChatColor.LIGHT_PURPLE + prefix);
    }

    public static void removeTeam(String civName) {
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;
        Team team = scoreboard.getTeam(teamName);

        if(team != null) {
            team.unregister();
        }
    }

    public static void addToTeam(Player player, String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if(team != null) {
            if(!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
                refreshPlayers();
            }
        }
    }

    public static void removeFromTeam(Player player, String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if (team != null && team.hasEntry(player.getName())) {
            team.removeEntry(player.getName());
            refreshPlayers();
        }
    }
}
