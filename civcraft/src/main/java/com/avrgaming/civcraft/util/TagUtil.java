package com.avrgaming.civcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Vervanger voor iTag/TagAPI aanroepen via reflectie.
 * Werkt ofwel met (Player), of met (Player, Set<? extends Player>) zoals iTag.
 */
public final class TagUtil {
    private TagUtil() {}
    private final static Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    /** Vervanger voor TagAPI (1 param) en iTag (we maken zelf de online set). */
    public static void refreshPlayer(Player player) {
        if (player == null) return;

        // iTag: net.md_5.itag.iTag.getInstance().refreshPlayer(Player, Set<Player>)
        try {
            Class<?> c = Class.forName("net.md_5.itag.iTag");
            Object itag = c.getMethod("getInstance").invoke(null);
            Method m = c.getMethod("refreshPlayer", Player.class, java.util.Set.class);
            m.invoke(itag, player, new HashSet<>(Bukkit.getOnlinePlayers()));
            return;
        } catch (Throwable ignored) { /* fallthrough */ }

        // TagAPI: org.kitteh.tag.TagAPI.refreshPlayer(Player)
        try {
            Class<?> c = Class.forName("org.kitteh.tag.TagAPI");
            c.getMethod("refreshPlayer", Player.class).invoke(null, player);
        } catch (Throwable ignored) {
            // geen provider — no-op
        }
    }

    public static void createTeam(String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if(team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        String prefix = teamName.substring(0, 3);
        team.setPrefix(prefix);
    }

    public static void addToTeam(Player player, String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if(team != null) {
            team.addEntry(player.getName());
            player.setScoreboard(scoreboard);
        }
    }

    public static void removeFromTeam(Player player, String civName) {
        // teamName can't be longer than 16 characters
        String teamName = civName.length() > 16 ? civName.substring(0, 16) : civName;

        Team team = scoreboard.getTeam(teamName);

        if (team != null && team.hasEntry(player.getName())) {
            team.removeEntry(player.getName());
        }
    }
}
