package gpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Llama;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.avrgaming.civcraft.main.CivCraft;
import com.avrgaming.civcraft.main.CivLog;

/**
 * Simplified HorseModifier for Paper/Spigot 1.21+
 * Replaces NMS usage with Bukkit API.
 */
public class HorseModifier {
    private final AbstractHorse bukkitHorse;

    public static String HORSE_META = "civcrafthorse";
    private static final UUID movementSpeedUID = UUID.fromString("206a89dc-ae78-4c4d-b42c-3b31db3f5a7c");

    public HorseModifier(LivingEntity horse) {
        if (!(horse instanceof AbstractHorse)) {
            throw new IllegalArgumentException("Entity has to be an AbstractHorse!");
        }
        this.bukkitHorse = (AbstractHorse) horse;
    }

    private HorseModifier(AbstractHorse horse) {
        this.bukkitHorse = horse;
    }

    public static HorseModifier spawn(Location loc) {
        // Default spawn as Horse; caller may change type later via setType
        AbstractHorse horse = loc.getWorld().spawn(loc, Horse.class);
        return new HorseModifier(horse);
    }

    public static boolean isHorse(LivingEntity le) {
        return le instanceof AbstractHorse;
    }

    public static void setHorseSpeed(LivingEntity entity, double amount) {
        if (!(entity instanceof AbstractHorse)) return;
        AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attr != null) {
            attr.setBaseValue(amount);
        }
    }

    public static void setCivCraftHorse(LivingEntity entity) {
        entity.setMetadata(HorseModifier.HORSE_META, new FixedMetadataValue(CivCraft.getPlugin(), HorseModifier.HORSE_META));
    }

    public static boolean isCivCraftHorse(LivingEntity entity) {
        if (!entity.hasMetadata(HORSE_META)) {
            CivLog.debug("Player tried using Horse without meta: " + HORSE_META);
            return false;
        }
        return entity instanceof AbstractHorse;
    }

    public void setType(HorseType type) {
        AbstractHorse current = this.bukkitHorse;
        Location loc = current.getLocation();
        Player owner = (Player) current.getOwner();
        boolean tamed = current.isTamed();
        // Replace entity with desired subtype if needed
        AbstractHorse newHorse;
        switch (type) {
            case NORMAL:
                newHorse = replace(loc, current, Horse.class);
                break;
            case DONKEY:
                newHorse = replace(loc, current, Donkey.class);
                break;
            case MULE:
                newHorse = replace(loc, current, Mule.class);
                break;
            case UNDEAD:
            case SKELETAL:
                // Not supported in modern API; keep as Horse
                newHorse = current;
                break;
            default:
                newHorse = current;
        }
        if (newHorse != current) {
            if (owner != null) newHorse.setOwner(owner);
            newHorse.setTamed(tamed);
        }
    }

    private <T extends AbstractHorse> T replace(Location loc, AbstractHorse old, Class<T> clazz) {
        T spawned = loc.getWorld().spawn(loc, clazz);
        spawned.getInventory().setSaddle(old.getInventory().getSaddle());
        spawned.setOwner(old.getOwner());
        spawned.setTamed(old.isTamed());
        old.remove();
        return spawned;
    }

    public void setChested(boolean chested) {
        if (bukkitHorse instanceof Mule) {
            ((Mule) bukkitHorse).setCarryingChest(chested);
        } else if (bukkitHorse instanceof Donkey) {
            ((Donkey) bukkitHorse).setCarryingChest(chested);
        }
    }

    public void setEating(boolean eating) {
        // No direct API; ignore
    }

    public void setBred(boolean bred) {
        // No direct API; ignore
    }

    public void setVariant(HorseVariant variant) {
        if (bukkitHorse instanceof Horse) {
            Horse horse = (Horse) bukkitHorse;
            // Map some legacy variants to modern Color/Style combinations
            switch (variant) {
                case WHITE:
                case WHITE_WHITE:
                    horse.setColor(Color.WHITE);
                    horse.setStyle(Style.NONE);
                    break;
                case CREAMY:
                case CREAMY_WHITE:
                    horse.setColor(Color.CREAMY);
                    horse.setStyle(Style.NONE);
                    break;
                case CHESTNUT:
                case CHESTNUT_WHITE:
                    horse.setColor(Color.CHESTNUT);
                    horse.setStyle(Style.NONE);
                    break;
                case BROWN:
                case BROWN_WHITE:
                    horse.setColor(Color.BROWN);
                    horse.setStyle(Style.NONE);
                    break;
                case BLACK:
                case BLACK_WHITE:
                    horse.setColor(Color.BLACK);
                    horse.setStyle(Style.NONE);
                    break;
                case GRAY:
                case GRAY_WHITE:
                    horse.setColor(Color.GRAY);
                    horse.setStyle(Style.NONE);
                    break;
                case DARK_BROWN:
                case DARK_BROWN_WHITE:
                    horse.setColor(Color.DARK_BROWN);
                    horse.setStyle(Style.NONE);
                    break;
                default:
                    break;
            }
        }
    }

    public void setTemper(int temper) {
        // No API in 1.21; ignore
    }

    public void setTamed(boolean tamed) {
        bukkitHorse.setTamed(tamed);
    }

    public void setSaddled(boolean saddled) {
        // Bukkit API uses inventory saddle item (since 1.14+)
        if (saddled) {
            // Server will provide saddle when setting owner/using inventory in Stable code
        }
    }

    public void setArmorItem(ItemStack i) {
        if (bukkitHorse instanceof Horse) {
            ((Horse) bukkitHorse).getInventory().setArmor(i);
        }
    }

    public HorseType getType() {
        if (bukkitHorse instanceof Donkey) return HorseType.DONKEY;
        if (bukkitHorse instanceof Mule) return HorseType.MULE;
        if (bukkitHorse instanceof Llama) return HorseType.MULE; // closest
        return HorseType.NORMAL;
    }

    public boolean isChested() {
        if (bukkitHorse instanceof Mule) return ((Mule) bukkitHorse).isCarryingChest();
        if (bukkitHorse instanceof Donkey) return ((Donkey) bukkitHorse).isCarryingChest();
        return false;
    }

    public boolean isEating() { return false; }
    public boolean isBred() { return false; }

    public HorseVariant getVariant() { return HorseVariant.BROWN; }

    public int getTemper() { return 0; }

    public boolean isTamed() { return bukkitHorse.isTamed(); }

    public boolean isSaddled() { return bukkitHorse.getInventory().getSaddle() != null; }

    public ItemStack getArmorItem() {
        if (bukkitHorse instanceof Horse) {
            return ((Horse) bukkitHorse).getInventory().getArmor();
        }
        return null;
    }

    public void openInventory(Player p) {
        // Right-click interaction opens inventory; no direct method
    }

    public LivingEntity getHorse() { return bukkitHorse; }

    private void setHorseValue(String key, Object value) { /* no-op */ }

    public enum HorseType { NORMAL("normal", 0), DONKEY("donkey", 1), MULE("mule", 2), UNDEAD("undead", 3), SKELETAL("skeletal", 4);
        private String name; private int id; HorseType(String name, int id) { this.name = name; this.id = id; }
        public String getName() { return name; } public int getId() { return id; }
        private static final Map<String, HorseType> NAME_MAP = new HashMap<>();
        private static final Map<Integer, HorseType> ID_MAP = new HashMap<>();
        static { for (HorseType t : values()) { NAME_MAP.put(t.name, t); ID_MAP.put(t.id, t);} }
        public static HorseType fromName(String name) { if (name==null) return null; for (Entry<String,HorseType> e: NAME_MAP.entrySet()) if (e.getKey().equalsIgnoreCase(name)) return e.getValue(); return null; }
        public static HorseType fromId(int id) { return ID_MAP.get(id);} }

    public enum HorseVariant { WHITE("white",0), CREAMY("creamy",1), CHESTNUT("chestnut",2), BROWN("brown",3), BLACK("black",4), GRAY("gray",5), DARK_BROWN("dark brown",6), INVISIBLE("invisible",7),
        WHITE_WHITE("white-white",256), CREAMY_WHITE("creamy-white",257), CHESTNUT_WHITE("chestnut-white",258), BROWN_WHITE("brown-white",259), BLACK_WHITE("black-white",260), GRAY_WHITE("gray-white",261), DARK_BROWN_WHITE("dark brown-white",262),
        WHITE_WHITE_FIELD("white-white field",512), CREAMY_WHITE_FIELD("creamy-white field",513), CHESTNUT_WHITE_FIELD("chestnut-white field",514), BROWN_WHITE_FIELD("brown-white field",515), BLACK_WHITE_FIELD("black-white field",516), GRAY_WHITE_FIELD("gray-white field",517), DARK_BROWN_WHITE_FIELD("dark brown-white field",518),
        WHITE_WHITE_DOTS("white-white dots",768), CREAMY_WHITE_DOTS("creamy-white dots",769), CHESTNUT_WHITE_DOTS("chestnut-white dots",770), BROWN_WHITE_DOTS("brown-white dots",771), BLACK_WHITE_DOTS("black-white dots",772), GRAY_WHITE_DOTS("gray-white dots",773), DARK_BROWN_WHITE_DOTS("dark brown-white dots",774),
        WHITE_BLACK_DOTS("white-black dots",1024), CREAMY_BLACK_DOTS("creamy-black dots",1025), CHESTNUT_BLACK_DOTS("chestnut-black dots",1026), BROWN_BLACK_DOTS("brown-black dots",1027), BLACK_BLACK_DOTS("black-black dots",1028), GRAY_BLACK_DOTS("gray-black dots",1029), DARK_BROWN_BLACK_DOTS("dark brown-black dots",1030);
        private String name; private int id; HorseVariant(String name,int id){this.name=name;this.id=id;} public String getName(){return name;} public int getId(){return id;}
        private static final Map<String,HorseVariant> NAME_MAP=new HashMap<>(); private static final Map<Integer,HorseVariant> ID_MAP=new HashMap<>(); static{ for(HorseVariant v:values()){NAME_MAP.put(v.name,v);ID_MAP.put(v.id,v);} }
        public static HorseVariant fromName(String name){ if(name==null)return null; for(Entry<String,HorseVariant> e:NAME_MAP.entrySet()) if(e.getKey().equalsIgnoreCase(name)) return e.getValue(); return null; }
        public static HorseVariant fromId(int id){ return ID_MAP.get(id);} }
}
