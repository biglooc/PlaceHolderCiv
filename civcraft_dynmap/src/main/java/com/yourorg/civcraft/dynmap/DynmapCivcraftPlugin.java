package com.yourorg.civcraft.dynmap;
import org.bukkit.plugin.java.JavaPlugin;
public class DynmapCivcraftPlugin extends JavaPlugin {
    @Override public void onEnable() { getLogger().info("CivCraft Dynmap enabled"); }
    @Override public void onDisable() { getLogger().info("CivCraft Dynmap disabled"); }
}
