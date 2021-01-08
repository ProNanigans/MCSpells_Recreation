package me.nanigans.potterworldspells.Magic;

import me.nanigans.potterworldspells.Magic.SpellsTypes.Spell;
import org.bukkit.inventory.ItemStack;

import java.util.TimerTask;

public class Cooldown extends TimerTask {
    private double cooldown;
    private ItemStack item;
    public Cooldown(Spell spell){
        this.cooldown = spell.getCooldown();
        this.item = spell.getWand().getLastSpell();
        item.setAmount(((int) cooldown));
    }

    public Cooldown(double cooldown, ItemStack item){
        this.cooldown = cooldown;
        this.item = item;
        item.setAmount(((int) cooldown));
    }

    @Override
    public void run() {

        item.setAmount(item.getAmount()-1);
        if(item.getAmount() <= 1 || cooldown == 0)
            this.cancel();

        cooldown--;
    }

    @Override
    public boolean cancel() {

        return super.cancel();
    }
}
