package me.nanigans.potterworldspells.Magic.Spells.Utility;

import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Utility;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.Particles.ParticleUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Aquamenti extends Utility implements SpellCasting {
    HitTypes hit;
    Entity hitEnt;
    public Aquamenti(Wand wand) {
        super(wand);
        final double range = 20D;
        final double spacing = 0.5D;
        final long speed = 5;
        player.getWorld().playSound(player.getLocation(), "magic.aguamenti", 100, 1);
        super.cast(range, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hit != null){

            if(hit == HitTypes.ENTITY)
                hitEnt.getWorld().playSound(hitEnt.getLocation(), Sound.ENTITY_PLAYER_HURT, 50, 1);

            final List<Location> sphere = ParticleUtils.createSphere(hitLoc, 3, false);
            World world = player.getWorld();
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Location location : sphere) {
                        final Block blockAt = world.getBlockAt(location);
                        if(blockAt.getType().isAir()) {
                            blockAt.setMetadata(Data.SPREAD.toString(), new FixedMetadataValue(plugin, "water"));
                            blockAt.setType(Material.WATER);
                        }
                    }
                }
            }.runTask(plugin);
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Location location : sphere) {
                        final Block blockAt = world.getBlockAt(location);
                        if(blockAt.getType() == Material.WATER) {
                            blockAt.setType(Material.AIR);
                        }
                    }
                }
            }.runTaskLater(plugin, 140);

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location loc = p1.toLocation(player.getWorld());
        loc.getWorld().spawnParticle(Particle.FALLING_WATER, loc, 5, .2, 0.2, 0.2);

        final Block blockAt = loc.getWorld().getBlockAt(loc);
        if(blockAt.getType().isSolid()) {
            if (blockAt.hasMetadata(Data.REFLECT.toString())) {
                if (reflectSpell(blockAt, player, p1, vector)) {
                    canHitCaster = true;
                    return null;
                }
            }
            hit = HitTypes.BLOCK;
            return loc;
        }

        final Entity[] entities = loc.getWorld().getChunkAt(loc).getEntities();
        for (Entity entity : entities) {
            if(canHitCaster || !entity.equals(player))
                if(entity instanceof LivingEntity && !((LivingEntity) entity).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
                    if(entity.getBoundingBox().expand(.25).contains(p1)){
                        hitEnt = entity;
                        hit = HitTypes.ENTITY;
                        return loc;
                    }
                }
        }


        p1.add(vector);
        return null;
    }
}
