package me.nanigans.potterworldspells.Magic.Spells.Utility;

import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Utility;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Particles.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Aparecium extends Utility implements SpellCasting {
    private HitTypes hit;
    public Aparecium(Wand wand) {
        super(wand);

        player.getWorld().playSound(player.getLocation(), "magic.aparecium", 50, 1);
        final double range = 5;
        final double spacing = 0.5;
        final long speed = 5;

        super.cast(range, spacing, speed, this::whileFiring, this::onHit);

    }

    @Override
    public void onHit(Location hitLoc) {

        final List<Location> sphere = ParticleUtils.createSphere(hitLoc, 5, false);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : sphere) {
                    Block b = location.getBlock();
                    BlockData d = b.getBlockData();
                    if (d instanceof Powerable) {
                        ((Powerable) d).setPowered(!((Powerable) d).isPowered());
                        b.setBlockData(d);
                    }
                }
            }
        }.runTask(plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : sphere) {
                    Block b = location.getBlock();
                    BlockData d = b.getBlockData();
                    if (d instanceof Powerable) {
                        ((Powerable) d).setPowered(!((Powerable) d).isPowered());
                        b.setBlockData(d);
                    }
                }
            }
        }.runTaskLater(plugin, 100);

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location location = p1.toLocation(player.getWorld());

        location.getWorld().spawnParticle(Particle.WATER_SPLASH, location, 10, 0.5, 0.5, 0.5, 0);
        final Block blockAt = location.getBlock();
        if(blockAt.getType().isSolid()) {
            hit = HitTypes.BLOCK;
            return location;
        }
        p1.add(vector);

        return null;
    }
}
