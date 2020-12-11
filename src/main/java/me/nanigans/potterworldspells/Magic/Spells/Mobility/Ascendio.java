package me.nanigans.potterworldspells.Magic.Spells.Mobility;

import de.slikey.effectlib.effect.TraceEffect;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Mobility;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class Ascendio extends Mobility implements SpellCasting {
    private double launchAmt = 2.5;

    public Ascendio(Wand wand) {
        super(wand);

        player.getWorld().playSound(player.getEyeLocation(), "magic.ascendio1", 2, 1);
        final Vector direction = player.getEyeLocation().getDirection();
        player.setVelocity(direction.multiply(launchAmt));
        saveFallTime = System.currentTimeMillis()+5000;
        TraceEffect effect = new TraceEffect(plugin.manager);
        effect.particle = Particle.SPELL;
        effect.color = Color.WHITE;
        effect.asynchronous = true;
        effect.setEntity(player);
        effect.particleOffsetX = 0.8F;
        effect.particleOffsetZ = 0.5F;
        effect.iterations = 20;
        effect.start();

    }

    @Override
    public void onHit(Location hit) {

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        return null;
    }
}
