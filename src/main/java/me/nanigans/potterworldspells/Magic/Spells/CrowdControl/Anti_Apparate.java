package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Anti_Apparate extends Crowd_Control implements SpellCasting {

    private HitTypes hit;
    private Entity hitEnt;

    public Anti_Apparate(Wand wand) {
        super(wand);

        double range = 52D;
        double spacing = 0.25;
        long speed = 1;

        super.cast(range, spacing, speed, this::whileFiring, this::onHit);

    }

    @Override
    public void onHit(Location hitLoc) {

        hitLoc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 20, 0, 0, 0, 0.3);
        player.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

        if(hitEnt instanceof Player){
            //TODO figure out forced player spell cooldowns
        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        double red = 105 / 255D;
        double green = 105 / 255D;
        double blue = 105 / 255D;
        player.getWorld().spawnParticle(Particle.SPELL_MOB, p1.toLocation(player.getWorld()), 0, red, green, blue);
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, p1.getX(), p1.getY(), p1.getZ(), 0, 0, 0, 0);

        final Location location = p1.toLocation(player.getWorld());
        final Block blockAt = player.getWorld().getBlockAt(location);
        if(blockAt.getType().isSolid()){
            hit = HitTypes.BLOCK;
            return location;
        }

        final Entity[] entities = location.getWorld().getChunkAt(location).getEntities();
        for (Entity entity : entities) {
            if(!entity.equals(player))
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
