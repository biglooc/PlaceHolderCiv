/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package com.avrgaming.civcraft.config;

import localize.Localize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.avrgaming.civcraft.camp.Camp;
import com.avrgaming.civcraft.endgame.ConfigEndCondition;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.exception.InvalidConfiguration;
import com.avrgaming.civcraft.items.units.Unit;
import com.avrgaming.civcraft.loreenhancements.LoreEnhancement;
import com.avrgaming.civcraft.lorestorage.LoreCraftableMaterial;
import com.avrgaming.civcraft.main.CivCraft;
import com.avrgaming.civcraft.main.CivData;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.randomevents.ConfigRandomEvent;
import com.avrgaming.civcraft.structure.FortifiedWall;
import com.avrgaming.civcraft.structure.Wall;
import com.avrgaming.civcraft.template.Template;
import com.avrgaming.global.perks.Perk;

public class CivSettings {

	public static CivCraft plugin;

	public static final long MOB_REMOVE_INTERVAL = 5000;
	public static final int GRACE_DAYS = 3;
	public static final int CIV_DEBT_GRACE_DAYS = 7;
	public static final int CIV_DEBT_SELL_DAYS = 14;
	public static final int CIV_DEBT_TOWN_SELL_DAYS = 21;
	public static final int TOWN_DEBT_GRACE_DAYS = 7;
	public static final int TOWN_DEBT_SELL_DAYS = 14;

	public static float T1_leather_speed;
	public static float T2_leather_speed;
	public static float T3_leather_speed;
	public static float T4_leather_speed;
	public static float T1_metal_speed;
	public static float T2_metal_speed;
	public static float T3_metal_speed;
	public static float T4_metal_speed;
	public static float normal_speed;
	public static double highjump;

	public static FileConfiguration townConfig; /* town.yml */
	public static Map<Integer, ConfigTownLevel> townLevels = new HashMap<>();
	public static Map<String, ConfigTownUpgrade> townUpgrades = new TreeMap<>();

	public static FileConfiguration civConfig; /* civ.yml */
	public static Map<String, ConfigEndCondition> endConditions = new HashMap<>();

	public static FileConfiguration cultureConfig; /* culture.yml */
	public static Map<Integer, ConfigCultureLevel> cultureLevels = new HashMap<>();
	private static Map<String, ConfigCultureBiomeInfo> cultureBiomes = new HashMap<>();

	public static FileConfiguration structureConfig; /* structures.yml */
	public static Map<String, ConfigBuildableInfo> structures = new HashMap<>();
	public static Map<Integer, ConfigGrocerLevel> grocerLevels = new HashMap<>();
	public static Map<Integer, ConfigCottageLevel> cottageLevels = new HashMap<>();
	public static Map<Integer, ConfigMineLevel> mineLevels = new HashMap<>();
	public static Map<Integer, ConfigTempleLevel> templeLevels = new HashMap<>();
	public static Map<Integer, ConfigTradeShipLevel> tradeShipLevels = new HashMap<>();

	public static FileConfiguration wonderConfig; /* wonders.yml */
	public static Map<String, ConfigBuildableInfo> wonders = new HashMap<>();
	public static Map<String, ConfigWonderBuff> wonderBuffs = new HashMap<>();

	public static FileConfiguration techsConfig; /* techs.yml */
	public static Map<String, ConfigTech> techs = new HashMap<>();
	public static Map<Integer, ConfigTechItem> techItems = new HashMap<>();
	public static Map<String, ConfigTechPotion> techPotions = new HashMap<>();


	public static FileConfiguration goodsConfig; /* goods.yml */
	public static Map<String, ConfigTradeGood> goods = new HashMap<>();
	public static Map<String, ConfigTradeGood> landGoods = new HashMap<>();
	public static Map<String, ConfigTradeGood> waterGoods = new HashMap<>();
	public static Map<String, ConfigHemisphere> hemispheres = new HashMap<>();

	public static FileConfiguration buffConfig;
	public static Map<String, ConfigBuff> buffs = new HashMap<>();

	public static FileConfiguration unitConfig;
	public static Map<String, ConfigUnit> units = new HashMap<>();

	public static FileConfiguration espionageConfig;
	public static Map<String, ConfigMission> missions = new HashMap<>();

	public static FileConfiguration governmentConfig; /* governments.yml */
	public static Map<String, ConfigGovernment> governments = new HashMap<>();

	public static HashSet<Material> switchItems = new HashSet<>();
	public static Map<Material, Integer> restrictedItems = new HashMap<>();
	public static Map<Material, Integer> blockPlaceExceptions = new HashMap<>();
	public static Map<EntityType, Integer> restrictedSpawns = new HashMap<>();
	public static HashSet<EntityType> playerEntityWeapons = new HashSet<>();
	public static HashSet<Integer> alwaysCrumble = new HashSet<>();

	public static FileConfiguration warConfig; /* war.yml */

	public static FileConfiguration scoreConfig; /* score.yml */

	public static FileConfiguration perkConfig; /* perks.yml */
	public static Map<String, ConfigPerk> perks = new HashMap<>();
	public static Map<String, ConfigPerk> templates = new HashMap<>();

	public static FileConfiguration enchantConfig; /* enchantments.yml */
	public static Map<String, ConfigEnchant> enchants = new HashMap<>();
	public static float speedtoe_speed;
	public static double speedtoe_consume;
	public static int thorhammerchance;
	public static int punchoutchance;

	public static FileConfiguration campConfig; /* camp.yml */
	public static Map<Integer, ConfigCampLonghouseLevel> longhouseLevels = new HashMap<>();
	public static Map<String, ConfigCampUpgrade> campUpgrades = new HashMap<>();

	public static FileConfiguration marketConfig; /* market.yml */
	public static Map<Integer, ConfigMarketItem> marketItems = new HashMap<>();

	public static Set<ConfigStableItem> stableItems = new HashSet<>();
	public static HashMap<Integer, ConfigStableHorse> horses = new HashMap<>();

	public static FileConfiguration happinessConfig; /* happiness.yml */
	public static HashMap<Integer, ConfigTownHappinessLevel> townHappinessLevels = new HashMap<>();
	public static HashMap<Integer, ConfigHappinessState> happinessStates = new HashMap<>();

	public static FileConfiguration materialsConfig; /* materials.yml */
	public static HashMap<String, ConfigMaterial> materials = new HashMap<>();

	public static FileConfiguration randomEventsConfig; /* randomevents.yml */
	public static HashMap<String, ConfigRandomEvent> randomEvents = new HashMap<>();
	public static ArrayList<String> randomEventIDs = new ArrayList<>();

	public static FileConfiguration nocheatConfig; /* nocheat.yml */
	public static HashMap<String, ConfigValidMod> validMods = new HashMap<>();

	public static FileConfiguration arenaConfig; /* arena.yml */
	public static HashMap<String, ConfigArena> arenas = new HashMap<>();

	public static FileConfiguration fishingConfig; /* fishing.yml */
	public static ArrayList<ConfigFishing> fishingDrops = new ArrayList<>();

	public static double iron_rate;
	public static double gold_rate;
	public static double diamond_rate;
	public static double emerald_rate;
	public static double startingCoins;

	public static ArrayList<String> kitItems = new ArrayList<>();
	public static HashMap<Integer, ConfigRemovedRecipes> removedRecipies = new HashMap<>();
	public static HashSet<Material> restrictedUndoBlocks = new HashSet<>();
	public static boolean hasVanishNoPacket = false;

	public static final String MINI_ADMIN = "civ.admin";
	public static final String HACKER = "civ.hacker";
	public static final String MODERATOR = "civ.moderator";
	public static final String FREE_PERKS = "civ.freeperks";
	public static final String ECON = "civ.econ";
	public static final String TPALLY = "civ.tp.ally";
	public static final String TPNEUTRAL = "civ.tp.neutral";
	public static final String TPHOSTILE = "civ.tp.hostile";
	public static final String TPWAR = "civ.tp.war";
	public static final String TPPEACE = "civ.tp.peace";
	public static final String TPCAMP = "civ.tp.camp";
	public static final String TPALL = "civ.tp.*";
	public static final int MARKET_COIN_STEP = 5;
	public static final int MARKET_BUYSELL_COIN_DIFF = 30;
	public static final int MARKET_STEP_THRESHOLD = 2;
	public static String CURRENCY_NAME;

	public static Localize localize;

	public static boolean hasTitleAPI = false;
	public static boolean hasITag = false;

	public static boolean hasMobIntergration = false;
	public static com.avrgaming.civcraft.mobs.MobIntegration mobIntegration = new com.avrgaming.civcraft.mobs.NoopMobIntegration();

	public static Material previewMaterial = Material.GLASS;
	public static Boolean showPreview = true;

	// ---------- INIT ----------

	public static void init(JavaPlugin pl) throws FileNotFoundException, IOException, InvalidConfigurationException, InvalidConfiguration {
		plugin = (CivCraft) pl;
		plugin.getDataFolder().mkdirs();

		// Localization
		String languageFile = CivSettings.getStringBase("localization_file");
		localize = new Localize(plugin, languageFile);
		CivLog.debug(localize.localizedString("welcome_string", "test", 1337, 100.50));
		CURRENCY_NAME = localize.localizedString("civ_currencyName");
		CivGlobal.fullMessage = CivSettings.localize.localizedString("civGlobal_serverFullMsg");

		// Ensure plugin data folders exist
		CivSettings.validateFiles();

		initRestrictedItems();
		initRestrictedUndoBlocks();
		initSwitchItems();
		initRestrictedSpawns();
		initBlockPlaceExceptions();
		initPlayerEntityWeapons();

		loadConfigFiles();
		loadConfigObjects();

		Perk.init();
		Unit.init();

		CivSettings.T1_leather_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T1_leather_speed");
		CivSettings.T2_leather_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T2_leather_speed");
		CivSettings.T3_leather_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T3_leather_speed");
		CivSettings.T4_leather_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T4_leather_speed");
		CivSettings.T1_metal_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T1_metal_speed");
		CivSettings.T2_metal_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T2_metal_speed");
		CivSettings.T3_metal_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T3_metal_speed");
		CivSettings.T4_metal_speed = (float) CivSettings.getDouble(CivSettings.unitConfig, "base.T4_metal_speed");
		CivSettings.normal_speed = 0.2f;

		for (Object obj : civConfig.getList("global.start_kit")) {
			if (obj instanceof String) {
				kitItems.add((String) obj);
			}
		}

		CivGlobal.banWords.add("fuck");
		CivGlobal.banWords.add("shit");
		CivGlobal.banWords.add("nigger");
		CivGlobal.banWords.add("faggot");
		CivGlobal.banWords.add("gay");
		CivGlobal.banWords.add("rape");
		CivGlobal.banWords.add("http");
		CivGlobal.banWords.add("cunt");

		iron_rate = CivSettings.getDouble(civConfig, "ore_rates.iron");
		gold_rate = CivSettings.getDouble(civConfig, "ore_rates.gold");
		diamond_rate = CivSettings.getDouble(civConfig, "ore_rates.diamond");
		emerald_rate = CivSettings.getDouble(civConfig, "ore_rates.emerald");
		startingCoins = CivSettings.getDouble(civConfig, "global.starting_coins");

		alwaysCrumble.add(CivData.BEDROCK);
		alwaysCrumble.add(CivData.COAL_BLOCK);
		alwaysCrumble.add(CivData.EMERALD_BLOCK);
		alwaysCrumble.add(CivData.LAPIS_BLOCK);
		alwaysCrumble.add(CivData.SPONGE);
		alwaysCrumble.add(CivData.HAY_BALE);
		alwaysCrumble.add(CivData.GOLD_BLOCK);
		alwaysCrumble.add(CivData.DIAMOND_BLOCK);
		alwaysCrumble.add(CivData.IRON_BLOCK);
		alwaysCrumble.add(CivData.REDSTONE_BLOCK);
		alwaysCrumble.add(CivData.ENDER_CHEST);
		alwaysCrumble.add(CivData.BEACON);

		LoreEnhancement.init();
		LoreCraftableMaterial.buildStaticMaterials();
		LoreCraftableMaterial.buildRecipes();
		Template.initAttachableTypes();

		if (plugin.hasPlugin("VanishNoPacket")) {
			hasVanishNoPacket = true;
			CivLog.info("VanishNoPacket hooks enabled");
		} else {
			CivLog.warning("VanishNoPacket not found, not registering VanishNoPacket hooks. This is fine if you're not using VanishNoPacket.");
		}

		if (plugin.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
			CivSettings.mobIntegration = new com.avrgaming.civcraft.mobs.MythicMobsAdapter();
			CivSettings.hasMobIntergration = CivSettings.mobIntegration.isEnabled();
			CivLog.info("MythicMobs hooks enabled");
		} else {
			CivLog.warning("MythicMobs not found or disabled; skipping mob intergration");
			CivSettings.mobIntegration = new com.avrgaming.civcraft.mobs.NoopMobIntegration();
			CivSettings.hasMobIntergration = false;
		}

		try {
			String materialName = CivSettings.getString(structureConfig, "previewBlock");
			previewMaterial = Material.getMaterial(materialName);
		} catch (InvalidConfiguration e) {
			CivLog.warning("Unable to change Preview Block. Defaulting to Glass.");
		}

		try {
			showPreview = CivSettings.getBoolean(structureConfig, "shouldShowPreview");
		} catch (InvalidConfiguration e) {
			CivLog.warning("Unable to change Structure Preview Settings. Defaulting to True.");
		}
	}

	// ---------- RESOURCE / CONFIG HELPERS ----------

	/** Ensure plugins/<Plugin>/data exists. */
	public static void validateFiles() {
		File data = new File(plugin.getDataFolder(), "data");
		if (!data.exists()) {
			data.mkdirs();
		}
	}

	/** Copy a bundled resource from the JAR to plugins/<plugin> (NO leading '/'). */
	public static void saveBundled(String resourcePath, boolean replace) throws IOException {
		if (resourcePath.startsWith("/")) resourcePath = resourcePath.substring(1);

		if (plugin.getResource(resourcePath) == null) {
			CivLog.error("Bundled resource missing in JAR: " + resourcePath);
			return;
		}
		File out = new File(plugin.getDataFolder(), resourcePath);
		File parent = out.getParentFile();
		if (parent != null && !parent.exists()) parent.mkdirs();

		if (out.exists() && !replace) return;
		plugin.saveResource(resourcePath, true);
	}

	/** Back-compat: old call sites may still call this with or without a leading '/'. */
	public static void streamResourceToFile(String filepath) {
		try {
			if (filepath.startsWith("/")) filepath = filepath.substring(1);
			saveBundled(filepath, false);
		} catch (IOException e) {
			CivLog.error("Failed to copy bundled resource: " + filepath);
			e.printStackTrace();
		}
	}

	/** Back-compat: some legacy code called this name; delegate to saveBundled. */
	public static void streamResourceToDisk(String filepath) {
		streamResourceToFile(filepath);
	}

	/** Load YAML under plugins/<plugin>/data/<fileName>. If missing, copy it first from the JAR. */
	public static FileConfiguration loadCivConfig(String fileName)
			throws IOException, InvalidConfigurationException {

		final String resourcePath = "data/" + fileName; // NO leading '/'
		final File out = new File(plugin.getDataFolder(), resourcePath);

		File parent = out.getParentFile();
		if (parent != null && !parent.exists()) parent.mkdirs();

		if (!out.exists()) {
			CivLog.warning("Configuration file:" + fileName + " was missing. Copying from JAR.");
			saveBundled(resourcePath, false);
		}

		CivLog.info("Loading Configuration file:" + fileName);
		YamlConfiguration cfg = new YamlConfiguration();
		cfg.load(out);
		return cfg;
	}

	/** Backwards-compat: legacy capitalized variant. */
	public static FileConfiguration LoadConfig(String filename)
			throws IOException, InvalidConfigurationException {
		return loadCivConfig(filename);
	}

	// ---------- RELOADERS ----------

	public static void reloadGovConfigFiles() throws IOException, InvalidConfigurationException, InvalidConfiguration {
		governments.clear();
		governmentConfig = loadCivConfig("governments.yml");
		ConfigGovernment.loadConfig(governmentConfig, governments);
	}

	private static void loadConfigFiles() throws IOException, InvalidConfigurationException {
		townConfig = loadCivConfig("town.yml");
		civConfig = loadCivConfig("civ.yml");
		cultureConfig = loadCivConfig("culture.yml");
		structureConfig = loadCivConfig("structures.yml");
		techsConfig = loadCivConfig("techs.yml");
		goodsConfig = loadCivConfig("goods.yml");
		buffConfig = loadCivConfig("buffs.yml");
		governmentConfig = loadCivConfig("governments.yml");
		warConfig = loadCivConfig("war.yml");
		wonderConfig = loadCivConfig("wonders.yml");
		unitConfig = loadCivConfig("units.yml");
		espionageConfig = loadCivConfig("espionage.yml");
		scoreConfig = loadCivConfig("score.yml");
		perkConfig = loadCivConfig("perks.yml");
		enchantConfig = loadCivConfig("enchantments.yml");
		campConfig = loadCivConfig("camp.yml");
		marketConfig = loadCivConfig("market.yml");
		happinessConfig = loadCivConfig("happiness.yml");
		materialsConfig = loadCivConfig("materials.yml");
		randomEventsConfig = loadCivConfig("randomevents.yml");
		nocheatConfig = loadCivConfig("nocheat.yml");
		arenaConfig = loadCivConfig("arena.yml");
		fishingConfig = loadCivConfig("fishing.yml");
	}

	public static void reloadPerks() throws IOException, InvalidConfigurationException, InvalidConfiguration {
		perkConfig = loadCivConfig("perks.yml");
		ConfigPerk.loadConfig(perkConfig, perks);
		ConfigPerk.loadTemplates(perkConfig, templates);
	}

	public static void reloadNoCheat() throws IOException, InvalidConfigurationException, InvalidConfiguration {
		nocheatConfig = loadCivConfig("nocheat.yml");
		ConfigValidMod.loadConfig(nocheatConfig, validMods);
	}

	// ---------- LOAD OBJECTS ----------

	private static void loadConfigObjects() throws InvalidConfiguration {
		ConfigTownLevel.loadConfig(townConfig, townLevels);
		ConfigTownUpgrade.loadConfig(townConfig, townUpgrades);
		ConfigCultureLevel.loadConfig(cultureConfig, cultureLevels);
		ConfigBuildableInfo.loadConfig(structureConfig, "structures", structures, false);
		ConfigBuildableInfo.loadConfig(wonderConfig, "wonders", wonders, true);
		ConfigTech.loadConfig(techsConfig, techs);
		ConfigTechItem.loadConfig(techsConfig, techItems);
		ConfigTechPotion.loadConfig(techsConfig, techPotions);
		ConfigHemisphere.loadConfig(goodsConfig, hemispheres);
		ConfigBuff.loadConfig(buffConfig, buffs);
		ConfigWonderBuff.loadConfig(wonderConfig, wonderBuffs);
		ConfigTradeGood.loadConfig(goodsConfig, goods, landGoods, waterGoods);
		ConfigGrocerLevel.loadConfig(structureConfig, grocerLevels);
		ConfigCottageLevel.loadConfig(structureConfig, cottageLevels);
		ConfigTempleLevel.loadConfig(structureConfig, templeLevels);
		ConfigMineLevel.loadConfig(structureConfig, mineLevels);
		ConfigGovernment.loadConfig(governmentConfig, governments);
		ConfigEnchant.loadConfig(enchantConfig, enchants);
		ConfigUnit.loadConfig(unitConfig, units);
		ConfigMission.loadConfig(espionageConfig, missions);
		ConfigPerk.loadConfig(perkConfig, perks);
		ConfigPerk.loadTemplates(perkConfig, templates);
		ConfigCampLonghouseLevel.loadConfig(campConfig, longhouseLevels);
		ConfigCampUpgrade.loadConfig(campConfig, campUpgrades);
		ConfigMarketItem.loadConfig(marketConfig, marketItems);
		ConfigStableItem.loadConfig(structureConfig, stableItems);
		ConfigStableHorse.loadConfig(structureConfig, horses);
		ConfigTownHappinessLevel.loadConfig(happinessConfig, townHappinessLevels);
		ConfigHappinessState.loadConfig(happinessConfig, happinessStates);
		ConfigCultureBiomeInfo.loadConfig(cultureConfig, cultureBiomes);
		ConfigMaterial.loadConfig(materialsConfig, materials);
		ConfigRandomEvent.loadConfig(randomEventsConfig, randomEvents, randomEventIDs);
		ConfigEndCondition.loadConfig(civConfig, endConditions);
		ConfigValidMod.loadConfig(nocheatConfig, validMods);
		ConfigArena.loadConfig(arenaConfig, arenas);
		ConfigFishing.loadConfig(fishingConfig, fishingDrops);
		ConfigTradeShipLevel.loadConfig(structureConfig, tradeShipLevels);

		ConfigRemovedRecipes.removeRecipes(materialsConfig, removedRecipies);
		CivGlobal.tradeGoodPreGenerator.preGenerate();
		Wall.init_settings();
		FortifiedWall.init_settings();
	}

	// ---------- STATIC INITIALIZERS ----------

	private static void initRestrictedSpawns() {
		restrictedSpawns.put(EntityType.BLAZE, 0);
		restrictedSpawns.put(EntityType.CAVE_SPIDER, 0);
		restrictedSpawns.put(EntityType.CREEPER, 0);
		restrictedSpawns.put(EntityType.ENDER_DRAGON, 0);
		restrictedSpawns.put(EntityType.ENDERMAN, 0);
		restrictedSpawns.put(EntityType.GHAST, 0);
		restrictedSpawns.put(EntityType.GIANT, 0);
		restrictedSpawns.put(EntityType.PIG_ZOMBIE, 0);
		restrictedSpawns.put(EntityType.SILVERFISH, 0);
		restrictedSpawns.put(EntityType.SKELETON, 0);
		restrictedSpawns.put(EntityType.SLIME, 0);
		restrictedSpawns.put(EntityType.SPIDER, 0);
		restrictedSpawns.put(EntityType.WITCH, 0);
		restrictedSpawns.put(EntityType.WITHER, 0);
		restrictedSpawns.put(EntityType.ZOMBIE, 0);
		restrictedSpawns.put(EntityType.BAT, 0);
		restrictedSpawns.put(EntityType.ENDERMITE, 0);
		restrictedSpawns.put(EntityType.GUARDIAN, 0);
		restrictedSpawns.put(EntityType.HUSK, 0);
		restrictedSpawns.put(EntityType.STRAY, 0);
		restrictedSpawns.put(EntityType.ZOMBIE_VILLAGER, 0);
	}

	private static void initRestrictedItems() {
		restrictedItems.put(Material.FLINT_AND_STEEL, 0);
		restrictedItems.put(Material.BUCKET, 0);
		restrictedItems.put(Material.WATER_BUCKET, 0);
		restrictedItems.put(Material.LAVA_BUCKET, 0);
		restrictedItems.put(Material.CAKE_BLOCK, 0);
		restrictedItems.put(Material.CAULDRON, 0);
		restrictedItems.put(Material.DIODE, 0);
		restrictedItems.put(Material.INK_SACK, 0);
		restrictedItems.put(Material.ITEM_FRAME, 0);
		restrictedItems.put(Material.PAINTING, 0);
		restrictedItems.put(Material.SHEARS, 0);
		restrictedItems.put(Material.STATIONARY_LAVA, 0);
		restrictedItems.put(Material.STATIONARY_WATER, 0);
		restrictedItems.put(Material.TNT, 0);
	}

	private static void initRestrictedUndoBlocks() {
		restrictedUndoBlocks.add(Material.CROPS);
		restrictedUndoBlocks.add(Material.CARROT);
		restrictedUndoBlocks.add(Material.POTATO);
		restrictedUndoBlocks.add(Material.REDSTONE);
		restrictedUndoBlocks.add(Material.REDSTONE_WIRE);
		restrictedUndoBlocks.add(Material.REDSTONE_TORCH_OFF);
		restrictedUndoBlocks.add(Material.REDSTONE_TORCH_ON);
		restrictedUndoBlocks.add(Material.DIODE_BLOCK_OFF);
		restrictedUndoBlocks.add(Material.DIODE_BLOCK_ON);
		restrictedUndoBlocks.add(Material.REDSTONE_COMPARATOR_OFF);
		restrictedUndoBlocks.add(Material.REDSTONE_COMPARATOR_ON);
		restrictedUndoBlocks.add(Material.REDSTONE_COMPARATOR);
		restrictedUndoBlocks.add(Material.STRING);
		restrictedUndoBlocks.add(Material.TRIPWIRE);
		restrictedUndoBlocks.add(Material.SUGAR_CANE_BLOCK);
		restrictedUndoBlocks.add(Material.BEETROOT_SEEDS);
		restrictedUndoBlocks.add(Material.LONG_GRASS);
		restrictedUndoBlocks.add(Material.RED_ROSE);
		restrictedUndoBlocks.add(Material.RED_MUSHROOM);
		restrictedUndoBlocks.add(Material.DOUBLE_PLANT);
		restrictedUndoBlocks.add(Material.CAKE_BLOCK);
		restrictedUndoBlocks.add(Material.CACTUS);
		restrictedUndoBlocks.add(Material.PISTON_BASE);
		restrictedUndoBlocks.add(Material.PISTON_EXTENSION);
		restrictedUndoBlocks.add(Material.PISTON_MOVING_PIECE);
		restrictedUndoBlocks.add(Material.PISTON_STICKY_BASE);
		restrictedUndoBlocks.add(Material.TRIPWIRE_HOOK);
		restrictedUndoBlocks.add(Material.SAPLING);
		restrictedUndoBlocks.add(Material.PUMPKIN_STEM);
		restrictedUndoBlocks.add(Material.MELON_STEM);
	}

	private static void initPlayerEntityWeapons() {
		playerEntityWeapons.add(EntityType.PLAYER);
		playerEntityWeapons.add(EntityType.ARROW);
		playerEntityWeapons.add(EntityType.SPECTRAL_ARROW);
		playerEntityWeapons.add(EntityType.TIPPED_ARROW);
		playerEntityWeapons.add(EntityType.EGG);
		playerEntityWeapons.add(EntityType.SNOWBALL);
		playerEntityWeapons.add(EntityType.SPLASH_POTION);
		playerEntityWeapons.add(EntityType.LINGERING_POTION);
		playerEntityWeapons.add(EntityType.FISHING_HOOK);
	}

	private static void initSwitchItems() {
		switchItems.add(Material.ANVIL);
		switchItems.add(Material.BEACON);
		switchItems.add(Material.BREWING_STAND);
		switchItems.add(Material.BURNING_FURNACE);
		switchItems.add(Material.CAKE_BLOCK);
		switchItems.add(Material.CAULDRON);
		switchItems.add(Material.CHEST);
		switchItems.add(Material.TRAPPED_CHEST);
		switchItems.add(Material.COMMAND);
		switchItems.add(Material.DIODE);
		switchItems.add(Material.DIODE_BLOCK_OFF);
		switchItems.add(Material.DIODE_BLOCK_ON);
		switchItems.add(Material.DISPENSER);
		switchItems.add(Material.FENCE_GATE);
		switchItems.add(Material.FURNACE);
		switchItems.add(Material.JUKEBOX);
		switchItems.add(Material.LEVER);
		switchItems.add(Material.STONE_BUTTON);
		switchItems.add(Material.STONE_PLATE);
		switchItems.add(Material.IRON_DOOR);
		switchItems.add(Material.TNT);
		switchItems.add(Material.TRAP_DOOR);
		switchItems.add(Material.WOOD_DOOR);
		switchItems.add(Material.WOODEN_DOOR);
		switchItems.add(Material.WOOD_PLATE);

		// 1.5 additions.
		switchItems.add(Material.HOPPER);
		switchItems.add(Material.HOPPER_MINECART);
		switchItems.add(Material.DROPPER);
		switchItems.add(Material.REDSTONE_COMPARATOR);
		switchItems.add(Material.REDSTONE_COMPARATOR_ON);
		switchItems.add(Material.REDSTONE_COMPARATOR_OFF);
		switchItems.add(Material.TRAPPED_CHEST);
		switchItems.add(Material.GOLD_PLATE);
		switchItems.add(Material.IRON_PLATE);
		switchItems.add(Material.IRON_TRAPDOOR);

		// 1.6 additions.
		switchItems.add(Material.SPRUCE_DOOR);
		switchItems.add(Material.BIRCH_DOOR);
		switchItems.add(Material.JUNGLE_DOOR);
		switchItems.add(Material.ACACIA_DOOR);
		switchItems.add(Material.DARK_OAK_DOOR);

		// 1.7 additions
		switchItems.add(Material.ACACIA_FENCE_GATE);
		switchItems.add(Material.BIRCH_FENCE_GATE);
		switchItems.add(Material.DARK_OAK_FENCE_GATE);
		switchItems.add(Material.SPRUCE_FENCE_GATE);
		switchItems.add(Material.JUNGLE_FENCE_GATE);
	}

	private static void initBlockPlaceExceptions() {
		blockPlaceExceptions.put(Material.FIRE, 0);
		blockPlaceExceptions.put(Material.PORTAL, 0);
	}

	// ---------- GETTERS / HELPERS ----------

	public static String getStringBase(String path) throws InvalidConfiguration {
		return getString(plugin.getConfig(), path);
	}

	public static double getDoubleTown(String path) throws InvalidConfiguration {
		return getDouble(townConfig, path);
	}

	public static double getDoubleCiv(String path) throws InvalidConfiguration {
		return getDouble(civConfig, path);
	}

	public static void saveGenID(String gen_id) {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("plugins/CivCraft/genid.data")));
			writer.write(gen_id);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getGenID() {
		String genid = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader("plugins/CivCraft/genid.data"));
			genid = br.readLine();
			br.close();
		} catch (IOException ignored) { }
		return genid;
	}

	public static Double getDoubleStructure(String path) {
		Double ret;
		try {
			ret = getDouble(structureConfig, path);
		} catch (InvalidConfiguration e) {
			ret = 0.0;
			e.printStackTrace();
		}
		return ret;
	}

	public static Boolean getBooleanStructure(String path) {
		Boolean ret;
		try {
			ret = getBoolean(structureConfig, path);
		} catch (InvalidConfiguration e) {
			ret = false;
			e.printStackTrace();
		}
		return ret;
	}

	public static int getIntegerStructure(String path) {
		Integer ret;
		try {
			ret = getInteger(structureConfig, path);
		} catch (InvalidConfiguration e) {
			ret = 0;
			e.printStackTrace();
		}
		return ret;
	}

	public static Integer getIntegerGovernment(String path) {
		Integer ret;
		try {
			ret = getInteger(governmentConfig, path);
		} catch (InvalidConfiguration e) {
			ret = 0;
			e.printStackTrace();
		}
		return ret;
	}

	public static Integer getInteger(FileConfiguration cfg, String path) throws InvalidConfiguration {
		if (!cfg.contains(path)) {
			throw new InvalidConfiguration("Could not get configuration integer " + path);
		}
		return cfg.getInt(path);
	}

	public static String getString(FileConfiguration cfg, String path) throws InvalidConfiguration {
		String data = cfg.getString(path);
		if (data == null) {
			throw new InvalidConfiguration("Could not get configuration string " + path);
		}
		return data;
	}

	public static double getDouble(FileConfiguration cfg, String path) throws InvalidConfiguration {
		if (!cfg.contains(path)) {
			throw new InvalidConfiguration("Could not get configuration double " + path);
		}
		return cfg.getDouble(path);
	}

	public static boolean getBoolean(FileConfiguration cfg, String path) throws InvalidConfiguration {
		if (!cfg.contains(path)) {
			throw new InvalidConfiguration("Could not get configuration boolean " + path);
		}
		return cfg.getBoolean(path);
	}

	public static int getMaxNameLength() {
		return 32;
	}

	public static String getNameCheckRegex() throws InvalidConfiguration {
		return getStringBase("regex.name_check_regex");
	}

	public static String getNameFilterRegex() throws InvalidConfiguration {
		return getStringBase("regex.name_filter_regex");
	}

	public static String getNameRemoveRegex() throws InvalidConfiguration {
		return getStringBase("regex.name_remove_regex");
	}

	public static ConfigTownUpgrade getUpgradeByName(String name) {
		for (ConfigTownUpgrade upgrade : townUpgrades.values()) {
			if (upgrade.name.equalsIgnoreCase(name)) {
				return upgrade;
			}
		}
		return null;
	}

	public static ConfigHappinessState getHappinessState(double amount) {
		ConfigHappinessState closestState = happinessStates.get(0);

		for (int i = 0; i < happinessStates.size(); i++) {
			ConfigHappinessState state = happinessStates.get(i);
			amount = (double) Math.round(amount * 100) / 100;
			if (amount >= state.amount) {
				closestState = state;
			}
		}
		return closestState;
	}

	public static ConfigTownUpgrade getUpgradeByNameRegex(Town town, String name) throws CivException {
		ConfigTownUpgrade returnUpgrade = null;
		for (ConfigTownUpgrade upgrade : townUpgrades.values()) {
			if (!upgrade.isAvailable(town)) continue;

			if (name.equalsIgnoreCase(upgrade.name)) {
				return upgrade;
			}

			String loweredUpgradeName = upgrade.name.toLowerCase();
			String loweredName = name.toLowerCase();

			if (loweredUpgradeName.contains(loweredName)) {
				if (returnUpgrade == null) {
					returnUpgrade = upgrade;
				} else {
					throw new CivException(CivSettings.localize.localizedString("var_cmd_notSpecificUpgrade", name));
				}
			}
		}
		return returnUpgrade;
	}

	public static ConfigCampUpgrade getCampUpgradeByNameRegex(Camp camp, String name) throws CivException {
		ConfigCampUpgrade returnUpgrade = null;
		for (ConfigCampUpgrade upgrade : campUpgrades.values()) {
			if (!upgrade.isAvailable(camp)) continue;

			if (name.equalsIgnoreCase(upgrade.name)) {
				return upgrade;
			}

			String loweredUpgradeName = upgrade.name.toLowerCase();
			String loweredName = name.toLowerCase();

			if (loweredUpgradeName.contains(loweredName)) {
				if (returnUpgrade == null) {
					returnUpgrade = upgrade;
				} else {
					throw new CivException(CivSettings.localize.localizedString("var_cmd_notSpecificUpgrade", name));
				}
			}
		}
		return returnUpgrade;
	}

	public static ConfigBuildableInfo getBuildableInfoByName(String fullArgs) {
		for (ConfigBuildableInfo sinfo : structures.values()) {
			if (sinfo.displayName.equalsIgnoreCase(fullArgs)) {
				return sinfo;
			}
		}

		for (ConfigBuildableInfo sinfo : wonders.values()) {
			if (sinfo.displayName.equalsIgnoreCase(fullArgs)) {
				return sinfo;
			}
		}

		return null;
	}

	public static ConfigTech getTechByName(String techname) {
		for (ConfigTech tech : techs.values()) {
			if (tech.name.equalsIgnoreCase(techname)) {
				return tech;
			}
		}
		return null;
	}

	public static int getCottageMaxLevel() {
		int returnLevel = 0;
		for (Integer level : cottageLevels.keySet()) {
			if (returnLevel < level) returnLevel = level;
		}
		return returnLevel;
	}

	public static int getTempleMaxLevel() {
		int returnLevel = 0;
		for (Integer level : templeLevels.keySet()) {
			if (returnLevel < level) returnLevel = level;
		}
		return returnLevel;
	}

	public static int getMineMaxLevel() {
		int returnLevel = 0;
		for (Integer level : mineLevels.keySet()) {
			if (returnLevel < level) returnLevel = level;
		}
		return returnLevel;
	}

	public static int getMaxCultureLevel() {
		int returnLevel = 0;
		for (Integer level : cultureLevels.keySet()) {
			if (returnLevel < level) returnLevel = level;
		}
		return returnLevel;
	}

	public static ConfigCultureBiomeInfo getCultureBiome(String name) {
		ConfigCultureBiomeInfo biomeInfo = cultureBiomes.get(name);
		if (biomeInfo == null) {
			biomeInfo = cultureBiomes.get("UNKNOWN");
		}
		return biomeInfo;
	}
}

