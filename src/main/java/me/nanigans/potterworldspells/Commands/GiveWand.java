package me.nanigans.potterworldspells.Commands;

import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GiveWand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("wand")){

            if(sender instanceof Player){

                Player player = ((Player) sender);
                ItemStack wand = new ItemStack(Material.DIAMOND_HOE);
                ItemMeta meta = wand.getItemMeta();
                meta.setCustomModelData(34);//item key type value
                meta.setDisplayName(ChatColor.AQUA+"Wand");
                ItemUtils.setData(wand, Data.ISWAND.toString(), Data.ISWAND.getType(), (byte)0);

                player.getInventory().addItem(wand);
                player.sendMessage(ChatColor.GREEN+"Wand given!");
                return true;

            }

        }
        return false;
    }
}
