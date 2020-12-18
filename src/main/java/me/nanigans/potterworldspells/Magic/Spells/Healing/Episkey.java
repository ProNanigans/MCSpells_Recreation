package me.nanigans.potterworldspells.Magic.Spells.Healing;

import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Healing;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Config.JsonPaths;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Episkey extends Healing implements SpellCasting {
    private final double spacing = 0.25;
    private final double range = Double.parseDouble(getData(this, JsonPaths.RANGE.path));
    private final Particle particle = Particle.REDSTONE;
    private final double hitRad = Double.parseDouble(getData(this, JsonPaths.HITBOX.path));
    private final boolean endSpell = false;
    private final short healAmt = Short.parseShort(getData(this, JsonPaths.HEALAMT.path));
    private final Particle.DustOptions color = new Particle.DustOptions(Color.MAROON, 1);
    private HitTypes hit;
    private Entity hitEnt;

    public Episkey(Wand wand) {
        super(wand);
        player.playSound(player.getEyeLocation(), "magic.hit", 500, 1);
        super.cooldDown = Spells.EPISKEY.getCooldown();
        cast(range, spacing, 0, this::whileFiring, this::onHit);
        addCooldown();
    }

    @Override
    public void onHit(Location hitLoc) {

        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 5, 0, 0, 0, 0.1);

        if(hit == null || hit == HitTypes.ENTITY){
            Entity healed = player;
            if(hitEnt != null) healed = hitEnt;
             healed.getWorld().playSound(healed.getLocation(), "magic.heal1", 1, 1);

            WarpEffect effect = new WarpEffect(plugin.manager);
            effect.particle = Particle.HEART;
            effect.asynchronous = true;
            effect.particles = 4;
            effect.offset = new Vector(0, -1.5, 0);
            effect.iterations = 8;
            effect.grow = 0.5F;
            effect.setEntity(healed);
            effect.start();

            Entity finalHealed = healed;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(finalHealed instanceof LivingEntity){
                        final LivingEntity healed1 = (LivingEntity) finalHealed;
                        healed1.setHealth(Math.min(healed1.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                                healed1.getHealth()+healAmt));
                    }
                }
            }.runTaskLaterAsynchronously(plugin, 40);

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        player.getWorld().spawnParticle(particle, p1.getX(), p1.getY(), p1.getZ(), 2, color);
        if(Math.random() > .5)
        player.getWorld().spawnParticle(Particle.CRIT, p1.getX(), p1.getY()-0.005, p1.getZ(), 2, 0, 0, 0, 0);

        Location loc = p1.toLocation(player.getWorld());
        final Block blockAt = player.getWorld().getBlockAt(loc);
        if(blockAt.getType().isSolid()){
            hit = HitTypes.ENTITY;
            return loc;
        }

        final Entity[] entities = loc.getWorld().getChunkAt(loc).getEntities();
        for (Entity entity : entities) {
            if(!entity.equals(player))
            if(entity instanceof LivingEntity && !((LivingEntity) entity).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
                if(entity.getBoundingBox().expand(hitRad).contains(p1)){
                    hitEnt = entity;
                    return loc;
                }
            }
        }

        if(endSpell) return loc;

        p1.add(vector.subtract(new Vector(0, 0, 0)));

        return null;
    }
}
