package me.nanigans.potterworldspells.Magic.Spells.Mobility;

import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.util.DynamicLocation;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Mobility;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Apparate extends Mobility {

    private long speed = 0;
    private double gravity = 0;
    private double distance = 25D;
    private double spacing = 0.5;
    protected double cooldown = 5;
    private Particle.DustOptions color = new Particle.DustOptions(
        Color.AQUA, 1f
    );
    private Particle particle = Particle.REDSTONE;

    public Apparate(Wand wand) {
        super(wand);

        if(!ItemUtils.hasNBT(wand.getLastSpell(), Data.COOLDOWN.toString(), Data.COOLDOWN.getType())) {
            super.cooldDown = cooldown;
            addCooldown();

            AnimatedBallEffect ball = new AnimatedBallEffect(plugin.manager);
            ball.color = color.getColor();
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
        }else{
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        }

    }

    protected void onHit(Location hit){
        new BukkitRunnable() {
            @Override
            public void run() {
                final Location location = new Location(player.getWorld(), hit.getX(), hit.getY(), hit.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(location.clone().add(0, 1.5, 0));
                player.getWorld().playSound(player.getLocation(), "magic.apparate", 1, 1);

            }
        }.runTask(plugin);
    }

    protected Location whileFiring(Vector p1, Vector vector){
        player.getWorld().spawnParticle(particle, p1.getX(), p1.getY(), p1.getZ(), 2, color);
        p1.add(vector.subtract(new Vector(0, gravity, 0)));

        if(player.getWorld().getBlockAt(p1.toLocation(player.getWorld())).getType().isSolid()){

            return p1.toLocation(player.getWorld());

        }

        return null;
    }
    }


