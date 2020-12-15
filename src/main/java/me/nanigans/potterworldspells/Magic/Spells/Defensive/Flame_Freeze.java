package me.nanigans.potterworldspells.Magic.Spells.Defensive;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.FountainEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Defensive;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class Flame_Freeze extends Defensive {

    private double cooldown = 5D;

    public Flame_Freeze(Wand wand) {
        super(wand);
        super.cooldDown = cooldown;
        addCooldown();

        FountainEffect f = new FountainEffect(plugin.manager);
        f.particle = Particle.SNOW_SHOVEL;
        f.particlesStrand = 4;
        f.type = EffectType.INSTANT;
        f.particlesSpout = 1;
        f.radius = .5f;
        f.height = 2;
        f.strands = 4;
        f.heightSpout = 0;
        f.iterations = 5;
        f.offset = new Vector(0, -1.5, 0);
        f.setEntity(player);
        f.start();

        FountainEffect fountain = new FountainEffect(plugin.manager);
        fountain.particle = Particle.FLAME;
        fountain.particlesStrand = 6;
        fountain.type = EffectType.INSTANT;
        fountain.particlesSpout = 1;
        fountain.radius = .5f;
        fountain.height = 2;
        fountain.particleOffsetY = 0.2F;
        fountain.strands = 5;
        fountain.heightSpout = 0;
        fountain.iterations = 5;
        fountain.offset = new Vector(0, -1.5, 0);
        fountain.setEntity(player);
        fountain.start();
        player.getWorld().playSound(player.getLocation(), "magic.rennervate", 100, 1.5F);
        player.setFireTicks(0);

    }
}
