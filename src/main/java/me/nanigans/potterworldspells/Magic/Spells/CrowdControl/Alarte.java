package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.effect.WarpEffect;
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
import org.bukkit.util.Vector;

public class Alarte extends Crowd_Control implements SpellCasting {
    private HitTypes hit;
    private Entity hitEnt;

    public Alarte(Wand wand) {
        super(wand);
        final double range = 32.5D;
        final double spacing = 0.5;
        final long speed = 1;
        player.getWorld().playSound(player.getLocation(), "magic.whoosh4", 100, 1);
        super.cast(range, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hit != null){
            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 10, 0, 0, 0, 0.2);
            hitLoc.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

            if(hit == HitTypes.ENTITY){

                hitEnt.setVelocity(new Vector(0, 1, 0));
                hitEnt.getWorld().playSound(hitEnt.getLocation(), Sound.ENTITY_PLAYER_HURT, 30, 1);

                WarpEffect warp = new WarpEffect(plugin.manager);
                warp.particle = Particle.REDSTONE;
                warp.color = Color.AQUA;
                warp.setEntity(hitEnt);
                warp.grow = 0.5F;
                warp.duration = 500;
                warp.start();

            }

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location location = p1.toLocation(player.getWorld());
        player.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, new Particle.DustOptions(
                Color.AQUA, 1
        ));

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
