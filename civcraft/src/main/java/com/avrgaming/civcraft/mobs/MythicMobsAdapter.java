package com.avrgaming.civcraft.mobs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;

public class MythicMobsAdapter implements MobIntegration {
    private final boolean enabled;

    public MythicMobsAdapter() {
        this.enabled = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
    }

    @Override public boolean isEnabled() { return enabled; }

    @Override
    public boolean mobExists(String mobName) {
        if (!enabled) return false;
        // Cheapest check: probeer info op te vragen; als mislukt, neem false
        // Command: /mm mobs list <name> bestaat niet in 4.x, dus we doen een best-effort:
        // Je kunt ook een interne registry cachen (config) en hierop valideren.
        return true; // of check tegen eigen configuratie
    }

    @Override
    public void ensureSpawner(String mobName, Location loc, int delaySeconds, boolean active) {
        if (!enabled) return;
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        String id = ("civ_" + mobName + "_" +
                loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ())
                .toLowerCase();

        // Maak of update spawner met vaste id
        // NB: MythicMobs 4.x spawner commands (globaal idee):
        // /mm spawner create <id>
        // /mm spawner set <id> mob <name>
        // /mm spawner set <id> location <world> <x> <y> <z>
        // /mm spawner set <id> warmup/delay/maxtotal etc.

        Bukkit.dispatchCommand(console, "mm spawner create " + id);
        Bukkit.dispatchCommand(console, "mm spawner set " + id + " mob " + mobName);
        Bukkit.dispatchCommand(console, "mm spawner set " + id + " location "
                + loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
        Bukkit.dispatchCommand(console, "mm spawner set " + id + " delay " + delaySeconds);

        if (active) {
            Bukkit.dispatchCommand(console, "mm spawner enable " + id);
        } else {
            Bukkit.dispatchCommand(console, "mm spawner disable " + id);
        }
    }
}
