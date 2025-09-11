package gpl;

import com.avrgaming.civcraft.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.avrgaming.civcraft.loreenhancements.LoreEnhancement;

import java.util.*;

/**
 * Modern, NMS-free replacement for the legacy 1.12 AttributeUtil used by CivCraft.
 * This implementation stores custom data in PersistentDataContainer, manipulates
 * display name and lore via ItemMeta, and offers minimal shims for the old
 * Attribute/AttributeType API used by some components to build items.
 */
public class AttributeUtil {
    // Backing item
    private ItemStack stack;

    public AttributeUtil(ItemStack stack) {
        this.stack = stack == null ? new ItemStack(org.bukkit.Material.AIR) : stack.clone();
    }

    public ItemStack getStack() {
        return stack;
    }

    // ===================== Utility =====================
    private static Plugin getPlugin() { return Bukkit.getPluginManager().getPlugin("CivCraft"); }
    private static NamespacedKey key(String k) {
        Plugin p = getPlugin();
        if (p != null) return new NamespacedKey(p, ("civ_"+k).toLowerCase());
        return NamespacedKey.minecraft(("civcraft_"+k).toLowerCase());
    }
    private static NamespacedKey enhKey(String name, String subkey) {
        String k = "enh_" + name + (subkey != null ? ("_" + subkey) : "");
        return key(k);
    }

    // ===================== Name/Lore =====================
    public void setName(String name) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            Text.setDisplayName(meta, name);
            stack.setItemMeta(meta);
        }
    }
    public String getName() {
        ItemMeta meta = stack.getItemMeta();
        return meta != null ? Text.getDisplayName(meta) : null;
    }

    public void addLore(String str) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.hasLore() ? new ArrayList<>(Text.getLore(meta)) : new ArrayList<>();
        lore.add(str);
        Text.setLore(meta, lore);
        stack.setItemMeta(meta);
    }
    public void addLore(String[] lore) { if (lore != null) for (String s : lore) addLore(s); }
    public String[] getLore() {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.hasLore()) return null;
        List<String> lore = Text.getLore(meta);
        return lore.toArray(new String[0]);
    }
    public void setLore(String string) { setLore(new String[]{string}); }
    public void setLore(String[] strings) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        Text.setLore(meta, strings == null ? null : Arrays.asList(strings) );
        stack.setItemMeta(meta);
    }
    public void setLore(java.util.LinkedList<String> strings) { setLore(strings == null ? null : strings.toArray(new String[0])); }

    // ===================== CivCraft custom properties (PDC) =====================
    public void setCivCraftProperty(String k, String value) {
        ItemMeta meta = stack.getItemMeta(); if (meta == null) return;
        meta.getPersistentDataContainer().set(key(k), PersistentDataType.STRING, value);
        stack.setItemMeta(meta);
    }
    public String getCivCraftProperty(String k) {
        ItemMeta meta = stack.getItemMeta(); if (meta == null) return null;
        return meta.getPersistentDataContainer().get(key(k), PersistentDataType.STRING);
    }
    public void removeCivCraftProperty(String k) {
        ItemMeta meta = stack.getItemMeta(); if (meta == null) return;
        meta.getPersistentDataContainer().remove(key(k));
        stack.setItemMeta(meta);
    }
    public void removeCivCraftCompound() { /* no-op in PDC model */ }

    // ===================== Enhancements (PDC-backed) =====================
    public void addEnhancement(String enhancementName, String k, String value) {
        ItemMeta meta = stack.getItemMeta(); if (meta == null) return;
        meta.getPersistentDataContainer().set(enhKey(enhancementName, "name"), PersistentDataType.STRING, enhancementName);
        if (k != null) meta.getPersistentDataContainer().set(enhKey(enhancementName, k), PersistentDataType.STRING, value);
        stack.setItemMeta(meta);
    }
    public void setEnhancementData(String enhancementName, String k, String value) { addEnhancement(enhancementName, k, value); }
    public String getEnhancementData(String enhName, String k) {
        ItemMeta meta = stack.getItemMeta(); if (meta == null) return null;
        return meta.getPersistentDataContainer().get(enhKey(enhName, k), PersistentDataType.STRING);
    }
    public boolean hasEnhancement(String enhName) {
        ItemMeta meta = stack.getItemMeta(); if (meta == null) return false;
        return meta.getPersistentDataContainer().has(enhKey(enhName, "name"), PersistentDataType.STRING);
    }
    public boolean hasEnhancements() {
        for (String name : com.avrgaming.civcraft.loreenhancements.LoreEnhancement.enhancements.keySet()) {
            if (hasEnhancement(name)) return true;
        }
        return false;
    }
    public java.util.LinkedList<LoreEnhancement> getEnhancements() {
        java.util.LinkedList<LoreEnhancement> list = new java.util.LinkedList<>();
        for (Map.Entry<String, LoreEnhancement> e : com.avrgaming.civcraft.loreenhancements.LoreEnhancement.enhancements.entrySet()) {
            if (hasEnhancement(e.getKey())) list.add(e.getValue());
        }
        return list;
    }

    // ===================== Leather armor color =====================
    public void setColor(Long color) {
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof LeatherArmorMeta lam) {
            lam.setColor(Color.fromRGB(color.intValue()));
            stack.setItemMeta(lam);
        } else {
            setCivCraftProperty("leather_color", Long.toString(color));
        }
    }
    public int getColor() {
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof LeatherArmorMeta lam) {
            Color c = lam.getColor();
            return c != null ? c.asRGB() : 0;
        }
        String v = getCivCraftProperty("leather_color");
        return v != null ? Integer.parseInt(v) : 0;
    }
    public boolean hasColor() {
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof LeatherArmorMeta lam) {
            return lam.getColor() != null;
        }
        return getCivCraftProperty("leather_color") != null;
    }

    // ===================== Skull owner =====================
    public void setSkullOwner(String name) {
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof SkullMeta skull) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(name));
            stack.setItemMeta(skull);
        }
    }

    // ===================== Item flags =====================
    public void setHideFlag(int flags) {
        ItemMeta meta = stack.getItemMeta(); if (meta == null) return;
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE);
        stack.setItemMeta(meta);
    }

    // ===================== Legacy attribute shim =====================
    public enum Operation { ADD_NUMBER(0), MULTIPLY_PERCENTAGE(1), ADD_PERCENTAGE(2); private final int id; Operation(int id){this.id=id;} public int getId(){return id;} public static Operation fromId(int id){ for (Operation o:values()) if (o.id==id) return o; throw new IllegalArgumentException(); } }
    public static class AttributeType {
        private static final Map<String, AttributeType> LOOKUP = new HashMap<>();
        public static final AttributeType GENERIC_MAX_HEALTH = new AttributeType("generic.maxHealth").register();
        public static final AttributeType GENERIC_FOLLOW_RANGE = new AttributeType("generic.followRange").register();
        public static final AttributeType GENERIC_ATTACK_DAMAGE = new AttributeType("generic.attackDamage").register();
        public static final AttributeType GENERIC_MOVEMENT_SPEED = new AttributeType("generic.movementSpeed").register();
        public static final AttributeType GENERIC_KNOCKBACK_RESISTANCE = new AttributeType("generic.knockbackResistance").register();
        private final String minecraftId; public AttributeType(String id){this.minecraftId=id;} public String getMinecraftId(){return minecraftId;} public AttributeType register(){LOOKUP.putIfAbsent(minecraftId,this); return this;} public static AttributeType fromId(String id){return LOOKUP.get(id);} }
    public static class Attribute {
        private double amount; private Operation operation; private AttributeType type; private String name; private UUID uuid;
        private Attribute(){}
        public double getAmount(){return amount;}
        public void setAmount(double amount){this.amount=amount;}
        public Operation getOperation(){return operation;}
        public void setOperation(Operation o){this.operation=o;}
        public AttributeType getAttributeType(){return type;}
        public void setAttributeType(AttributeType t){this.type=t;}
        public String getName(){return name;}
        public void setName(String n){this.name=n;}
        public UUID getUUID(){return uuid;}
        public void setUUID(UUID id){this.uuid=id;}
        public static Builder newBuilder(){return new Builder();}
        public static class Builder{
            private final Attribute a=new Attribute();
            public Builder amount(double v){a.amount=v;return this;}
            public Builder operation(Operation o){a.operation=o;return this;}
            public Builder type(AttributeType t){a.type=t;return this;}
            public Builder name(String n){a.name=n;return this;}
            public Builder uuid(UUID id){a.uuid=id;return this;}
            public Attribute build(){ if(a.uuid==null) a.uuid=UUID.randomUUID(); if(a.operation==null) a.operation=Operation.ADD_NUMBER; return a; }
        }
    }
    // Accepts legacy attributes but does nothing (Paper handles attributes via ItemMeta now)
    public void add(Attribute attribute) { /* no-op: preserved for API compatibility */ }
}
