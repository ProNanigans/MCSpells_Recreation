package me.nanigans.potterworldspells.Magic;

import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class Wand implements Listener {

    private final Player player;
    private final ItemStack wand;
    private byte wandPage = 0;
    private static Map<UUID, Byte> wandPageSave = new HashMap<>();
    private static Map<UUID, Boolean> inWand = new HashMap<>();

    public Wand(Player player){
        this.player = player;
        this.wand = player.getInventory().getItemInMainHand();

    }

    /**
     * Loads the player inventory. Checks if they're trying to load the wand inventory or their actual inventory
     */
    public void loadInventory(){

        if(ItemUtils.hasNBT(wand, Data.SPELL_INVENTORY.toString(), Data.SPELL_INVENTORY.getType())){


        }else{// we need to create the first inventory

        }

    }

    /**
     * This will setup a wand inventory if it has not been set up before (has been opened before) while avoiding
     * modifying the wand
     */
    private void setUpInventory(){

        final Spells[] values = Spells.values();
        LinkedHashMap<Integer, ItemStack> inventorySpellPlacement = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i++) {
            ItemStack item = new ItemStack(Material.DIAMOND_AXE);
            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(values[i].getName());
            itemMeta.setCustomModelData(values[i].getData());
            item.setItemMeta(itemMeta);
            inventorySpellPlacement.put(i, item);
        }
        inventorySpellPlacement.put(inventorySpellPlacement.size(), inventorySpellPlacement.get(player.getInventory().getHeldItemSlot()));
        inventorySpellPlacement.remove(player.getInventory().getHeldItemSlot());//remove wand

    }

    /**
     * This will save the current open inventory minus the wand
     * @param clear weather to clear the inventory or not
     */
    private void saveInventory(boolean clear){
        Inventory inv = ItemUtils.cloneInvContents(player.getInventory());
        inv.removeItem(player.getInventory().getItemInMainHand());
        if(inWandInv()){
            ItemUtils.setData(wand, Data.SPELL_INVENTORY.toString()+wandPage, PersistentDataType.STRING, Arrays.stream(inv.getContents())
                    .map(ItemStack::toString).collect(Collectors.joining(Data.SPELLSEPARATOR.toString())));
            wandPageSave.put(player.getUniqueId(), wandPage);
        }else{
            ItemUtils.setData(wand, "INVENTORY", PersistentDataType.STRING, Arrays.stream(inv.getContents())
                    .map(ItemStack::toString).collect(Collectors.joining(Data.SPELLSEPARATOR.toString())));
        }

        if(clear){
            clearAllNotWand();
        }

    }

    /**
     * Will go though players inventory and clear everything except the wand
     */
    private void clearAllNotWand(){

        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            if(!ItemUtils.hasNBT(player.getInventory().getItemInMainHand(), Data.ISWAND.toString(), Data.ISWAND.getType())){
                player.getInventory().setItem(i, null);
            }
        }

    }

    /**
     * Checks if the player is in the wand inventory
     * @return if the player is in the wand inventory (true) or not (false)
     */
    public static boolean inWandInv(Player player){
        return inWand.containsKey(player.getUniqueId());
    }

    public ItemStack getWand(){
        return wand;
    }

    public Player getPlayer() {
        return player;
    }
}
