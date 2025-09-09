package com.avrgaming.civcraft.questions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.main.CivData;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.trade.TradeInventoryListener;
import com.avrgaming.civcraft.trade.TradeInventoryPair;
import com.avrgaming.civcraft.util.CivColor;
import com.avrgaming.civcraft.lorestorage.LoreGuiItem;
import com.avrgaming.civcraft.util.ItemManager;

public class TradeRequest implements QuestionResponseInterface {

	public Resident resident;
	public Resident trader;
	
	@Override
	public void processResponse(String param) {
		if (param.equalsIgnoreCase("accept")) {
			try {
				// Resolve players
				Player traderPlayer = CivGlobal.getPlayer(trader);
				Player residentPlayer = CivGlobal.getPlayer(resident);
				
				// Create trade inventories (5 rows = 45 slots, listener uses up to index 44)
    Inventory traderInv = Bukkit.createInventory(traderPlayer, 5*9, "Trade: "+resident.getName());
    Inventory residentInv = Bukkit.createInventory(residentPlayer, 5*9, "Trade: "+trader.getName());
				
				// Build pairs
				TradeInventoryPair pair = new TradeInventoryPair();
				pair.inv = traderInv;
				pair.otherInv = residentInv;
				pair.resident = trader;
				pair.otherResident = resident;
				pair.coins = 0;
				pair.otherCoins = 0;
				
				TradeInventoryPair otherPair = new TradeInventoryPair();
				otherPair.inv = residentInv;
				otherPair.otherInv = traderInv;
				otherPair.resident = resident;
				otherPair.otherResident = trader;
				otherPair.coins = 0;
				otherPair.otherCoins = 0;
				
				// Register before populating so listener helpers can reference
				TradeInventoryListener.tradeInventories.put(TradeInventoryListener.getTradeInventoryKey(trader), pair);
				TradeInventoryListener.tradeInventories.put(TradeInventoryListener.getTradeInventoryKey(resident), otherPair);
				
				// Initialize confirmation buttons to invalid (red) on both sides
				// We reuse listener's helper to ensure consistent items
				TradeInventoryListener listener = new TradeInventoryListener();
				listener.markTradeInvalid(pair);
				listener.markTradeInvalid(otherPair);
				
				// Initialize coin offer display to 0 for both views
				pair.inv.setItem(TradeInventoryListener.MY_COIN_OFFER, LoreGuiItem.build(""+CivSettings.CURRENCY_NAME+" "+CivSettings.localize.localizedString("resident_tradeOffered"), 
						ItemManager.getId(org.bukkit.Material.NETHER_BRICK), 0, 
						CivColor.Yellow+"0 "+CivSettings.CURRENCY_NAME));
				otherPair.inv.setItem(TradeInventoryListener.MY_COIN_OFFER, LoreGuiItem.build(""+CivSettings.CURRENCY_NAME+" "+CivSettings.localize.localizedString("resident_tradeOffered"), 
						ItemManager.getId(org.bukkit.Material.NETHER_BRICK), 0, 
						CivColor.Yellow+"0 "+CivSettings.CURRENCY_NAME));
				// Mirror to their view slots
				pair.inv.setItem(TradeInventoryListener.OTHER_COIN_OFFER, LoreGuiItem.build(""+CivSettings.CURRENCY_NAME+" "+CivSettings.localize.localizedString("resident_tradeOffered"), 
						ItemManager.getId(org.bukkit.Material.NETHER_BRICK), 0, 
						CivColor.Yellow+"0 "+CivSettings.CURRENCY_NAME));
				otherPair.inv.setItem(TradeInventoryListener.OTHER_COIN_OFFER, LoreGuiItem.build(""+CivSettings.CURRENCY_NAME+" "+CivSettings.localize.localizedString("resident_tradeOffered"), 
						ItemManager.getId(org.bukkit.Material.NETHER_BRICK), 0, 
						CivColor.Yellow+"0 "+CivSettings.CURRENCY_NAME));
				
				// Open both inventories to players
				traderPlayer.openInventory(traderInv);
				residentPlayer.openInventory(residentInv);
			} catch (Exception e) {
				// If anything goes wrong, clear registrations to avoid stale state
				TradeInventoryListener.tradeInventories.remove(TradeInventoryListener.getTradeInventoryKey(trader));
				TradeInventoryListener.tradeInventories.remove(TradeInventoryListener.getTradeInventoryKey(resident));
			}
		} else {
			CivMessage.send(trader, CivColor.LightGray+CivSettings.localize.localizedString("var_trade_declined",resident.getName()));
		}
	}

	@Override
	public void processResponse(String response, Resident responder) {
		processResponse(response);		
	}
}
