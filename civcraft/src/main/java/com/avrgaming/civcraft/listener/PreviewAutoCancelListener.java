// com/avrgaming/civcraft/listeners/PreviewAutoCancelListener.java
package com.avrgaming.civcraft.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.threading.tasks.BuildPreviewAsyncTask;
import com.avrgaming.civcraft.template.Template;
import com.avrgaming.civcraft.util.BlockCoord;

public class PreviewAutoCancelListener implements Listener {

    private static final double RAY_MAX  = 12.0;
    private static final double RAY_STEP = 0.20;

    // Alleen cancel wanneer je OP de preview stapt (vloerblok verandert naar preview)
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        Resident resident = CivGlobal.getResident(player);
        if (resident == null) return;

        if (resident.getPreviewTask() != null) {
            resident.touchPreviewActivity(); // preview-timeout bump
        }

        if (resident.previewUndo == null || resident.previewUndo.isEmpty()) return;

        Location toBelow   = event.getTo().clone().add(0, -1, 0);
        Location fromBelow = event.getFrom().clone().add(0, -1, 0);
        BlockCoord toFloor   = new BlockCoord(toBelow);
        BlockCoord fromFloor = new BlockCoord(fromBelow);

        if (!toFloor.equals(fromFloor) && resident.previewUndo.containsKey(toFloor)) {
            resident.cancelPreviewIfAny(player, true);
        }
    }

    // Rechterklik op de preview: detecteer via clicked block of raycast (ghost bedrock)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) return;

        // verwerk beide handen (MAIN & OFF), voorkomt gemiste clicks
        if (event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        Resident resident = CivGlobal.getResident(player);
        if (resident == null) return;

        if (resident.getPreviewTask() != null) {
            resident.touchPreviewActivity(); // preview-timeout bump
        }

        if ((resident.previewUndo == null || resident.previewUndo.isEmpty()) && resident.getPreviewTask() == null) return;

        BlockCoord targetBc = null;

        if (action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            targetBc = new BlockCoord(event.getClickedBlock().getLocation());
        } else {
            targetBc = raycastPreviewCoord(player, RAY_MAX, resident);
        }

        if (targetBc == null) return;

        if (resident.previewUndo != null && resident.previewUndo.containsKey(targetBc)) {
            resident.cancelPreviewIfAny(player, true);
            return;
        }
        if (isInsideActivePreviewVolume(targetBc, resident)) {
            resident.cancelPreviewIfAny(player, true);
        }
    }

    private BlockCoord raycastPreviewCoord(Player player, double maxDistance, Resident resident) {
        Location eye = player.getEyeLocation();
        org.bukkit.util.Vector dir = eye.getDirection().normalize();
        Location probe = eye.clone();
        int steps = (int) Math.ceil(maxDistance / RAY_STEP);

        for (int i = 0; i < steps; i++) {
            probe.add(dir.getX() * RAY_STEP, dir.getY() * RAY_STEP, dir.getZ() * RAY_STEP);

            Block serverBlock = probe.getBlock();
            BlockCoord bc = new BlockCoord(probe);

            // Stop bij echte muur; tenzij dat blok zelf preview is
            if (serverBlock.getType().isOccluding()) {
                if ((resident.previewUndo != null && resident.previewUndo.containsKey(bc)) ||
                        isInsideActivePreviewVolume(bc, resident)) {
                    return bc;
                }
                return null;
            }

            if (resident.previewUndo != null && resident.previewUndo.containsKey(bc)) return bc;
            if (isInsideActivePreviewVolume(bc, resident)) return bc;
        }
        return null;
    }

    private boolean isInsideActivePreviewVolume(BlockCoord bc, Resident resident) {
        BuildPreviewAsyncTask task = resident.getPreviewTask();
        if (task == null) return false;

        Template tpl = task.getTemplate();
        Block origin = task.getCenterBlock();
        if (tpl == null || origin == null) return false;

        int minX = origin.getX();
        int minY = origin.getY();
        int minZ = origin.getZ();
        int maxX = minX + tpl.size_x - 1;
        int maxY = minY + tpl.size_y - 1;
        int maxZ = minZ + tpl.size_z - 1;

        int x = bc.getX(), y = bc.getY(), z = bc.getZ();
        return (x >= minX && x <= maxX) &&
                (y >= minY && y <= maxY) &&
                (z >= minZ && z <= maxZ);
    }
}
