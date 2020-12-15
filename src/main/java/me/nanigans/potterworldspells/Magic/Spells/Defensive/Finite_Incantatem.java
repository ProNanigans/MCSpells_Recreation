package me.nanigans.potterworldspells.Magic.Spells.Defensive;

import de.slikey.effectlib.effect.AtomEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Defensive;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.NegativeEffects;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Finite_Incantatem extends Defensive {
    public Finite_Incantatem(Wand wand) {
        super(wand);

        AtomEffect atom = new AtomEffect(plugin.manager);
        atom.asynchronous = true;
        atom.particleOrbital = Particle.REDSTONE;
        atom.colorOrbital = wand.getWandColor();
        atom.particleNucleus = Particle.ENCHANTMENT_TABLE;
        atom.duration = 1500;
        atom.radius = 2;
        atom.setEntity(player);
        atom.start();
        player.playSound(player.getLocation(), "magic.healchargeup1", 100, 1);

        for(NegativeEffects effect : NegativeEffects.values()){
            if(player.hasPotionEffect(effect.effect))
                player.removePotionEffect(effect.effect);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));

    }
}
