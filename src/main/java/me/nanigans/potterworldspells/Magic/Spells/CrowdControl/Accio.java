package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Accio extends Crowd_Control implements SpellCasting {

    private final Vector ending;
    private HitTypes hit;
    private Entity hitEnt;

    public Accio(Wand wand) {
        super(wand);
        double distance = 30D;
        ending = player.getLocation().add(player.getLocation().getDirection().multiply(distance)).toVector();
        double spacing = 0.5;
        long speed = 5L;
        player.getWorld().playSound(player.getLocation(), "magic.whip2", 100, 1);
        ignoreCancel = true;
        super.cast(distance, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hit != null && hit == HitTypes.ENTITY){

            hitEnt.setVelocity(player.getLocation().getDirection().multiply(-3));

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        Location particleLoc = ending.subtract(vector).toLocation(player.getWorld());
        particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc.getX(), particleLoc.getY(), particleLoc.getZ(),
                2, .5, .5, .5, new Particle.DustOptions(Color.WHITE, 1));

        final Location location = p1.toLocation(player.getWorld());
        final Block block = player.getWorld().getBlockAt(location);
        if(block.getType().isSolid()){
            hit = HitTypes.BLOCK;
            return location;
        }
        final Entity[] entities = location.getWorld().getChunkAt(location).getEntities();
        for (Entity entity : entities) {
            if(!entity.equals(player)){

                if(entity.getBoundingBox().expand(1).contains(p1)){
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
