package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
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

public class Rictusempra extends Crowd_Control implements SpellCasting {
    private HitTypes hit;
    private Entity hitEnt;
    Vector dir;
    public Rictusempra(Wand wand) {
        super(wand);
        final double range = 40D;
        final double spacing = 0.35;
        final long speed = 1;
        player.getWorld().playSound(player.getLocation(), "magic.whoosh3", 100, 1);
        super.cast(range, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hitLoc != null) {
            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 20, 0, 0, 0, 0.2);
            hitLoc.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

            if(hit == HitTypes.ENTITY){

                new BukkitRunnable() {
                    short ticks = 0;
                    @Override
                    public void run() {
                        hitEnt.getWorld().playSound(hitEnt.getLocation(), Sound.ENTITY_WITCH_AMBIENT, 50, 5);
                        hitEnt.getWorld().playSound(hitEnt.getLocation(), Sound.ENTITY_PLAYER_HURT, 50, 1);
                        hitEnt.setVelocity(dir.clone().setY(0.25));
                        ticks++;
                        if(ticks > 5) this.cancel();
                    }
                }.runTaskTimerAsynchronously(plugin, 0, 5);

            }


        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location loc = p1.toLocation(player.getWorld());
        player.getWorld().spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 2, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(0, 0, 128), 1));

        final Block blockAt = player.getWorld().getBlockAt(loc);
        if(blockAt.getType().isSolid()){

            if(blockAt.hasMetadata(Data.REFLECT.toString())){
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
                        dir = vector;
                        return loc;
                    }
                }
        }

        p1.add(vector);
        return null;

    }
}
