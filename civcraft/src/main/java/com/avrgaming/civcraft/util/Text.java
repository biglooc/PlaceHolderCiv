package com.avrgaming.civcraft.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Text {
    private Text() {}
    // Gebruik §-kleuren; switch naar legacyAmpersand() als jullie & gebruiken.
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public static void setDisplayName(ItemMeta meta, String name) {
        meta.displayName(name == null ? null : LEGACY.deserialize(name));
    }

    public static String getDisplayName(ItemMeta meta) {
        Component c = meta.displayName();
        return (c == null) ? null : LEGACY.serialize(c);
    }

    public static void setLore(ItemMeta meta, List<String> lines) {
        if (lines == null) { meta.lore(null); return; }
        List<Component> comps = new ArrayList<>(lines.size());
        for (String s : lines) comps.add(LEGACY.deserialize(s == null ? "" : s));
        meta.lore(comps);
    }

    public static List<String> getLore(ItemMeta meta) {
        List<Component> comps = meta.lore();
        if (comps == null) return Collections.emptyList();
        List<String> out = new ArrayList<>(comps.size());
        for (Component c : comps) out.add(LEGACY.serialize(c));
        return out;
    }

    public static boolean hasDisplayName(ItemMeta meta) { return meta.displayName() != null; }
    public static boolean hasLore(ItemMeta meta) { return meta.lore() != null && !meta.lore().isEmpty(); }
}
