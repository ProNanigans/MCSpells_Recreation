package me.nanigans.potterworldspells.Magic.Spells.Defensive;

import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Defensive;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class Arresto_Momentum extends Defensive {
    public Arresto_Momentum(Wand wand) {
        super(wand);
        super.cooldDown = Spells.ARRESTO_MOMENTUM.getCooldown();
        saveFallTime = System.currentTimeMillis()+15000;
        addCooldown();
        WarpEffect effect = new WarpEffect(plugin.manager);
        effect.radius = 0.5F;
        effect.asynchronous = true;
        effect.particle = Particle.SPELL;
        effect.duration = 3000;
        effect.grow = 0;
        effect.offset = new Vector(0, -1.5, 0);
        effect.setEntity(player);
        effect.start();
        player.getWorld().playSound(player.getLocation(), "magic.chargeup4", 100, 1);
    }
}
