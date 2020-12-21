package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.HelixEffect;
import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Immobulus extends Crowd_Control {
    public Immobulus(Wand wand) {
        super(wand);

        player.getWorld().playSound(player.getLocation(), "magic.evilchargeup5", 100, 1);
        WarpEffect warp = new WarpEffect(plugin.manager);
        warp.asynchronous = true;
        warp.offset = new Vector(0, -1, 0);
        warp.setEntity(player);
        warp.radius = 0.6F;
        warp.duration = 1500;
        warp.color = Color.PURPLE.mixColors(Color.BLACK);
        warp.particle = Particle.REDSTONE;
        warp.particles = 6;
        warp.start();

        new BukkitRunnable() {
            @Override
            public void run() {

                HelixEffect helix = new HelixEffect(plugin.manager);
                helix.asynchronous = true;
                helix.particle = Particle.REDSTONE;
                helix.color = Color.PURPLE.mixColors(Color.BLACK);
                helix.setLocation(player.getLocation());
                helix.particles = 30;
                helix.radius = 4;
                helix.type = EffectType.INSTANT;
                helix.start();
                player.getWorld().playSound(player.getLocation(), "magic.evilwhoosh3", 100, 1);
                final Entity[] entities = getEntitiesRadius(player.getLocation(), 4);
                for (Entity entity : entities) {
                    if(!entity.equals(player) && entity instanceof LivingEntity){

                        WarpEffect warp = new WarpEffect(plugin.manager);
                        warp.asynchronous = true;
                        warp.offset = new Vector(0, -1, 0);
                        warp.setEntity(entity);
                        warp.radius = 0.6F;
                        warp.duration = 1500;
                        warp.color = Color.PURPLE.mixColors(Color.BLACK);
                        warp.particle = Particle.REDSTONE;
                        warp.particles = 6;
                        warp.start();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 100));
                                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 30, 250));

                            }
                        }.runTask(plugin);
                    }
                }

            }
        }.runTaskLaterAsynchronously(plugin, 30);

    }
}
