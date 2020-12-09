package me.nanigans.potterworldspells.Magic.Spells.Mobility;

import me.nanigans.potterworldspells.Magic.SpellsTypes.Mobility;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Apparate extends Mobility {

    enum SpellData{
        SPEED(0L),
        GRAVITY(0D),
        DISTANCE(25D),
        SPACING(0.5),
        PARTICLECOLOR(new Particle.DustOptions(
                Color.AQUA, 1f
        )),
        PARTICLE(Particle.REDSTONE);
        public Object value;

        SpellData(Object d) {
            this.value = d;
        }

        public String getValue() {
            return value.toString();
        }
    }


    public Apparate(Wand wand) {
        super(wand);
        player.getWorld().playSound(player.getLocation(), "magic.apparate", 1, 1);

        super.cast(Double.parseDouble(SpellData.DISTANCE.getValue()), Double.parseDouble(SpellData.SPACING.getValue()),
                Long.parseLong(SpellData.SPEED.getValue()), this::whileFiring, this::onHit);

    }

    protected void onHit(Location hit){
        new BukkitRunnable() {
            @Override
            public void run() {
                final Location location = new Location(player.getWorld(), hit.getX(), hit.getY(), hit.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(location);
                player.getWorld().playSound(player.getLocation(), "magic.apparate", 1, 1);

            }
        }.runTask(plugin);
    }

    protected Location whileFiring(Vector p1, Vector vector){

        player.getWorld().spawnParticle(Particle.valueOf(SpellData.PARTICLE.getValue()), p1.getX(), p1.getY(), p1.getZ(), 2, SpellData.PARTICLECOLOR.value);
        p1.add(vector.subtract(new Vector(0, Double.parseDouble(SpellData.GRAVITY.getValue()), 0)));
        return null;
    }
    }


