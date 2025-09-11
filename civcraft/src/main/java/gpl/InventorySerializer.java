package gpl;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.avrgaming.civcraft.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.avrgaming.civcraft.lorestorage.LoreCraftableMaterial;
import com.avrgaming.civcraft.util.ItemManager;

import static InventorySerializer.getSerializedItemStack;

/*
 * Original serializer by Phil2812 (https://forums.bukkit.org/threads/serialize-inventory-to-single-string-and-vice-versa.92094/)
 */

public class InventorySerializer {
	
	private static String getSerializedItemStack(ItemStack is) {
        ItemMeta m = is.getItemMeta();
        String serializedItemStack = new String();
        String dn = Text.getDisplayName(m);
        if (dn != null && !dn.isEmpty()) {
            serializedItemStack += "&D@" + dn;
        }
        
        String isType = String.valueOf(ItemManager.getId(is.getType()));
        serializedItemStack += "t@" + isType;
       
        ItemMeta meta1 = is.getItemMeta();
        if (meta1 instanceof Damageable dmg1 && dmg1.getDamage() != 0) {
            serializedItemStack += "&d@" + dmg1.getDamage();
        }
       
        if (is.getAmount() != 1)
        {
            String isAmount = String.valueOf(is.getAmount());
            serializedItemStack += "&a@" + isAmount;
        }
       
        Map<Enchantment,Integer> isEnch = is.getEnchantments();
        if (isEnch.size() > 0)
        {
            for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
            {
                serializedItemStack += "&e@" + ItemManager.getId(ench.getKey()) + "@" + ench.getValue();
            }
        }
       
        ItemMeta meta = is.getItemMeta();
        if (meta != null && meta.hasLore()) {
        	for (List<String> lore = Text.getLore(m);
        		char[] encode = Base64Coder.encode(Text.getBytes());
        		String encodedString = new String(encode);
        		serializedItemStack += "&l@" + encodedString;
        	}
        return serializedItemStack;

        
        if (meta != null) {
        	if (meta.getDisplayName() != null) {
        		serializedItemStack += "&D@" + meta.getDisplayName();
        	}
        }
        
        LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterial(is);
        if (craftMat != null) {
        	serializedItemStack += "&C@" + craftMat.getConfigId();
        	
        	if (LoreCraftableMaterial.hasEnhancements(is)) {
    			serializedItemStack += "&Enh@" + LoreCraftableMaterial.serializeEnhancements(is);
        	}
        }
        
        AttributeUtil attrs = new AttributeUtil(is);
        if (attrs.hasColor()) {
        	serializedItemStack += "&LC@" + attrs.getColor();
        }
        
        return serializedItemStack;
	}
	
	private static ItemStack getItemStackFromSerial(String serial) {
        ItemStack is = null;
        Boolean createdItemStack = false;
        List<String> lore = new LinkedList<String>();
       
        //String[] serializedItemStack = serializedBlock[1].split("&");
        String[] serializedItemStack = serial.split("&");
        for (String itemInfo : serializedItemStack)
        {
            String[] itemAttribute = itemInfo.split("@");
            if (itemAttribute[0].equals("t"))
            {
                is = ItemManager.createItemStack(Integer.valueOf(itemAttribute[1]), 1);
                createdItemStack = true;
            }
            else if (itemAttribute[0].equals("d") && createdItemStack)
            {
                ItemMeta meta = is.getItemMeta();
                if (meta instanceof Damageable dmg) {
                    int _max = is.getType().getMaxDurability();
                    int _val;
                    try {
                        _val = Integer.parseInt(itemAttribute[1]);
                    } catch (NumberFormatException e) {
                        _val = 0;
                    }
                    if (_max > 0) {
                        _val = Math.max(0, Math.min(_val, _max - 1));
                    } else {
                        _val = Math.max(0, _val);
                    }
                    dmg.setDamage(_val);
                    is.setItemMeta(meta);
                }
            }
            else if (itemAttribute[0].equals("a") && createdItemStack)
            {
                is.setAmount(Integer.valueOf(itemAttribute[1]));
            }
            else if (itemAttribute[0].equals("e") && createdItemStack)
            {
                is.addEnchantment(ItemManager.getEnchantById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
            } 
            else if (itemAttribute[0].equals("l") && createdItemStack) 
            {
            	byte[] decode = Base64Coder.decode(itemAttribute[1]);
            	String decodedString = new String(decode);                	
            	lore.add(decodedString);
            }
            else if (itemAttribute[0].equals("D") && createdItemStack) {
            	ItemMeta meta = is.getItemMeta();
            	if (meta != null) {
            		setDisplayName(meta, String.valueOf(lore));
            	}
            	is.setItemMeta(meta);
            } else if (itemAttribute[0].equals("C")) {
            	/* Custom craftItem. */
                LoreCraftableMaterial craftMat = LoreCraftableMaterial.getCraftMaterialFromId(itemAttribute[1]);
                try {
                	AttributeUtil attrs = new AttributeUtil(is);
                	LoreCraftableMaterial.setMIDAndName(attrs, itemAttribute[1], craftMat.getName());
                	is = attrs.getStack();
                } catch (NullPointerException e) {
                	e.printStackTrace();
                }
            } else if (itemAttribute[0].equals("Enh")) {
            	is = LoreCraftableMaterial.deserializeEnhancements(is, itemAttribute[1]);
            } else if (itemAttribute[0].equals("LC")) {
            	AttributeUtil attrs = new AttributeUtil(is);
            	attrs.setColor(Long.valueOf(itemAttribute[1]));
            	is = attrs.getStack();
            }
        }
        
        if (lore.size() > 0) {
        	ItemMeta meta = is.getItemMeta();
        	if (meta != null) {
        		meta.setLore(lore);
        		is.setItemMeta(meta);
        	}
        }
        
        return is;
	}
	
    public static String InventoryToString (Inventory invInventory)
    {
        String serialization = invInventory.getSize() + ";";
        for (int i = 0; i < invInventory.getSize(); i++)
        {
            ItemStack is = invInventory.getItem(i);
            if (is != null)
            {
            	String serializedItemStack = InventorySerializer.getSerializedItemStack(is);
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        
        if (invInventory instanceof PlayerInventory) {
        	serialization += "&PINV@";
        	PlayerInventory pInv = (PlayerInventory)invInventory;
        	
        	for (ItemStack stack : pInv.getArmorContents()) {
        		if (stack != null) {
        			serialization += InventorySerializer.getSerializedItemStack(stack) + ";";
        		}
        	}
        }
        
        return serialization;
    }
   
    public static void StringToInventory (Inventory inv, String inString)
    {
    	String invString;
    	String[] inventorySplit = null; 
    	
    	if (inv instanceof PlayerInventory) {
    		inventorySplit = inString.split("&PINV@");
    		invString = inventorySplit[0];
    	} else {
    		invString = inString;
    	}
    	
        String[] serializedBlocks = invString.split(";");
        inv.clear();
       
        for (int i = 1; i < serializedBlocks.length; i++)
        {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);
           
            if (stackPosition >= inv.getSize())
            {
                continue;
            }
           
            ItemStack is = getItemStackFromSerial(serializedBlock[1]);
            inv.setItem(stackPosition, is);
        }
        
        if (inv instanceof PlayerInventory) {
        	PlayerInventory pInv = (PlayerInventory)inv;
        	invString = inventorySplit[1];
            String[] serializedBlocksArmor = invString.split(";");
           
            ItemStack[] contents = new ItemStack[4];
            for (int i = 0; i < serializedBlocksArmor.length; i++)
            { 
                ItemStack is = getItemStackFromSerial(serializedBlocksArmor[i]);
                contents[i] = is;
            }
            
            pInv.setArmorContents(contents);
        }
       
        return;
    }
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    public static void setDisplayName(ItemMeta meta, String name) {
        meta.displayName(name == null ? null : LEGACY.deserialize(name));
    }

    public static String getDisplayName(ItemMeta meta) {
        Component c = meta.displayName();
        return (c == null) ? null : LEGACY.serialize(c);
    }

    public static void setLore(ItemMeta meta, java.util.List<String> lines) {
        if (lines == null) { meta.lore(null); return; }
        java.util.List<Component> comps = new java.util.ArrayList<>(lines.size());
        for (String s : lines) comps.add(LEGACY.deserialize(s == null ? "" : s));
        meta.lore(comps);
    }

    public static java.util.List<String> getLore(ItemMeta meta) {
        java.util.List<Component> comps = meta.lore();
        if (comps == null) return java.util.Collections.emptyList();
        java.util.List<String> out = new java.util.ArrayList<>(comps.size());
        for (Component c : comps) out.add(LEGACY.serialize(c));
        return out;
    }
	
}

