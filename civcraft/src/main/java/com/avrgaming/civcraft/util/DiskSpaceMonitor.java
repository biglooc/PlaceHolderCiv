package com.avrgaming.civcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class DiskSpaceMonitor {

    public enum Level { OK, WARN, CRITICAL }

    private final JavaPlugin plugin;
    private final long intervalMinutes;
    private final double warnPct;
    private final double criticalPct;
    private final boolean notifyAdmins;
    private final String adminPerm;
    private final boolean autoCleanup;

    private final AtomicReference<Level> lastLevel = new AtomicReference<Level>(Level.OK);
    private volatile long lastNotifyAtMs = 0L;

    private static final long NOTIFY_COOLDOWN_MS = 10 * 60 * 1000L; // 10 min
    private static final DecimalFormat DF = new DecimalFormat("#,##0.0");

    public DiskSpaceMonitor(JavaPlugin plugin) {
        this.plugin = plugin;
        FileConfiguration cfg = plugin.getConfig();

        this.intervalMinutes = Math.max(1, cfg.getLong("monitor.interval_minutes", 10));
        this.warnPct        = clamp(cfg.getDouble("monitor.warn_percent_free", 15.0), 0, 100);
        this.criticalPct    = clamp(cfg.getDouble("monitor.critical_percent_free", 5.0), 0, 100);
        this.notifyAdmins   = cfg.getBoolean("monitor.notify_admins", true);
        String ap = cfg.getString("monitor.admin_permission", "civ.admin");
        this.adminPerm      = (ap == null ? "civ.admin" : ap);
        this.autoCleanup    = cfg.getBoolean("monitor.auto_cleanup_on_critical", true);
    }

    public void start() {
        long ticks = intervalMinutes * 60L * 20L;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override public void run() { checkOnce(); }
        }, 20L, ticks);
        plugin.getLogger().info("[DiskSpace] monitor gestart: elke " + intervalMinutes + " min");
    }

    private void checkOnce() {
        try {
            Path dataDir = plugin.getDataFolder().toPath();
            Files.createDirectories(dataDir);

            FileStore store = Files.getFileStore(dataDir);
            long total = safeTotal(store, dataDir);
            long free = safeUsable(store, dataDir);

            if (total <= 0) return;

            double pctFree = (free * 100.0) / total;
            Level lvl = (pctFree < criticalPct) ? Level.CRITICAL :
                    (pctFree <  warnPct)    ? Level.WARN : Level.OK;

            plugin.getLogger().info(String.format("[DiskSpace] vrij: %s / %s (%.1f%%) — status: %s",
                    humanBytes(free), humanBytes(total), pctFree, lvl));

            Level prev = lastLevel.getAndSet(lvl);
            long now = System.currentTimeMillis();
            boolean cooldownPassed = (now - lastNotifyAtMs) > NOTIFY_COOLDOWN_MS;
            if ((lvl != prev || cooldownPassed) && lvl != Level.OK) {
                lastNotifyAtMs = now;
                String msg = warnMessage(pctFree, free, total, lvl);

                plugin.getLogger().warning(msg);
                if (notifyAdmins) notifyAdmins(msg);

                if (lvl == Level.CRITICAL && autoCleanup) {
                    tryAutoCleanup();
                }
            }

        } catch (Exception e) {
            plugin.getLogger().warning("[DiskSpace] check faalde: " + e.getMessage());
        }
    }

    private void tryAutoCleanup() {
        try {
            Path base = plugin.getDataFolder().toPath();
            int logsDays    = plugin.getConfig().getInt("retention.logs_days", 14);
            int exportsDays = plugin.getConfig().getInt("retention.exports_days", 30);
            long quotaMb    = plugin.getConfig().getLong("storage.data_quota_mb", 200);

            // Helpers uit DiskMaintenance (sectie B hieronder)
            DiskMaintenance.pruneByAge(base.resolve("logs"), logsDays);
            DiskMaintenance.pruneByAge(base.resolve("exports"), exportsDays);
            DiskMaintenance.pruneByQuota(base, quotaMb * 1024L * 1024L);

            plugin.getLogger().warning("[DiskSpace] auto-cleanup uitgevoerd (logs/exports/quota).");
        } catch (Exception ex) {
            plugin.getLogger().warning("[DiskSpace] auto-cleanup faalde: " + ex.getMessage());
        }
    }

    private void notifyAdmins(String msg) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player p : players) {
            if (p.hasPermission(adminPerm)) {
                p.sendMessage("§c" + msg);
            }
        }
    }

    private static String warnMessage(double pctFree, long free, long total, Level lvl) {
        return String.format("[DiskSpace] %s: vrije ruimte laag: %s / %s (%.1f%%). " +
                        "Overweeg logs te beperken en oude data op te ruimen.",
                (lvl == Level.CRITICAL ? "CRITICAL" : "WARN"),
                humanBytes(free), humanBytes(total), pctFree);
    }

    private static String humanBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return DF.format(bytes / Math.pow(1024, exp)) + " " + unit + "B";
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static long safeUsable(FileStore store, Path base) {
        try { return store.getUsableSpace(); }
        catch (Exception ignored) { return new File(base.toString()).getUsableSpace(); }
    }
    private static long safeTotal(FileStore store, Path base) {
        try { return store.getTotalSpace(); }
        catch (Exception ignored) { return new File(base.toString()).getTotalSpace(); }
    }
}
