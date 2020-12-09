package me.nanigans.potterworldspells.Magic.Spells.Mobility;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Mobility;
import me.nanigans.potterworldspells.Magic.Wand;

public class Apparate extends Mobility {
    public Apparate(Wand wand) {
        super(wand);
        player.playSound(player.getLocation(), "magic.apparate", 1, 1);
    }

    @Override
    protected void cast(){



    }

}
