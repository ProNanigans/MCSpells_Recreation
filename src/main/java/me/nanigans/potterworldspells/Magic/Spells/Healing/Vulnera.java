package me.nanigans.potterworldspells.Magic.Spells.Healing;

import de.slikey.effectlib.effect.HelixEffect;
import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Healing;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Vulnera extends Healing implements SpellCasting {

    private final double range = 39D;
    private final double gravity = 0.005;
    private final float radius = 3F;
    private final short heal = 1;

    public Vulnera(Wand wand) {
        super(wand);
        super.cooldDown = Spells.VULNERA.getCooldown();
        cast(range, 0.5, 3, this::whileFiring, this::onHit);
        addCooldown();
    }

    @Override
    public void onHit(Location hit) {

        player.getWorld().playSound(hit, "magic.heal1", 1, 1);

        WarpEffect warp = new WarpEffect(plugin.manager);
        warp.asynchronous = true;
        warp.particle = Particle.CRIT;
        warp.grow = 0.2F;
        warp.setLocation(hit);
        warp.radius = radius;
        warp.particles = 10;
        warp.particleCount = 10;
        warp.start();

        HelixEffect effect = new HelixEffect(plugin.manager);
        effect.asynchronous = true;
        effect.particleSize = 0.05F;
        effect.period = 20;
        effect.particles = 10;
        effect.curve = (float) Math.PI;
        effect.particle = Particle.HEART;
        effect.setLocation(hit);
        effect.duration = 8000;
        effect.radius = radius;
        effect.start();
        player.getWorld().playSound(hit, "magic.vulnera", 1, 1);

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                tick++;
                if(tick > 8) this.cancel();

                final Entity[] entities = getEntitiesRadius(hit, 3);
                for (Entity entity : entities) {
                    if(entity instanceof LivingEntity){
                        LivingEntity ent = (LivingEntity) entity;
                        final AttributeInstance attribute = ent.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        ent.setHealth(Math.min(
                                attribute.getValue(), ent.getHealth()+heal
                        ));
                    }
                }

            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        Location loc = p1.toLocation(player.getWorld());
        player.getWorld().spawnParticle(Particle.HEART, p1.getX(), p1.getY(), p1.getZ(), 1, .5, .5, .5, 0);

        if(loc.getWorld().getBlockAt(loc).getType().isSolid())
            return loc;

        p1.add(vector.subtract(new Vector(0, gravity, 0)));

        return null;
    }
}
