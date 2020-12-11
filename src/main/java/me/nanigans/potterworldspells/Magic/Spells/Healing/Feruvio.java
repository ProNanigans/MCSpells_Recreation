package me.nanigans.potterworldspells.Magic.Spells.Healing;

import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Healing;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Feruvio extends Healing {
    private final int duration = 10000;
    public Feruvio(Wand wand) {
        super(wand);
        super.cooldDown = 16D;
        player.playSound(player.getEyeLocation(), "magic.heal1", 1, 1);

        player.playSound(player.getEyeLocation(), "magic.healwhoosh2", 1, 1);
        WarpEffect effect = new WarpEffect(plugin.manager);
        effect.asynchronous = true;
        effect.offset = new Vector(0, -1.5, 0);
        effect.setEntity(player);
        effect.duration = duration;
        effect.particle = Particle.SPELL_MOB;
        effect.color = Color.fromRGB(72, 255, 0);
        effect.grow = 0;
        effect.radius = 0.5F;
        effect.start();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration/1000*20, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration/1000*20, 2));
        addCooldown();

    }



}
