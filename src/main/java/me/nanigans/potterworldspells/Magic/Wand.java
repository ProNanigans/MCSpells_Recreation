package me.nanigans.potterworldspells.Magic;

import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class Wand implements Listener {

    private final Player player;
    private final ItemStack wand;

    public Wand(Player player){
        this.player = player;
        this.wand = player.getInventory().getItemInMainHand();
    }

    public void loadInventory(){

        if(ItemUtils.hasNBT(wand, Data.INVENTORY.toString(), Data.INVENTORY.getType())){


        }else{// we need to create the first inventory

        }

    }

    private void setUpInventory(){

        final Spells[] values = Spells.values();
        Map<Integer, ItemStack> inventorySpellPlacement = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            ItemStack item = new ItemStack(Material.DIAMOND_AXE);
            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(values[i].getName());
            itemMeta.setCustomModelData(values[i].getData());
            item.setItemMeta(itemMeta);
            inventorySpellPlacement.put(i, item);
        }

    }

    public ItemStack getWand(){
        return wand;
    }

    public Player getPlayer() {
        return player;
    }
}
