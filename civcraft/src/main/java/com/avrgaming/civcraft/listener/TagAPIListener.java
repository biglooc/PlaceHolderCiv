package com.avrgaming.civcraft.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * No-op shim voor TagAPI. Heeft geen plugin-deps.
 * Je kunt in CivCraft alleen registreren als TagAPI aanwezig is,
 * maar registreren zonder handlers is sowieso veilig.
 */
public class TagAPIListener implements Listener {
    public static boolean isTagApiPresent() {
        try {
            if (Bukkit.getPluginManager().getPlugin("TagAPI") == null) {
                Class.forName("org.kitteh.tag.TagAPI");
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
