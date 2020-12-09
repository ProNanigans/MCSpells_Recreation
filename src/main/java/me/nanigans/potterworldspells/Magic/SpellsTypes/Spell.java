package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.PotterWorldSpells;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

abstract public class Spell {
    protected long cooldDown;
    protected Wand wand;
    protected Player player;
    protected PotterWorldSpells plugin;

    public Spell(Wand wand){
        this.wand = wand;
        this.player = wand.getPlayer();
        this.plugin = wand.getPlugin();
    }

    /**
     * Casts the spell. This will only create a loop between two points and will not increment the current position. You will
     * have to do this yourself
     * @param length the distance the spell will travel
     * @param spacing the spacing per particle
     * @param speed the speed of the particle
     * @param locCb the callback for when the spell is firing
     * @param endCb when the spell has hit something or has reached its max distance
     * @requires speed >= 0
     */
    protected void cast(double length, double spacing, final long speed, Callback locCb, Consumer<Location> endCb){
        Location p2 = player.getLocation().add(player.getLocation().getDirection().multiply(length));
        double distance = getSpellCastLoc().distance(p2);
        Vector p1 = getSpellCastLoc().toVector();
        Vector vector = p2.toVector().clone().subtract(p1).normalize().multiply(spacing);

        new BukkitRunnable() {
            @Override
            public void run() {

                for(double len = 0; len < distance; len += spacing){

                    if(speed > 0){
                        try{
                            Thread.sleep(speed);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }

                    final Location call = locCb.call(p1, vector);
                    if(call != null){
                        endCb.accept(call);
                        break;
                    }
                }
                endCb.accept(p1.toLocation(player.getWorld()));

            }
        }.runTaskAsynchronously(plugin);
    }

    public interface Callback{
        Location call(Vector p1, Vector vector);
    }

    protected void runCooldown(){

    }


    protected void removeCooldown(){
    }

    protected Location getSpellCastLoc(){
        if(player.getMainHand() == MainHand.RIGHT)
            return getRightArm();
        else return getLeftArm();
    }

    private Location getRightArm() {
        Location location = player.getEyeLocation().subtract(0, 0.25, 0);
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(0.3));
    }

    private Location getLeftArm() {
        Location location = player.getEyeLocation().subtract(0, 0.25, 0);
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(0.3));
    }


}
