package com.avrgaming.civcraft.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.avrgaming.civcraft.lorestorage.LoreGuiItemListener;

public class CivTutorial {

    public static final int MAX_CHEST_SIZE = 6;

    public static void showTutorialInventory(Player player) {
        String title = "CivCraft Tutorial";
        Inventory inv = Bukkit.getServer().createInventory(player, 9 * 3, title);
        LoreGuiItemListener.guiInventories.put(title, inv);
        if (player != null && player.isOnline() && player.isValid()) {
            player.openInventory(inv);
        }
    }

    public static void showCraftingHelp(Player player) {
        String title = "CivCraft Recipes";
        Inventory inv = Bukkit.getServer().createInventory(player, MAX_CHEST_SIZE * 9, title);
        LoreGuiItemListener.guiInventories.put(title, inv);
        if (player != null && player.isOnline() && player.isValid()) {
            player.openInventory(inv);
        }
    }

    public static void spawnGuiBook(Player player) {
        String title = "CivCraft Info";
        Inventory inv = Bukkit.getServer().createInventory(player, 3 * 9, title);
        LoreGuiItemListener.guiInventories.put(title, inv);
        if (player != null && player.isOnline() && player.isValid()) {
            player.openInventory(inv);
        }
    }
}
