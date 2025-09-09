package com.avrgaming.civcraft.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.avrgaming.civcraft.util.ItemManager;

public class ConfigRemovedRecipes {
	public int type_id;
	public int data;
	
	
	public static void removeRecipes(FileConfiguration cfg, HashMap<Integer, ConfigRemovedRecipes> removedRecipies) {
		List<Map<?, ?>> configMaterials = cfg.getMapList("removed_recipes");
		if (configMaterials == null || configMaterials.isEmpty()) {
			return;
		}
		for (Map<?, ?> b : configMaterials) {
			if (b == null) continue;

			ConfigRemovedRecipes item = new ConfigRemovedRecipes();
			Object typeObj = b.get("type_id");
			Object dataObj = b.get("data");
			String materialName = (String) b.get("material");

			int dataVal = (dataObj instanceof Integer) ? (Integer) dataObj : 0;

			ItemStack is = null;
			Material mat = null;

			if (materialName != null && !materialName.isEmpty()) {
				mat = Material.matchMaterial(materialName);
				if (mat == null) {
					try { mat = Material.valueOf(materialName.toUpperCase()); } catch (Exception ignored) {}
				}
				if (mat != null && mat != Material.AIR) {
					is = new ItemStack(mat, 1);
					item.type_id = ItemManager.getId(mat);
					item.data = dataVal;
				}
			}

			if (is == null && (typeObj instanceof Integer)) {
				item.type_id = (Integer) typeObj;
				item.data = dataVal;

				is = ItemManager.createItemStack(item.type_id, 1, (short) item.data);
				if (is == null || is.getType() == Material.AIR) {
					mat = ItemManager.getMaterial(item.type_id);
					if (mat != null && mat != Material.AIR) {
						is = new ItemStack(mat, 1);
					}
				}
			}

			if (is == null || is.getType() == null || is.getType() == Material.AIR) {
				continue;
			}
			removedRecipies.put(item.type_id, item);

			List<Recipe> backup = new ArrayList<Recipe>();
			// Idk why you change scope, but why not
			Iterator<Recipe> a = Bukkit.getServer().recipeIterator();
			while(a.hasNext()){
				Recipe recipe = a.next();
				ItemStack result = recipe.getResult();
				if (!result.isSimilar(is)) {
					backup.add(recipe);
				}
			}

			 Bukkit.getServer().clearRecipes();
			 for (Recipe r : backup) {
				 Bukkit.getServer().addRecipe(r);
			 }
		}
	}
}