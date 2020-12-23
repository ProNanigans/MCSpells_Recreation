package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.effect.SphereEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Conjunctivitis extends Crowd_Control implements SpellCasting {

    private HitTypes hit;
    private Entity hitEnt;
    private final static BlockData fallingDustData = Material.OBSIDIAN.createBlockData();


    public Conjunctivitis(Wand wand) {
        super(wand);
        player.getWorld().playSound(player.getLocation(), "magic.whoosh6", 100, 1);
        final double range = 40D;
        final double spacing = 0.5D;
        final long speed = 2;
        super.cast(range, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hit != null) {
            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 5, 0, 0, 0, 0.1);
            hitLoc.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

            if(hit == HitTypes.ENTITY){

                final LivingEntity hitEnt = (LivingEntity) this.hitEnt;
                hitEnt.getWorld().playSound(hitEnt.getLocation(), Sound.ENTITY_PLAYER_HURT, 20, 1);
                SphereEffect sphere = new SphereEffect(plugin.manager);
                sphere.asynchronous = true;
                sphere.radius = 0.45;
                sphere.radiusIncrease = 0;
                sphere.particle = Particle.REDSTONE;
                sphere.color = Color.BLACK;
                sphere.duration = 4600;
                sphere.setEntity(hitEnt);
                sphere.start();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        hitEnt.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
                    }
                }.runTask(plugin);

            }

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location loc = p1.toLocation(player.getWorld());
        player.spawnParticle(Particle.BLOCK_CRACK, loc, 4, .05, .05, .05, fallingDustData);

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
                    if(entity.getBoundingBox().expand(.12).contains(p1)){
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
