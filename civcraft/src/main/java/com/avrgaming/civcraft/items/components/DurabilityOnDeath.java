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
package com.avrgaming.civcraft.items.components;

import gpl.AttributeUtil;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.lorestorage.ItemChangeResult;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DurabilityOnDeath extends ItemComponent {

	@Override
	public void onPrepareCreate(AttributeUtil attrs) {
//		attrs.addLore(CivColor.Blue+""+this.getDouble("value")+" Durability");
	}

	@Override
	public ItemChangeResult onDurabilityDeath(PlayerDeathEvent event, ItemChangeResult result, ItemStack sourceStack) {
		if (result == null) {
			result = new ItemChangeResult();
			result.stack = sourceStack;
			result.destroyItem = false;
		}
		
		if (result.destroyItem) {
			return result;
		}
		
		double percent = this.getDouble("value");
		
		int max = result.stack.getType().getMaxDurability();
		int reduction = (int) (max * percent);

		ItemMeta meta = result.stack.getItemMeta();
		if (!(meta instanceof Damageable dmg)) {
			return result;
		}

		int damage = dmg.getDamage();
		int durabilityLeft = max - damage;

		if (durabilityLeft > reduction) {
			dmg.setDamage(damage + reduction);
			result.stack.setItemMeta(meta);
		}else {
			result.destroyItem = true;
		}
		
		return result;
	}

}
