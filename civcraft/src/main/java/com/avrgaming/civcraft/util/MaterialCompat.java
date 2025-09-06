// civcraft/src/main/java/com/avrgaming/civcraft/util/MaterialCompat.java
package com.avrgaming.civcraft.util;

import org.bukkit.Material;

public final class MaterialCompat {
    private MaterialCompat() {}

    public static boolean is(Material type, String modernName, Material legacyFallback) {
        if (type == null) return false;
        if (modernName.equalsIgnoreCase(type.name())) return true;
        return type == legacyFallback;
    }

    public static boolean isAny(Material type, String modernName, Material... legacyFallbacks) {
        if (type == null) return false;
        if (modernName.equalsIgnoreCase(type.name())) return true;
        if (legacyFallbacks != null) {
            for (Material m : legacyFallbacks) {
                if (type == m) return true;
            }
        }
        return false;
    }
}
