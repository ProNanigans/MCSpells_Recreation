package me.nanigans.potterworldspells.Utils;

import org.bukkit.potion.PotionEffectType;

public enum NegativeEffects {//enum of removable negative potion effects

    SLOWNESS(PotionEffectType.SLOW),
    SLOW_DIG(PotionEffectType.SLOW_DIGGING),
    HARM(PotionEffectType.HARM),
    CONFUSION(PotionEffectType.CONFUSION),
    BLINDNESS(PotionEffectType.BLINDNESS),
    WEAKNESS(PotionEffectType.WEAKNESS),
    POISON(PotionEffectType.POISON),
    WITHER(PotionEffectType.WITHER);


    public final PotionEffectType effect;

    NegativeEffects(PotionEffectType effect) {
        this.effect = effect;
    }
}
