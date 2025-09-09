package com.avrgaming.civcraft.util;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

public class EntityProximity {

    /*
     * Use a Bukkit method to grab an axis aligned bounding box around an area to
     * determine which entities are within this radius.
     * Optionally provide an entity that is exempt from these checks.
     * Also optionally provide a filter so we can only capture specific types of entities.
     */
    public static LinkedList<Entity> getNearbyEntities(Entity exempt, Location loc, double radius, Class<?> filter) {
        LinkedList<Entity> entities = new LinkedList<>();

        double x = loc.getX() + 0.5;
        double y = loc.getY() + 0.5;
        double z = loc.getZ() + 0.5;
        double r = radius;

        World world = loc.getWorld();
        if (world == null) {
            return entities;
        }

        BoundingBox box = new BoundingBox(x - r, y - r, z - r, x + r, y + r, z + r);
        for (Entity e : world.getNearbyEntities(box)) {
            if (exempt != null && e.getUniqueId().equals(exempt.getUniqueId())) {
                continue;
            }
            if (filter == null || filter.isInstance(e)) {
                entities.add(e);
            }
        }

        return entities;
    }
}
