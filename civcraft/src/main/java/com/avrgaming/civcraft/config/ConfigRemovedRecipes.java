package com.avrgaming.civcraft.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.avrgaming.civcraft.main.CivLog;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.avrgaming.civcraft.util.ItemManager;

public class ConfigRemovedRecipes {
	public int type_id;
	public int data;


    public static void removeRecipes(FileConfiguration cfg, HashMap<Integer, ConfigRemovedRecipes> removedRecipies) {
        List<Map<?, ?>> configMaterials = cfg.getMapList("removed_recipes");
        for (Map<?, ?> b : configMaterials) {
            ConfigRemovedRecipes item = new ConfigRemovedRecipes();
            item.type_id = (Integer) b.get("type_id");
            item.data = (Integer) b.get("data");
            removedRecipies.put(item.type_id, item);
        }

        Iterator<Recipe> iter = Bukkit.getServer().recipeIterator();
        List<Recipe> backup = new ArrayList<>();

        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            boolean remove = false;

            for (ConfigRemovedRecipes removed : removedRecipies.values()) {
                ItemStack target = new ItemStack(ItemManager.getMaterial(removed.type_id), 1);
                if (recipe.getResult().getType() == target.getType()) {
                    remove = true;
                    break;
                }
            }

            if (!remove) {
                backup.add(recipe);
            }
        }

        Bukkit.getServer().clearRecipes();
        for (Recipe r : backup) {
            Bukkit.getServer().addRecipe(r);
        }
    }
}