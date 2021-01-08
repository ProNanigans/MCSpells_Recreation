package me.nanigans.potterworldspells.Magic.Spells.Mobility;

import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Mobility;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Volo extends Mobility {

    private int effectLength = 60;

    public Volo(Wand wand) {
        super(wand);
        super.cooldown = Spells.VOLO.getCooldown();
        player.playSound(player.getEyeLocation(), "magic.whoosh6", 1, 1);

        WarpEffect warp = new WarpEffect(plugin.manager);
        warp.asynchronous = true;
        warp.color = wand.getWandColor();
        warp.offset = new Vector(0, -1, 0);
        warp.particle = Particle.CRIT;
        warp.grow = 0.5F;
        warp.iterations = 5;
        warp.setEntity(player);
        warp.start();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 3));

        WarpEffect effect = new WarpEffect(plugin.manager);
        effect.asynchronous = true;
        effect.color = wand.getWandColor();
        effect.particle = Particle.REDSTONE;
        effect.offset = new Vector(0, -1.5, 0);
        effect.setEntity(player);
        effect.duration = effectLength/20*1000;
        effect.radius = 0.5f;
        effect.grow = 0;
        effect.start();
        addCooldown();
    }
}
