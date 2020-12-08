package me.nanigans.potterworldspells.Events;

import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class WandClickEvents implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){

            Player player = event.getPlayer();
            if(player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE && !Wand.inWandInv(player)){
                ItemStack item = player.getInventory().getItemInMainHand();
                if (ItemUtils.hasNBT(item, Data.ISWAND.toString(), PersistentDataType.BYTE)) {
                    new Wand(player).loadInventory();
                }
            }

        }

    }

}
