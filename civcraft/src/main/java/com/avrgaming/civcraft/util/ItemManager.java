package com.avrgaming.civcraft.util;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;


/*
 * The ItemManager class is going to be used to wrap itemstack operations that have now
 * been deprecated by Bukkit. If bukkit ever actually takes these methods away from us,
 * we'll just have to use NMS or be a little creative. Doing it on spot (here) will be 
 * better than having fragile code scattered everywhere. 
 * 
 * Additionally it gives us an opportunity to unit test certain item operations that we
 * want to use with our new custom item stacks.
 */

public class ItemManager {

	// Minimal legacy ID -> Material mapping to support common items; extend as needed
	private static Material fromLegacyId(int id, int data) {
		switch (id) {
			case 261: return Material.BOW;
			case 297: return Material.BREAD;
			case 326: return Material.WATER_BUCKET;
			case 325: return Material.BUCKET;
			case 346: return Material.FISHING_ROD;
			case 368: return Material.ENDER_PEARL;
			case 387: return Material.WRITTEN_BOOK;
			case 289: return Material.GUNPOWDER;
			case 265: return Material.IRON_INGOT;
			case 266: return Material.GOLD_INGOT;
			case 264: return Material.DIAMOND;
			default: return null;
		}
	}

	public static boolean inventoryContainsLegacy(org.bukkit.inventory.Inventory inv, int itemId, int itemData) {
		for (ItemStack is : inv.getContents()) {
			if (is == null) continue;
			if (matchesLegacy(is, itemId, itemData)) return true;
		}
		return false;
	}

	public static java.util.Map<Integer, ItemStack> allLegacy(org.bukkit.inventory.Inventory inv, int itemId) {
		java.util.Map<Integer, ItemStack> out = new java.util.HashMap<>();
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack is = inv.getItem(i);
			if (is == null) continue;
			if (matchesLegacy(is, itemId, -1)) out.put(i, is);
		}
		return out;
	}

	public static boolean isItemInMainHandLegacy(org.bukkit.entity.Player p, int itemId, int itemData) {
		ItemStack is = p.getInventory().getItemInMainHand();
		return is != null && matchesLegacy(is, itemId, itemData);
	}

	private static boolean matchesLegacy(ItemStack is, int itemId, int itemData) {
		Material m = fromLegacyId(itemId, itemData);
		if (m == null) return false;
		if (is.getType() != m) return false;
		return true; // ignoring data for now; extend if needed
	}

	public static ItemStack createItemStack(int typeId, int amount, short damage) {
		Material m = fromLegacyId(typeId, damage);
		if (m == null) m = Material.AIR;
		ItemStack is = new ItemStack(m, Math.max(1, amount));
		// damage ignored on modern versions unless item is damageable; retained for legacy compatibility
		return is;
	}

	public static ItemStack createItemStack(int typeId, int amount) {
		return createItemStack(typeId, amount, (short)0);
	}

	public static MaterialData getMaterialData(int type_id, int data) {
		// MaterialData is deprecated; provide minimal shim for legacy callers
		return new MaterialData(fromLegacyId(type_id, data) == null ? Material.AIR : fromLegacyId(type_id, data));
	}
	
	public static Enchantment getEnchantById(int id) {
		// No direct ID mapping in 1.21; return null to indicate unsupported
		return null;
	}
	
	public static int getId(Material material) {
		// Legacy numeric IDs removed; return ordinal as stable placeholder (not Mojang ID)
		return material.ordinal();
	}
	
	public static int getId(Enchantment e) {
		// Unsupported in 1.21; return -1
		return -1;
	}
	
	public static int getId(ItemStack stack) {
		// Return a pseudo id via material ordinal
		return stack.getType().ordinal();
	}
	
	public static int getId(Block block) {
		return block.getType().ordinal();
	}
	
	public static void setTypeId(Block block, int typeId) {
		Material m = fromLegacyId(typeId, 0);
		block.setType(m == null ? Material.AIR : m);
	}
	
	public static void setTypeId(BlockState block, int typeId) {
		Material m = fromLegacyId(typeId, 0);
		block.setType(m == null ? Material.AIR : m);
	}
	
	public static byte getData(Block block) {
		// BlockData no longer fits into a byte; return 0 to keep legacy methods compiling
		return 0;
	}
	
	public static short getData(ItemStack stack) {
		// Durability concept changed; return 0 for compatibility
		return 0;
	}
	
	public static byte getData(MaterialData data) {
		return 0;
	}

	public static byte getData(BlockState state) {
		return 0;
	}
	
	public static void setData(Block block, int data) {
		// No-op on modern API
	}
	
	public static void setData(Block block, int data, boolean update) {
		// No-op on modern API
	}
	
	public static Material getMaterial(int material) {
		// Translate int legacy id to Material using our mapping
		Material m = fromLegacyId(material, 0);
		return m == null ? Material.AIR : m;
	}
	
	public static int getBlockTypeId(ChunkSnapshot snapshot, int x, int y, int z) {
		return snapshot.getBlockType(x, y, z).ordinal();
	}
	
	public static int getBlockData(ChunkSnapshot snapshot, int x, int y, int z) {
		// BlockData is complex; return 0 as placeholder
		return 0;
	}
	
	public static void sendBlockChange(Player player, Location loc, int type, int data) {
		Material m = fromLegacyId(type, data);
		if (m == null) m = Material.AIR;
		player.sendBlockChange(loc, m.createBlockData());
	}
	
	public static int getBlockTypeIdAt(World world, int x, int y, int z) {
		return world.getBlockAt(x, y, z).getType().ordinal();
	}
	
	public static int getId(BlockState newState) {
		return newState.getType().ordinal();
	}
	
	public static short getId(EntityType entity) {
		// Numeric type IDs removed; return ordinal
		return (short) entity.ordinal();
	}
	
	public static void setData(MaterialData data, byte chestData) {
		// No-op; legacy API
	}
	
	public static void setTypeIdAndData(Block block, int type, int data, boolean update) {
		setTypeId(block, type);
	}
	
	public static ItemStack spawnPlayerHead(String playerName, String itemDisplayName) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		if (meta != null) {
			try {
				// 1.13+: setOwningPlayer requires OfflinePlayer, fallback to deprecated setOwner if available at runtime
				org.bukkit.OfflinePlayer off = org.bukkit.Bukkit.getOfflinePlayer(java.util.UUID.nameUUIDFromBytes(("OfflinePlayer:"+playerName).getBytes(java.nio.charset.StandardCharsets.UTF_8)));
				meta.setOwningPlayer(off);
			} catch (Throwable ignored) {
				try { meta.getClass().getMethod("setOwner", String.class).invoke(meta, playerName); } catch (Exception ignored2) {}
			}
			meta.setDisplayName(itemDisplayName);
			skull.setItemMeta(meta);
		}
		return skull;
	}

	public static boolean removeItemFromPlayer(Player player, Material mat, int amount) {
		ItemStack m = new ItemStack(mat, amount);
		if (player.getInventory().contains(mat)) {
			player.getInventory().removeItem(m);
			return true;
		}
		return false;
	}
	
}
