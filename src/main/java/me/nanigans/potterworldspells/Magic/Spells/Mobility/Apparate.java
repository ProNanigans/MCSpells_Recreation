package me.nanigans.potterworldspells.Magic.Spells.Mobility;

import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.DynamicLocation;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Mobility;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Apparate extends Mobility implements SpellCasting {

    private long speed = 0;
    private double gravity = 0;
    private double distance = 25D;
    private double spacing = 0.5;
    private Particle.DustOptions color = new Particle.DustOptions(wand.getWandColor(), 1);

    private Particle particle = Particle.REDSTONE;

    public Apparate(Wand wand) {
        super(wand);

            super.cooldown = Spells.APPARATE.getCooldown();
            addCooldown();

            AnimatedBallEffect ball = new AnimatedBallEffect(plugin.manager);
            ball.color = wand.getWandColor();
            ball.setEntity(player);
            ball.asynchronous = true;
            ball.iterations = 20;
            ball.yOffset = -0.32F;
            ball.yFactor = 1.5F;
            ball.particle = particle;
            ball.start();

            player.getWorld().playSound(player.getLocation(), "magic.apparate", 1, 1);
            addCooldown();
            super.cast(distance, spacing, speed, this::whileFiring, this::onHit);

    }

    @Override
    public void onHit(Location hit){
        new BukkitRunnable() {
            @Override
            public void run() {
                final Location location = new Location(player.getWorld(), hit.getX(), hit.getY(), hit.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(location.clone().add(0, 1.5, 0));
                player.getWorld().playSound(player.getLocation(), "magic.apparate", 1, 1);
                saveFallTime = System.currentTimeMillis()+5000;

            }
        }.runTask(plugin);
    }

    @Override
    public Location whileFiring(Vector p1, Vector vector){
        player.getWorld().spawnParticle(particle, p1.getX(), p1.getY(), p1.getZ(), 2, color);
        p1.add(vector.subtract(new Vector(0, gravity, 0)));

        if(player.getWorld().getBlockAt(p1.toLocation(player.getWorld())).getType().isSolid()){

            return p1.toLocation(player.getWorld());

        }

        return null;
    }

}


