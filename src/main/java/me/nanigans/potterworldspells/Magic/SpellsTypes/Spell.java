package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.entity.Player;

abstract public class Spell {
    protected long cooldDown;
    protected Wand wand;
    protected Player player;

    public Spell(Wand wand){
        this.wand = wand;
        this.player = wand.getPlayer();
    }

    abstract protected void cast();

    protected void runCooldown(){

    }

    protected void removeCooldown(){

    }


}
