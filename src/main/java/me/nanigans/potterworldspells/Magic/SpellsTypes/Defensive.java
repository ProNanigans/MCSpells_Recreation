package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Spells;

import java.util.ArrayList;
import java.util.List;

abstract public class Defensive extends Spell{

    List<Spells> defensiveSpells = new ArrayList<Spells>(){{
        final Spells[] values = Spells.values();
        for (Spells value : values) {
            if(value.getSpellType().equals("Defensive"))
                add(value);
        }
    }};

    public Defensive(Wand wand) {
        super(wand);
    }

    protected void addDefensiveCooldowns(){



    }

}
