package com.avrgaming.civcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.lang.reflect.Method;
import java.util.UUID;

public final class VanishUtil {
    private VanishUtil() {}

    public static boolean isVanished(Player p) {
        if (p == null) return false;

        // 1) PremiumVanish / SuperVanish (delen dezelfde API)
        // de.myzelyam.api.vanish.VanishAPI#isInvisible(UUID)
        if (hasPlugin("PremiumVanish") || hasPlugin("SuperVanish")) {
            Boolean v = callStaticBoolean(
                    "de.myzelyam.api.vanish.VanishAPI",
                    "isInvisible",
                    new Class<?>[]{UUID.class},
                    new Object[]{p.getUniqueId()}
            );
            if (v != null) return v;
        }

        // 2) EssentialsX
        // Essentials#getUser(UUID).isVanished()
        if (hasPlugin("Essentials")) {
            try {
                Object ess = Bukkit.getPluginManager().getPlugin("Essentials");
                if (ess != null) {
                    Method getUser = ess.getClass().getMethod("getUser", UUID.class);
                    Object user = getUser.invoke(ess, p.getUniqueId());
                    if (user != null) {
                        Method isVanished = user.getClass().getMethod("isVanished");
                        Object r = isVanished.invoke(user);
                        if (r instanceof Boolean) return (Boolean) r;
                    }
                }
            } catch (Throwable ignored) {}
        }

        // 3) VanishNoPacket (Kitteh) – probeer zowel API-klasse als plugin manager
        // Mogelijk: org.kitteh.vanish.VanishAPI#isVanished(Player)
        Boolean vnp = callStaticBoolean(
                "org.kitteh.vanish.VanishAPI",
                "isVanished",
                new Class<?>[]{Player.class},
                new Object[]{p}
        );
        if (vnp != null) return vnp;

        // Alternatieve VNP route: plugin.getManager().isVanished(Player)
        try {
            Object plugin = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
            if (plugin != null) {
                Method getManager = plugin.getClass().getMethod("getManager");
                Object mgr = getManager.invoke(plugin);
                if (mgr != null) {
                    Method isVanished = mgr.getClass().getMethod("isVanished", Player.class);
                    Object r = isVanished.invoke(mgr, p);
                    if (r instanceof Boolean) return (Boolean) r;
                }
            }
        } catch (Throwable ignored) {}

        // 4) Metadata-keys die veel plugins zetten
        if (hasTrueMetadata(p, "vanished") || hasTrueMetadata(p, "invisible")) return true;

        // 5) Sommige plugins zetten een scoreboard tag
        try {
            if (p.getScoreboardTags().contains("vanish")
                    || p.getScoreboardTags().contains("vanished")) {
                return true;
            }
        } catch (Throwable ignored) {}

        // Geen provider gevonden → niet vanished
        return false;
    }

    private static boolean hasPlugin(String name) {
        try {
            return Bukkit.getPluginManager().getPlugin(name) != null
                    && Bukkit.getPluginManager().isPluginEnabled(name);
        } catch (Throwable t) {
            return false;
        }
    }

    private static Boolean callStaticBoolean(String className, String method,
                                             Class<?>[] sig, Object[] args) {
        try {
            Class<?> c = Class.forName(className);
            Method m = c.getMethod(method, sig);
            Object r = m.invoke(null, args);
            return (r instanceof Boolean) ? (Boolean) r : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean hasTrueMetadata(Player p, String key) {
        try {
            for (MetadataValue mv : p.getMetadata(key)) {
                try {
                    if (mv != null && mv.asBoolean()) return true;
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
        return false;
    }
}
