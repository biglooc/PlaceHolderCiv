package com.avrgaming.civcraft.mobs;

import org.bukkit.Location;

public interface MobIntegration {
    boolean mobExists(String mobName);

    /** Volledige variant met expliciete delay. */
    void ensureSpawner(String mobName, Location location, int delaySeconds, boolean active);

    /** Gemaksoverload: default delay (60s) */
    default void ensureSpawner(String mobName, Location location, boolean active) {
        ensureSpawner(mobName, location, 60, active); // pas 60 aan als je andere default wil
    }

    boolean isEnabled();
}
