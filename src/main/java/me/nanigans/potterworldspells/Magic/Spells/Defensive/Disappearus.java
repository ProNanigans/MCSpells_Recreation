package me.nanigans.potterworldspells.Magic.Spells.Defensive;

import de.slikey.effectlib.effect.SphereEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Defensive;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Disappearus extends Defensive {
    private final int duration = 120;
    public Disappearus(Wand wand) {
        super(wand);
        super.cooldown = Spells.DISAPPEARUS.getCooldown();
        addCooldown();
        SphereEffect sphere = new SphereEffect(plugin.manager);
        sphere.asynchronous = true;
        sphere.particle = Particle.REDSTONE;
        sphere.color = wand.getWandColor();
        sphere.radius = 2;
        sphere.duration = 1500;
        sphere.particles = 20;
        sphere.setEntity(player);
        sphere.radiusIncrease = 0;
        sphere.start();
        player.getWorld().playSound(player.getLocation(), "magic.evilchargeup6", 100, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));

    }
}
