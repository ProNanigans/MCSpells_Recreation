package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.inventory.ItemStack;

import java.util.List;

abstract public class Healing extends Spell{

    public Healing(Wand wand) {
        super(wand);
    }

    protected void addCooldownToOthers(Healing spellToNot){

        final List<ItemStack> spellsByName = wand.getSpellsByName(Spells.EPISKEY, Spells.FERUVIO, Spells.VULNERA);
        for (ItemStack itemStack : spellsByName) {
            ItemUtils.setData(itemStack, Data.COOLDOWN.toString(), Data.COOLDOWN.getType(), 10D);
            Spell.reloadCooldown(itemStack, wand, plugin, );
        }

    }

}
