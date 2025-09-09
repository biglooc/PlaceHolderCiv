package com.avrgaming.civcraft.config;


import org.bukkit.Material;

public class ConfigIngredient {
	public int type_id;
	public int data;

	/* optional */
	public String custom_id;
	public int count = 1;
	public String letter;
	public boolean ignore_data;

	// NEW (modern API)
	public String material_name; // YAML "material"
	public Material material;    // resolved enum
}
