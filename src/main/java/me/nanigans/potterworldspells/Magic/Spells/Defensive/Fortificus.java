package me.nanigans.potterworldspells.Magic.Spells.Defensive;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.SphereEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Defensive;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Fortificus extends Defensive {

    public Fortificus(Wand wand) {
        super(wand);
        super.cooldDown = Spells.FORTIFICUS.getCooldown();
        addCooldown();
        SphereEffect sphere = new SphereEffect(plugin.manager);
        sphere.asynchronous = true;
        sphere.particle = Particle.VILLAGER_ANGRY;
        sphere.color = wand.getWandColor();
        sphere.radius = 2;
        sphere.type = EffectType.INSTANT;

        sphere.particles = 40;
        sphere.setEntity(player);
        sphere.radiusIncrease = 0;
        sphere.start();
        player.getWorld().playSound(player.getLocation(), "magic.evilchargeup6", 100, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 120, 1));

    }

}
