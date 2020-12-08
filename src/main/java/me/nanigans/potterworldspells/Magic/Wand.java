package me.nanigans.potterworldspells.Magic;

import me.nanigans.potterworldspells.PotterWorldSpells;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
    private final PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);
    private static Map<UUID, Byte> wandPageSave = new HashMap<>();
    private static Map<UUID, Boolean> inWand = new HashMap<>();

    public Wand(Player player){
        this.player = player;
        this.wand = player.getInventory().getItemInMainHand();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }


    @EventHandler
    public void closeInventory(PlayerInteractEvent event) throws Throwable {
        System.out.println(event.getAction());
        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())){
            if(event.getAction().toString().toLowerCase().contains("right")) {
                inWand.remove(event.getPlayer().getUniqueId());
                saveInventory(true);
                loadPlayerInventory();
                HandlerList.unregisterAll(this);
            }

        }
    }

    /**
     * Loads the player inventory. Checks if they're trying to load the wand inventory or their actual inventory
     */
    public void loadInventory(){

        inWand.put(player.getUniqueId(), true);
        if(ItemUtils.hasNBT(wand, Data.SPELL_INVENTORY.toString()+wandPage, Data.SPELL_INVENTORY.getType())){

            plugin.getConfig().getItemStack()

        }else{// we need to create the first inventory
            setUpInventory();
        }

    }

    private void loadPlayerInventory(){
        String s = ItemUtils.getNBT(wand, Data.INVENTORY.toString(), Data.INVENTORY.getType()).toString();
        String[] items = s.split(Data.SPELLSEPARATOR.toString());
        List<ItemStack> itemList = items.stream
    }

    /**
     * This will setup a wand inventory if it has not been set up before (has been opened before) while avoiding
     * modifying the wand
     */
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
        inventorySpellPlacement.put(inventorySpellPlacement.size(), inventorySpellPlacement.get(player.getInventory().getHeldItemSlot()));
        inventorySpellPlacement.replace(player.getInventory().getHeldItemSlot(), player.getInventory().getItemInMainHand());//remove wand
        player.getInventory().clear();
        inventorySpellPlacement.forEach((i, j) -> player.getInventory().setItem(i, j));

    }

    /**
     * This will save the current open inventory minus the wand
     * @param clear weather to clear the inventory or not
     */
    private void saveInventory(boolean clear){
        Inventory inv = ItemUtils.cloneInvContents(player.getInventory());
        inv.removeItem(player.getInventory().getItemInMainHand());
        System.out.println(inWandInv(player) + " 1");
        if(inWandInv(player)){
            ItemUtils.setData(wand, Data.SPELL_INVENTORY.toString()+wandPage, Data.SPELL_INVENTORY.getType(), Arrays.stream(inv.getContents())
                    .map(ItemStack::toString).collect(Collectors.joining(Data.SPELLSEPARATOR.toString())));
            wandPageSave.put(player.getUniqueId(), wandPage);
        }else{
            ItemUtils.setData(wand, Data.INVENTORY.toString(), Data.INVENTORY.getType(), Arrays.stream(inv.getContents()).filter(Objects::nonNull)
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
