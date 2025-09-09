package gpl;

import org.bukkit.OfflinePlayer;

/**
 * Minimal stub for ImprovedOfflinePlayer to remove NMS/CraftBukkit dependencies on MC 1.21.
 * This class preserves constructors and a few methods so existing call sites (if any)
 * will compile. Functionality that required direct NBT/offline manipulation was removed.
 */
public class ImprovedOfflinePlayer {
    private final String playerName;
    private boolean autosave = false;

    public ImprovedOfflinePlayer(String playername) {
        this.playerName = playername;
    }

    public ImprovedOfflinePlayer(OfflinePlayer offlineplayer) {
        this.playerName = offlineplayer != null ? offlineplayer.getName() : null;
    }

    public boolean exists() {
        // Original implementation searched player .dat files. Unsupported on 1.21.
        return false;
    }

    public void savePlayerData() {
        // No-op in stub.
    }

    public boolean getAutoSave() {
        return autosave;
    }

    public void setAutoSave(boolean autosave) {
        this.autosave = autosave;
    }

    public String getName() {
        return this.playerName;
    }
}
