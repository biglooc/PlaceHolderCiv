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
package com.avrgaming.civcraft.threading.tasks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivCraft;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.template.Template;
import com.avrgaming.civcraft.threading.CivAsyncTask;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.util.BlockCoord;
import com.avrgaming.civcraft.util.ItemManager;
import com.avrgaming.civcraft.util.SimpleBlock;

/**
 * Bouwt een PREVIEW van een template in batches per tick (client-side via sendBlockChange),
 * om lag te vermijden. Undo wordt bijgehouden in resident.previewUndo.
 */
public class BuildPreviewAsyncTask extends CivAsyncTask {

	// Input
	public Template tpl;
	public Block centerBlock;
	public UUID playerUUID;

	// Owner
	private final Resident resident;

	// Concurrency/flow
	public final ReentrantLock lock = new ReentrantLock();
	public volatile boolean aborted = false;

	// Batching
	private final ArrayDeque<Placement> queue = new ArrayDeque<Placement>();
	private BukkitTask previewTicker = null;
	private final Plugin plugin;

	// Throttle (kan je later uit config lezen)
	private int blocksPerTick = 60; // max # block changes per tick
	private int tickInterval  = 1;   // iedere tick (20/s)

	private static class Placement {
		final Block block;
		final int typeId;
		final int data;
		Placement(Block block, int typeId, int data) {
			this.block = block;
			this.typeId = typeId;
			this.data = data;
		}
	}

	/**
	 * Voorkeursconstructor: geef Resident en Plugin mee.
	 */
	public BuildPreviewAsyncTask(Template t, Block center, UUID playerUUID, Resident resident, Plugin plugin) {
		this.tpl = t;
		this.centerBlock = center;
		this.playerUUID = playerUUID;
		this.resident = resident;
		this.plugin = plugin;
	}

	/**
	 * Legacy constructor; delegeert naar de voorkeursconstructor.
	 * Laat deze bestaan i.v.m. oudere callsites, maar gebruik bij voorkeur de 5-args ctor.
	 */
	@Deprecated
	public BuildPreviewAsyncTask(Template t, Block center, UUID playerUUID) {
		this(
				t,
				center,
				playerUUID,
				CivGlobal.getResidentViaUUID(playerUUID),
				JavaPlugin.getPlugin(CivCraft.class)
		);
		if (this.resident == null) {
			throw new IllegalStateException("BuildPreviewAsyncTask: Resident not found for UUID " + playerUUID);
		}
	}

	public Player getPlayer() throws CivException {
		Player player = Bukkit.getPlayer(playerUUID);
		if (player == null) {
			throw new CivException("Player offline");
		}
		return player;
	}

	@Override
	public void run() {
		// 1) Queue vullen met ALLE niet-air templateblokken
		queue.clear();
		for (int y = 0; y < tpl.size_y; y++) {
			for (int x = 0; x < tpl.size_x; x++) {
				for (int z = 0; z < tpl.size_z; z++) {
					SimpleBlock sb = tpl.blocks[x][y][z];
					if (sb == null || sb.isAir()) {
						continue;
					}

					Block worldBlock = centerBlock.getRelative(x, y, z);

					// Echte template-blokken tonen (1.12: id + data)
					int typeId = sb.getType();  // in veel forks: getType() -> int id
					int data   = sb.getData();  // in veel forks: getData() -> int data

					queue.add(new Placement(worldBlock, typeId, data));
				}
			}
		}

		// 2) Start de ticker op de MAIN thread
		Bukkit.getScheduler().runTask(plugin, this::startTicker);
	}

	private void startTicker() {
		stopTicker(); // safety
		previewTicker = Bukkit.getScheduler().runTaskTimer(plugin, this::tickOnce, 0L, tickInterval);
	}

	private void tickOnce() {
		if (aborted) {
			stopTicker();
			return;
		}

		final Player p;
		try {
			p = getPlayer();
		} catch (CivException e) {
			stopTicker();
			return;
		}

		int sent = 0;
		while (sent < blocksPerTick && !queue.isEmpty()) {
			Placement pl = queue.pollFirst();

			// Client-side block change
			ItemManager.sendBlockChange(p, pl.block.getLocation(), pl.typeId, pl.data);

			// Bewaar originele wereldstate voor undo
			resident.previewUndo.put(
					new BlockCoord(pl.block.getLocation()),
					new SimpleBlock(ItemManager.getId(pl.block), ItemManager.getData(pl.block))
			);

			sent++;
		}

		if (queue.isEmpty()) {
			// klaar met opbouwen; preview blijft staan tot cancel/undo
			stopTicker();
		}
	}

	private void stopTicker() {
		if (previewTicker != null) {
			previewTicker.cancel();
			previewTicker = null;
		}
	}

	/** Abort + volledige undo van de preview. */
	public void abortAndUndoPreview(Player player) {
		lock.lock();
		try {
			if (aborted) return;
			aborted = true;
		} finally {
			lock.unlock();
		}

		stopTicker();

		if (resident != null && resident.previewUndo != null && !resident.previewUndo.isEmpty()) {
			// Stuur revert packets terug naar de client (gebruik player als die er is)
			if (player != null) {
				ArrayList<BlockCoord> coords = new ArrayList<BlockCoord>(resident.previewUndo.keySet());
				for (BlockCoord bc : coords) {
					Block worldBlock = bc.getBlock();
					ItemManager.sendBlockChange(
							player,
							worldBlock.getLocation(),
							ItemManager.getId(worldBlock),
							ItemManager.getData(worldBlock)
					);
				}
			}
			resident.previewUndo.clear();
		}
	}

	// Getters die je listeners gebruiken
	public Template getTemplate() { return tpl; }
	public Block getCenterBlock() { return centerBlock; }

	// Optioneel: stel throttle dynamisch in
	public void setBlocksPerTick(int blocksPerTick) {
		if (blocksPerTick > 0) this.blocksPerTick = blocksPerTick;
	}

	public void setTickInterval(int tickInterval) {
		if (tickInterval > 0) this.tickInterval = tickInterval;
	}
}
