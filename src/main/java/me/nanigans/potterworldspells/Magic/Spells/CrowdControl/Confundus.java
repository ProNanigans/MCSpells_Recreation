package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.effect.AnimatedBallEffect;
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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Confundus extends Crowd_Control implements SpellCasting {
    private HitTypes hit;
    private Entity hitEnt;

    public Confundus(Wand wand) {
        super(wand);

        double distance = 39D;
        double spacing = 0.25;
        long speed = 1;
        super.cast(distance, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        hitLoc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 20, 0, 0, 0, 0.3);
        hitLoc.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

        if(hitEnt instanceof LivingEntity){
            LivingEntity ent = (LivingEntity) hitEnt;
            new BukkitRunnable() {
                @Override
                public void run() {
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 2));
                }
            }.runTask(plugin);
            hitLoc.getWorld().playSound(hitLoc, "magic.evilwhoosh3", 100, 1);
            hitLoc.getWorld().playSound(hitLoc, Sound.ENTITY_VILLAGER_HURT, 100, 1);

            AnimatedBallEffect effect = new AnimatedBallEffect(plugin.manager);
            effect.setEntity(ent);
            effect.asynchronous = true;
            effect.color = Color.fromRGB(0, 0, 128);
            effect.particle = Particle.REDSTONE;
            effect.duration = 8000;
            effect.offset = new Vector(0, -1, 0);
            effect.start();

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location loc = p1.toLocation(player.getWorld());
        player.getWorld().spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 2, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(0, 0, 128), 1));

        final Block blockAt = player.getWorld().getBlockAt(loc);
        if(blockAt.getType().isSolid()){
            hit = HitTypes.BLOCK;
            return loc;
        }

        final Entity[] entities = loc.getWorld().getChunkAt(loc).getEntities();
        for (Entity entity : entities) {
            if(!entity.equals(player))
                if(entity instanceof LivingEntity && !((LivingEntity) entity).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
                    if(entity.getBoundingBox().expand(0.15).contains(p1)){
                        hitEnt = entity;
                        return loc;
                    }
                }
        }

        p1.add(vector);
        return null;
    }
}
