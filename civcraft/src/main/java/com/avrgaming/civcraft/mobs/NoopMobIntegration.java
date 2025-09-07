package com.avrgaming.civcraft.mobs;

import org.bukkit.Location;

public class NoopMobIntegration implements MobIntegration {
    public void ensureSpawner(String mobName, Location loc, int delaySec, boolean active) { /* niks */ }
    public boolean mobExists(String mobName) { return false; }
    public boolean isEnabled() { return false; }
}