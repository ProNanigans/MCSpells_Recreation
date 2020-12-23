package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Flipendo extends Crowd_Control implements SpellCasting {
    private HitTypes hit;
    private Entity hitEnt;
    Vector lastV;
    public Flipendo(Wand wand) {
        super(wand);
        final double distance = 40D;
        final double space = 0.35;
        final long speed = 1;
        super.cast(distance, space, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 5, 0, 0, 0, 0.1);
        hitLoc.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

        if(hit != null && hit == HitTypes.ENTITY){

            hitEnt.setVelocity(lastV.multiply(2).add(new Vector(0, 0.3, 0)));
            if(hitEnt instanceof LivingEntity)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        hitEnt.getWorld().playSound(hitEnt.getLocation(), Sound.ENTITY_PLAYER_HURT, 100, 1);
                    }
                }.runTask(plugin);

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        player.getWorld().spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 2, new Particle.DustOptions(
                wand.getWandColor(), 1
        ));
        player.getWorld().spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 2, .1, .1, .1,
                new Particle.DustOptions(Color.WHITE, 0.5F));

        Location loc = p1.toLocation(player.getWorld());
        final Block blockAt = player.getWorld().getBlockAt(loc);
        if(blockAt.getType().isSolid()){

            if(blockAt.hasMetadata("Protego")){
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
                    if(entity.getBoundingBox().expand(0.15).contains(p1)){
                        hitEnt = entity;
                        hit = HitTypes.ENTITY;
                        lastV = vector;
                        return loc;
                    }
                }
        }

        p1.add(vector);
        return null;
    }
}
