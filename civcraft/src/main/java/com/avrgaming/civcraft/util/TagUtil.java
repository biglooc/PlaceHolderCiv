package com.avrgaming.civcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Vervanger voor iTag/TagAPI aanroepen via reflectie.
 * Werkt ofwel met (Player), of met (Player, Set<? extends Player>) zoals iTag.
 */
public final class TagUtil {
    private TagUtil() {}

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

    /** Overload compatibel met iTag-signatuur (2 params). */
    public static void refreshPlayer(Player player, Set<? extends Player> online) {
        if (player == null) return;

        // Probeer iTag met de meegegeven set
        try {
            Class<?> c = Class.forName("net.md_5.itag.iTag");
            Object itag = c.getMethod("getInstance").invoke(null);
            Method m = c.getMethod("refreshPlayer", Player.class, java.util.Set.class);
            m.invoke(itag, player, online);
            return;
        } catch (Throwable ignored) { /* fallthrough */ }

        // Val terug op TagAPI of de 1-param variant
        try {
            Class<?> c = Class.forName("org.kitteh.tag.TagAPI");
            c.getMethod("refreshPlayer", Player.class).invoke(null, player);
        } catch (Throwable ignored) {
            refreshPlayer(player); // gebruikt Bukkit.getOnlinePlayers()
        }
    }
}
