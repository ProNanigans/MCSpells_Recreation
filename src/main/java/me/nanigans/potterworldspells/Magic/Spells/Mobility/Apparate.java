package me.nanigans.potterworldspells.Magic.Spells.Mobility;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Mobility;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Apparate extends Mobility {

    enum SpellData{
        SPEED(1L),
        GRAVITY(0.0),
        DISTANCE(25),
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
        player.playSound(player.getLocation(), "magic.apparate", 1, 1);
        this.cast();
    }

    @Override
    protected void cast(){
        double length = Double.parseDouble(SpellData.DISTANCE.value.toString());
        Location p2 = player.getLocation().add(player.getLocation().getDirection().multiply(length));
        double distance = getSpellCastLoc().distance(p2);
        Vector p1 = getSpellCastLoc().toVector();
        final double spacing = Double.parseDouble(SpellData.SPACING.getValue());
        Vector vector = p2.toVector().clone().subtract(p1).normalize().multiply(spacing);

        new BukkitRunnable() {
            final long speed = Long.parseLong(SpellData.SPEED.value.toString());
            @Override
            public void run() {

                for(double len = 0; len < distance; len += spacing) {

                    if (speed > 0) {
                        try {
                            Thread.sleep(speed);

                            player.getWorld().spawnParticle(Particle.valueOf(SpellData.PARTICLE.getValue()), p1.getX(), p1.getY(), p1.getZ(), 2, SpellData.PARTICLECOLOR.value);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        p1.add(vector.subtract(new Vector(0, Double.parseDouble(SpellData.GRAVITY.getValue()), 0)));
                    }

                }
            }
        }.runTaskAsynchronously(plugin);

    }

}
