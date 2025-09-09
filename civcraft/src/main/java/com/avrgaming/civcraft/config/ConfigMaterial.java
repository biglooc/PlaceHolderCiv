package com.avrgaming.civcraft.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.util.CivColor;

/**
 * ConfigMaterial met support voor modern Bukkit Material via YAML key "material".
 * Backwards-compatible met legacy item_id / item_data.
 */
public class ConfigMaterial {

	/* Required (legacy pad) */
	public String id;
	public int item_id;
	public int item_data;
	public String name;

	/* Categorie & tier */
	public String category = /* default uit localization */ com.avrgaming.civcraft.config.CivSettings.localize.localizedString("config_material_misc");
	public String categoryCivColortripped = CivColor.stripTags(category);
	public int tier;

	/* Modern pad (result item via Bukkit Material) */
	/** YAML: "material": bv. "DIAMOND", "ACACIA_STAIRS" */
	public String material_name = null;
	/** Geresolveerde Bukkit enum (indien material_name is gezet en geldig) */
	public Material material = null;

	/* Optional */
	public String[] lore = null;
	public boolean craftable = false;
	public String required_tech = null;
	public boolean shaped = false;
	public boolean shiny = false;
	public boolean tradeable = false;
	public HashMap<String, ConfigIngredient> ingredients;
	public String[] shape;
	public List<HashMap<String, String>> components = new LinkedList<HashMap<String, String>>();
	public boolean vanilla = false;
	public int amount = 1;
	public double tradeValue = 0;

	@SuppressWarnings("unchecked")
	public static void loadConfig(FileConfiguration cfg, Map<String, ConfigMaterial> materials) {
		materials.clear();
		List<Map<?, ?>> configMaterials = cfg.getMapList("materials");
		for (Map<?, ?> b : configMaterials) {
			ConfigMaterial mat = new ConfigMaterial();

			/* Mandatory / identifiers */
			mat.id = asString(b.get("id"));
			if (mat.id == null || mat.id.isEmpty()) {
				CivLog.warning("Skipping material with missing 'id'.");
				continue;
			}

			/* Result item: modern eerst (material), daarna legacy (item_id/item_data) */
			mat.material_name = asString(b.get("material"));
			if (mat.material_name != null) {
				Material m = Material.matchMaterial(mat.material_name);
				if (m == null) {
					CivLog.warning("Unknown Bukkit material '" + mat.material_name + "' for id:" + mat.id + ". Falling back to item_id.");
				} else {
					mat.material = m;
				}
			}

			Integer legacyItemId = asInteger(b.get("item_id"));
			if (legacyItemId != null) mat.item_id = legacyItemId;
			Integer legacyItemData = asInteger(b.get("item_data"));
			if (legacyItemData != null) mat.item_data = legacyItemData;

			if (mat.material != null && legacyItemId != null) {
				CivLog.warning("Both 'material' and 'item_id' provided for id:" + mat.id + ". Modern 'material' will be preferred by downstream code; legacy kept for compatibility.");
			}

			/* Name (met kleurcodes) */
			mat.name = CivColor.colorize(asString(b.get("name")));

			/* Category & tier */
			String category = asString(b.get("category"));
			if (category != null) {
				mat.category = CivColor.colorize(category);
				mat.categoryCivColortripped = CivColor.stripTags(category);

				String lower = mat.category.toLowerCase();
				if (lower.contains("tier 1") || lower.contains("tier i")) {
					mat.tier = 1;
				} else if (lower.contains("tier 2") || lower.contains("tier ii")) {
					mat.tier = 2;
				} else if (lower.contains("tier 3") || lower.contains("tier iii")) {
					mat.tier = 3;
				} else if (lower.contains("tier 4") || lower.contains("tier iv")) {
					mat.tier = 4;
				} else {
					mat.tier = 0;
				}
			}

			/* Lore */
			List<?> configLore = (List<?>) b.get("lore");
			if (configLore != null) {
				String[] lore = new String[configLore.size()];
				int i = 0;
				for (Object obj : configLore) {
					if (obj instanceof String) {
						lore[i++] = (String) obj;
					}
				}
				mat.lore = lore; // fix: daadwerkelijk op veld zetten
			}

			/* Flags */
			mat.craftable = asBoolean(b.get("craftable"), false);
			mat.shaped    = asBoolean(b.get("shaped"), false);
			mat.shiny     = asBoolean(b.get("shiny"), false);
			mat.tradeable = asBoolean(b.get("tradeable"), false);
			mat.vanilla   = asBoolean(b.get("vanilla"), false);

			/* Waarden */
			Integer amount = asInteger(b.get("amount"));
			if (amount != null) mat.amount = amount;

			Double tValue = asDouble(b.get("tradeValue"));
			if (tValue != null) mat.tradeValue = tValue;

			/* Tech vereisten (let op: sleutel heet 'required_techs' in jullie YAML) */
			String required_tech = asString(b.get("required_techs"));
			if (required_tech != null) {
				mat.required_tech = required_tech;
			}

			/* Componenten */
			List<Map<?, ?>> comps = (List<Map<?, ?>>) b.get("components");
			if (comps != null) {
				for (Map<?, ?> compObj : comps) {
					HashMap<String, String> compMap = new HashMap<String, String>();
					for (Object key : compObj.keySet()) {
						compMap.put(String.valueOf(key), asString(compObj.get(key)));
					}
					mat.components.add(compMap);
				}
			}

			/* Ingrediënten (N.B. nog legacy pad: type_id/data of custom_id) */
			List<Map<?, ?>> configIngredients = (List<Map<?, ?>>) b.get("ingredients");
			if (configIngredients != null) {
				mat.ingredients = new HashMap<String, ConfigIngredient>();

				for (Map<?, ?> ingred : configIngredients) {
					ConfigIngredient ingredient = new ConfigIngredient();

					ingredient.type_id = asIntDefault(ingred.get("type_id"), 0);
					ingredient.data    = asIntDefault(ingred.get("data"), 0);

					String key;
					String custom_id = asString(ingred.get("custom_id"));
					if (custom_id != null) {
						ingredient.custom_id = custom_id;
						key = custom_id;
					} else {
						ingredient.custom_id = null;
						key = "mc_" + ingredient.type_id;
					}

					ingredient.ignore_data = asBoolean(ingred.get("ignore_data"), false);
					Integer count = asInteger(ingred.get("count"));
					if (count != null) ingredient.count = count;

					String letter = asString(ingred.get("letter"));
					if (letter != null) ingredient.letter = letter;

					mat.ingredients.put(key, ingredient);
				}
			}

			/* Shaped recept: optionele shape */
			if (mat.shaped) {
				List<?> configShape = (List<?>) b.get("shape");
				if (configShape != null) {
					String[] shape = new String[configShape.size()];
					int i = 0;
					for (Object obj : configShape) {
						if (obj instanceof String) {
							shape[i++] = (String) obj;
						}
					}
					mat.shape = shape;
				}
			}

			/* Categoriseren & opslaan */
			ConfigMaterialCategory.addMaterial(mat);
			materials.put(mat.id, mat);
		}

		CivLog.info("Loaded " + materials.size() + " Materials.");
	}

	/* =========================
	 *  Helpers (null-safe casts)
	 * ========================= */
	private static String asString(Object o) {
		return (o == null) ? null : String.valueOf(o);
	}

	private static Integer asInteger(Object o) {
		if (o == null) return null;
		if (o instanceof Integer) return (Integer) o;
		if (o instanceof Number) return ((Number) o).intValue();
		try {
			return Integer.valueOf(String.valueOf(o));
		} catch (Exception ignored) {
			return null;
		}
	}

	private static int asIntDefault(Object o, int def) {
		Integer i = asInteger(o);
		return (i == null) ? def : i;
	}

	private static boolean asBoolean(Object o, boolean def) {
		if (o == null) return def;
		if (o instanceof Boolean) return (Boolean) o;
		if (o instanceof String) return Boolean.parseBoolean((String) o);
		return def;
	}

	private static Double asDouble(Object o) {
		if (o == null) return null;
		if (o instanceof Double) return (Double) o;
		if (o instanceof Number) return ((Number) o).doubleValue();
		try {
			return Double.valueOf(String.valueOf(o));
		} catch (Exception ignored) {
			return null;
		}
	}

	/* =========================
	 *  Bestaande API
	 * ========================= */

	public boolean playerHasTechnology(Player player) {
		if (this.required_tech == null) {
			return true;
		}

		com.avrgaming.civcraft.object.Resident resident = CivGlobal.getResident(player);
		if (resident == null || !resident.hasTown()) {
			return false;
		}

		/* Parse technologies */
		String[] split = this.required_tech.split(",");
		for (String tech : split) {
			tech = tech.replace(" ", "");
			if (!resident.getCiv().hasTechnology(tech)) {
				return false;
			}
		}

		return true;
	}

	public String getRequireString() {
		String out = "";
		if (this.required_tech == null) {
			return out;
		}

		/* Parse technologies */
		String[] split = this.required_tech.split(",");
		for (String tech : split) {
			tech = tech.replace(" ", "");
			ConfigTech technology = CivSettings.techs.get(tech);
			if (technology != null) {
				out += technology.name + ", ";
			}
		}

		return out;
	}
}

