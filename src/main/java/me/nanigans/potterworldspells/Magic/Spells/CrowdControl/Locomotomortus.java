package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Locomotomortus extends Crowd_Control implements SpellCasting {
    HitTypes hit;
    Entity target;
    private final double spacing = 0.35;
    public Locomotomortus(Wand wand) {
        super(wand);

        final double range = 32.5;
        final long speed = 62;
        Location start = getSpellCastLoc();
        BoundingBox box = new BoundingBox(start.getX(), start.getY(), start.getZ(), start.getX(), start.getY(), start.getZ());
        Collection<Entity> entities = player.getWorld().getNearbyEntities(box.expand(player.getLocation().getDirection(), range));

        player.playSound(player.getLocation(), "magic.whoosh7", 100, 1);
        this.target = entities.stream().filter(i -> i != player).min(Comparator.comparingDouble(i -> i.getLocation().distance(player.getLocation()))).orElse(null);
        super.cast(range, spacing, speed, this::whileFiring, this::onHit);

    }

    @Override
    public void onHit(Location hitLoc) {

        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 10, 0, 0, 0, 0.2);
        if(hit != null) {
            hitLoc.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

            if(hit == HitTypes.ENTITY){

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final LivingEntity target = (LivingEntity) Locomotomortus.this.target;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2));
                        target.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, 100, 250)));
                    }
                }.runTask(plugin);

            }

        }
    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {


        final Location center = p1.toLocation(player.getWorld());
        if(target != null && target.isDead()){
            ignoreCancel = false;
            target = null;
            return center;
        }
        final List<Location> locations = ParticleUtils.verticleCircle(center, 0.5F, 8, player.getLocation().getDirection());
        for (Location location : locations) {
            center.getWorld().spawnParticle(Particle.REDSTONE, location, 2, new Particle.DustOptions(Color.MAROON, 1));
        }
        center.getWorld().spawnParticle(Particle.REDSTONE, center, 2, new Particle.DustOptions(wand.getWandColor(), 1));

        if(!ignoreCancel) {
            final Block blockAt = center.getWorld().getBlockAt(center);
            if (blockAt.getType().isSolid()) {
                hit = HitTypes.BLOCK;
                return center;
            }

            final Entity[] entities = center.getWorld().getChunkAt(center).getEntities();
            for (Entity entity : entities) {
                if (!entity.equals(player))
                    if (entity instanceof LivingEntity && !((LivingEntity) entity).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                        if (entity.getBoundingBox().expand(0.05).contains(p1)) {
                            target = entity;
                            hit = HitTypes.ENTITY;
                            ignoreCancel = true;
                            return center;
                        }
                    }
            }
        }

        if(target != null){

            Location entLoc = target.getLocation().add(0, target.getBoundingBox().getHeight(), 0);
            Vector v = entLoc.toVector().subtract(p1).normalize().multiply(spacing);
            p1.add(v);

        }else p1.add(vector);

        return null;
    }
}
