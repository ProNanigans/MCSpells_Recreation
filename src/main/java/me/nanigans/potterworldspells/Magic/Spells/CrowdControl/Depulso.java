package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.effect.ConeEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class Depulso extends Crowd_Control {
    public Depulso(Wand wand) {
        super(wand);
        ConeEffect cone = new ConeEffect(plugin.manager);
        cone.asynchronous = true;
        cone.setLocation(player.getEyeLocation());
        cone.particle = Particle.REDSTONE;
        cone.color = Color.AQUA;
        cone.lengthGrow = .03f;
        cone.radiusGrow = 2/100F;
        cone.iterations = 20;
        cone.period = 0;
        cone.start();
        player.playSound(player.getLocation(), "magic.flying", 100, 1);


        Location start = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2));
        BoundingBox box = new BoundingBox(start.getX(), start.getY(), start.getZ(), start.getX(), start.getY(), start.getZ());

        final double radius = 2;
        Collection<Entity> entities = player.getWorld().getNearbyEntities(box.expand(player.getLocation().getDirection(), 4)//range
                .expand(new Vector(-1, 0, 0), radius).expand(new Vector(1, 0, 0), radius).expand(new Vector(0, 1, 0), radius)
                .expand(new Vector(0, -1, 0), radius).expand(new Vector(0, 0, -1), radius).expand(new Vector(0, 0, 1), radius));

        entities.remove(player);
        for(Entity ent : entities){
            ent.setVelocity(player.getEyeLocation().getDirection().multiply(1.5).add(new Vector(0, 1, 0)));
        }

    }
}
