package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Particles.VortexEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Ventus extends Crowd_Control {
    public Ventus(Wand wand) {
        super(wand);

        VortexEffect vort = new VortexEffect(plugin.manager);
        vort.asynchronous = true;
        vort.duration = 3000;
        vort.particleCount = 3;
        vort.particle = Particle.SPELL;
        vort.grow = 0.25F;
        vort.period = -5;
        vort.helixes = 2;
        vort.callback = this::castSpell;
        vort.setEntity(player);
        vort.start();


    }

    private void castSpell(Location location) {
        final Entity[] entities = getEntitiesRadius(location, 5);
        Vector v = player.getLocation().getDirection().add(new Vector(0, 0.2, 0));
        for (Entity entity : entities) {
            if(!entity.equals(player))
            entity.setVelocity(v);
        }
    }


}
