package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Levicorpus extends Crowd_Control implements SpellCasting {
    private HitTypes hit;
    private Entity hitEnt;
    public Levicorpus(Wand wand) {
        super(wand);
        final double length = 40D;
        final double space = 0.25;
        final long speed = 2;

        AnimatedBallEffect ball = new AnimatedBallEffect(plugin.manager);
        ball.setEntity(player);
        ball.asynchronous = true;
        ball.iterations = 15;
        ball.period = 0;
        ball.yOffset = -0.32F;
        ball.yFactor = 1.5F;
        ball.particle = Particle.SPELL;
        ball.start();
        player.getWorld().playSound(player.getLocation(), "magic.levicorpus", 100, 1);
        super.cast(length, space, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hit != null){
            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 10, 0, 0, 0, 0.15);

            if(hit == HitTypes.ENTITY){

                if(hitEnt instanceof LivingEntity){

                    LivingEntity ent = (LivingEntity) hitEnt;
                    ent.setVelocity(new Vector(0, .5, 0));

                    ent.getWorld().playSound(ent.getLocation(), "magic.hit1", 100, 1);
                    WarpEffect effect = new WarpEffect(plugin.manager);
                    effect.asynchronous = true;
                    effect.offset = new Vector(0, -1.5, 0);
                    effect.setEntity(player);
                    effect.duration = 7000;
                    effect.particle = Particle.REDSTONE;
                    effect.color = Color.PURPLE;
                    effect.grow = 0;
                    effect.radius = 0.5F;
                    effect.start();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ent.damage(0.1);
                            ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 140, 1));
                        }
                    }.runTask(plugin);

                }

            }

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        player.getWorld().spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 2, new Particle.DustOptions(
                Color.PURPLE, 1
        ));
        final Location location = p1.toLocation(player.getWorld());
        final Block blockAt = player.getWorld().getBlockAt(location);
        if(blockAt.getType().isSolid()){

            if(blockAt.hasMetadata("Protego")){
                if (reflectSpell(blockAt, player, p1, vector)) {
                    canHitCaster = true;
                    return null;
                }
            }
            hit = HitTypes.BLOCK;
            return location;
        }

        final Entity[] entities = location.getWorld().getChunkAt(location).getEntities();
        for (Entity entity : entities) {
            if(canHitCaster || !entity.equals(player))
                if(entity instanceof LivingEntity && !((LivingEntity) entity).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
                    if(entity.getBoundingBox().expand(0.12).contains(p1)){
                        hitEnt = entity;
                        hit = HitTypes.ENTITY;
                        return location;
                    }
                }
        }


        p1.add(vector);

        return null;
    }
}
