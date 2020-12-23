package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.FountainEffect;
import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class Petrificus_Totalus extends Crowd_Control implements SpellCasting {
    private HitTypes hit;
    private Entity hitEnt;

    public Petrificus_Totalus(Wand wand) {
        super(wand);

        WarpEffect warp = new WarpEffect(plugin.manager);
        warp.offset = new Vector(0, -1, 0);
        warp.setEntity(player);
        warp.radius = 0.5F;
        warp.asynchronous = true;
        warp.particle = Particle.FIREWORKS_SPARK;
        warp.duration = 800;
        warp.particles = 7;
        warp.rings = 7;
        warp.start();
        player.getWorld().playSound(player.getLocation(), "magic.chargeup5", 100, 1);

        final double spacing = 0.35D;
        final double range = 40D;
        final long speed = 1;
        warp.callback = () -> {
            player.getWorld().playSound(player.getLocation(), "magic.evilwhoosh2", 100, 2);
            cast(range, spacing, speed, Petrificus_Totalus.this::whileFiring, Petrificus_Totalus.this::onHit);
        };


    }

    @Override
    public void onHit(Location hitLoc) {

        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 5, 0, 0, 0, 0.1);

        if(hit != null){
            player.getWorld().playSound(player.getLocation(), "magic.hit", 100, 2);
            if(hit == HitTypes.ENTITY){

                FountainEffect fountain = new FountainEffect(plugin.manager);
                fountain.particle = Particle.FIREWORKS_SPARK;
                fountain.particlesStrand = 6;
                fountain.particlesSpout = 1;
                fountain.radius = .5f;
                fountain.height = (float) hitEnt.getBoundingBox().getHeight()+0.2F;
                fountain.duration = 3000;
                fountain.particleOffsetY = 0.2F;
                fountain.strands = 5;
                fountain.heightSpout = 0;
                fountain.iterations = 5;
                fountain.offset = new Vector(0, -1.5, 0);
                fountain.setEntity(hitEnt);
                fountain.start();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final LivingEntity hitEnt = (LivingEntity) Petrificus_Totalus.this.hitEnt;
                        hitEnt.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 100));
                        if(hitEnt instanceof Player)
                            hitEnt.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 80, 250));
                        hitEnt.damage(0.1);
                    }
                }.runTask(plugin);

            }

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location location = p1.toLocation(player.getWorld());
        location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0.2, 0.2, 0.2, 0);
        location.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, location,2, 0, 0, 0, 0);
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
                    if(entity.getBoundingBox().expand(0.22).contains(p1)){
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
