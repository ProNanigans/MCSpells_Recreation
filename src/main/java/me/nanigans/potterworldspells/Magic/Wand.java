package me.nanigans.potterworldspells.Magic;

import me.nanigans.potterworldspells.PotterWorldSpells;
import me.nanigans.potterworldspells.Utils.Config.FilePaths;
import me.nanigans.potterworldspells.Utils.Config.YamlGenerator;
import me.nanigans.potterworldspells.Utils.Config.YamlPaths;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Wand implements Listener {

    private final Player player;
    private final ItemStack wand;
    private byte wandPage = 0;
    private final PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);
    private static Map<UUID, Byte> wandPageSave = new HashMap<>();
    public static Map<UUID, Wand> inWand = new HashMap<>();

    public Wand(Player player){
        this.player = player;
        this.wand = player.getInventory().getItemInMainHand();
        savePlayerInventory();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        inWand.put(player.getUniqueId(), this);

    }


    /**
     * Handles the right click event for when a player wants to close their wand inventory
     * @param event PlayerInteractEvent for when a player right clicks their mouse
     */
    @EventHandler
    public void rightClick(PlayerInteractEvent event) {
        System.out.println(event.getAction());
        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())){
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                closeWand();
            }

        }
    }


    /**
     * Closes the current wand object. First it saves the wand inventory, then it clears the inventory except the wand
     * then it loads the players inventory and then it unregisters this event and removes the player from the players
     * that are in their wand inventory
     */
    public void closeWand(){
        if(inWand.containsKey(player.getUniqueId())){

            saveWandInventory();
            clearAllNotWand();
            loadPlayerInventory();
            HandlerList.unregisterAll(this);
            inWand.remove(player.getUniqueId());

        }
    }

    /**
     * Loads the player inventory. Checks if they're trying to load the wand inventory or their actual inventory
     */
    public void loadInventory(){

        if(inWand.containsKey(player.getUniqueId())) {
            if (ItemUtils.hasNBT(wand, Data.SPELL_INVENTORY.toString() + wandPage, Data.SPELL_INVENTORY.getType())) {

                //plugin.getConfig().getItemStack()

            } else {// we need to create the first inventory
                setUpInventory();
            }
        }else{

        }

    }

    private void loadPlayerInventory(){
        String s = ItemUtils.getNBT(wand, Data.INVENTORY.toString(), Data.INVENTORY.getType()).toString();
        String[] items = s.split(Data.SPELLSEPARATOR.toString());
        //List<ItemStack> itemList = items.stream
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
     * Saves the player inventory to their respective yaml file
     */
    private void savePlayerInventory(){

        ItemStack[] items = player.getInventory().getStorageContents();
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            if(!items[i].equals(wand))
                itemStackMap.put(i, items[i]);
        }

        YamlGenerator yaml = new YamlGenerator(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();
        data.set(YamlPaths.INVENTORY.getPath(), itemStackMap);
        yaml.save();
    }

    /**
     * This will save the current open inventory minus the wand
     */
    private void saveWandInventory(){

        ItemStack[] items = player.getInventory().getStorageContents();
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        for(int i = 0; i < items.length; i++){
            if(!items[i].equals(wand))
                itemStackMap.put(i, items[i]);
        }

        YamlGenerator yaml = new YamlGenerator(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();
        data.set(YamlPaths.SPELL_INVENTORY.getPath(), items);
        yaml.save();
//
//        Inventory inv = ItemUtils.cloneInvContents(player.getInventory());
//        inv.removeItem(player.getInventory().getItemInMainHand());
//        System.out.println(inWandInv(player) + " 1");
//        if(inWandInv(player)){
//            ItemUtils.setData(wand, Data.SPELL_INVENTORY.toString()+wandPage, Data.SPELL_INVENTORY.getType(), Arrays.stream(inv.getContents())
//                    .map(ItemStack::toString).collect(Collectors.joining(Data.SPELLSEPARATOR.toString())));
//            wandPageSave.put(player.getUniqueId(), wandPage);
//        }else{
//            ItemUtils.setData(wand, Data.INVENTORY.toString(), Data.INVENTORY.getType(), Arrays.stream(inv.getContents()).filter(Objects::nonNull)
//                    .map(ItemStack::toString).collect(Collectors.joining(Data.SPELLSEPARATOR.toString())));
//        }
//
//        if(clear){
//            clearAllNotWand();
//        }

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
