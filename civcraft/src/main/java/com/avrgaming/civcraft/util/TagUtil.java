package com.avrgaming.civcraft.util;

import org.bukkit.Bukkit;
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

        String prefix = "[" + teamName.substring(0, 3).toUpperCase() + "] ";
        team.setPrefix(prefix);
    }

    public static void addToTeam(Player player, String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if(team != null) {
            team.addEntry(player.getName());
            for(Player _player : Bukkit.getOnlinePlayers()) {
                _player.setScoreboard(scoreboard);
            }
        }
    }

    public static void removeFromTeam(Player player, String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if (team != null && team.hasEntry(player.getName())) {
            team.removeEntry(player.getName());
            for(Player _player : Bukkit.getOnlinePlayers()) {
                _player.setScoreboard(scoreboard);
            }
        }
    }
}
