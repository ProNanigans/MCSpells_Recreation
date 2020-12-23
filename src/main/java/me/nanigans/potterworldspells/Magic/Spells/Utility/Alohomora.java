package me.nanigans.potterworldspells.Magic.Spells.Utility;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.CubeEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Utility;
import me.nanigans.potterworldspells.Magic.Wand;
import net.minecraft.server.v1_16_R3.BlockDoor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Alohomora extends Utility implements SpellCasting {
    HitTypes hit;
    public Alohomora(Wand wand) {
        super(wand);
        final double range = 15D;
        final long speed = 3;
        final double spacing = 0.5;
        player.getWorld().playSound(player.getLocation(), "magic.alohomora", 75, 1);
        super.cast(range, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        CubeEffect cube = new CubeEffect(plugin.manager);
        cube.asynchronous = true;
        cube.particleData = 0.2F;
        cube.type = EffectType.INSTANT;
        cube.particle = Particle.FIREWORKS_SPARK;
        cube.setLocation(hitLoc);
        cube.edgeLength = 1;
        cube.outlineOnly = true;
        cube.start();
        if(hit == HitTypes.BLOCK){

            Block blockAt = hitLoc.getWorld().getBlockAt(hitLoc);
            if(blockAt.getType() == Material.IRON_DOOR){
                hitLoc.getWorld().playSound(hitLoc, Sound.BLOCK_IRON_DOOR_OPEN, 30, 1);
                final BlockState state = blockAt.getState();
                final Openable finalDoor = ((Openable) state.getBlockData());
                boolean isOpen = finalDoor.isOpen();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Block door = hitLoc.getBlock();
                        BlockData doorData = door.getBlockData();
                        ((Openable) doorData).setOpen(!isOpen);
                        door.setBlockData(doorData);
                    }
                }.runTask(plugin);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        hitLoc.getWorld().playSound(hitLoc, Sound.BLOCK_IRON_DOOR_CLOSE, 30, 1);
                        Block door = hitLoc.getBlock();
                        BlockData doorData = door.getBlockData();
                        ((Openable) doorData).setOpen(isOpen);
                        door.setBlockData(doorData);
                    }
                }.runTaskLater(plugin, 100);

            }

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location location = p1.toLocation(player.getWorld());
        if(Math.random() > 0.5)
        location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0.1, 0.1, 0.1, 0);

        final Block blockAt = location.getWorld().getBlockAt(location);
        if(blockAt.getType().isSolid()) {
            hit = HitTypes.BLOCK;
            return location;
        }
        p1.add(vector);
        return null;
    }
}
