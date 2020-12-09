package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Wand;

abstract public class Spell {
    protected long cooldDown;
    protected Wand wand;

    public Spell(Wand wand){
        this.wand = wand;
    }

    abstract protected void cast();

    protected void runCooldown(){

    }

    protected void removeCooldown(){

    }


}
