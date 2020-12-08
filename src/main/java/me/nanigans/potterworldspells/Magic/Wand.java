package me.nanigans.potterworldspells.Magic;

import me.nanigans.potterworldspells.Magic.SpellsTypes.Spell;
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

import java.io.File;
import java.util.*;

public class Wand implements Listener {

    private final Player player;
    private ItemStack wand;
    private int wandPage = 1;
    private final PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);
    public static Map<UUID, Wand> inWand = new HashMap<>();
    private List<Spell> activeSpells = new ArrayList<>();

    public Wand(Player player){
        this.player = player;
        this.wand = player.getInventory().getItemInMainHand();
        inWand.put(player.getUniqueId(), this);
        ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }


    /**
     * Handles the right click event for when a player wants to close their wand inventory
     * @param event PlayerInteractEvent for when a player right clicks their mouse
     */
    @EventHandler
    public void rightClick(PlayerInteractEvent event) {
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

            try {
                player.getInventory().setItemInMainHand(ItemUtils.setData(wand, Data.PAGENUM.toString(), Data.PAGENUM.getType(), this.wandPage));
                saveWandInventory();
                inWand.remove(player.getUniqueId());
                clearAllNotWand();
                loadInventory();
                HandlerList.unregisterAll(this);
            }catch(AssertionError err){
                err.printStackTrace();
            }

        }
    }

    /**
     * Loads the player inventory. Checks if they're trying to load the wand inventory or their actual inventory
     */
    public void loadInventory(){

        File file = new File(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml");
        if(inWand.containsKey(player.getUniqueId())) {
            if (file.exists()) {

                loadPlayerSpells(file);

            } else {// we need to create the first inventory
                setUpInventory();
            }
        }else{

            if(file.exists()){

                loadPlayerItems(file);

            }

        }

    }

    /**
     * Loads the players spell inventory into their inventory and replaces all current items with nothing
     * @param fromFile the file to get the inventory from
     * @requires fromFile to exist
     */

    private void loadPlayerSpells(File fromFile){

        YamlGenerator yaml = new YamlGenerator(fromFile.getAbsolutePath());
        final FileConfiguration data = yaml.getData();

        final Map<String, Object> spells =
                YamlGenerator.getConfigSectionValue(data.get(YamlPaths.SPELL_INVENTORY.getPath()+"."+wandPage), true);
        if(spells != null) {
            clearAllNotWand();

            spells.forEach((i, j) ->{
                int pos = Integer.parseInt(i);
                if(pos < 9){
                    int handPos = player.getInventory().getHeldItemSlot();
                    if(pos == handPos)
                        pos = player.getInventory().firstEmpty();
                }
                player.getInventory().setItem(pos, (ItemStack) j);
            });
        }else{
            setUpInventory();
        }

    }

    /**
     * Loads the players original inventory to their inventory and replaces all spells with nothing
     * @param fromFile the file to get the inventory from
     * @requires fromFile to exist
     */
    private void loadPlayerItems(File fromFile){

        YamlGenerator yaml = new YamlGenerator(fromFile.getAbsolutePath());
        final FileConfiguration data = yaml.getData();
        final Map<String, Object> playerItems = YamlGenerator.getConfigSectionValue(data
                .get(YamlPaths.INVENTORY.getPath()), true);
        clearAllNotWand();
        if(playerItems != null){
            playerItems.forEach((i, j) -> player.getInventory().setItem(Integer.parseInt(i), (ItemStack) j));
        }

    }

    /**
     * This will setup a wand inventory if it has not been set up before (has been opened before) while avoiding
     * modifying the wand
     */
    private void setUpInventory(){

        ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);

        final Spells[] values = Spells.values();
        Map<String, Map<Integer, ItemStack>> inventorySpellPlacement = new HashMap<>();
        Map<Integer, ItemStack> spellPlacement = new HashMap<>();

        int invNum = wandPage;
        for (int i = 0; i < values.length; i++) {
            if(i >= player.getInventory().getStorageContents().length){
                inventorySpellPlacement.put(String.valueOf(invNum), spellPlacement);
                invNum++;
            }
            ItemStack item = new ItemStack(Material.DIAMOND_AXE);
            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(values[i].getName());
            itemMeta.setCustomModelData(values[i].getData());
            item.setItemMeta(itemMeta);
            spellPlacement.put(i, item);
        }
        inventorySpellPlacement.put(String.valueOf(invNum), spellPlacement);
        clearAllNotWand();
        inventorySpellPlacement.get(String.valueOf(wandPage)).forEach((i, j) -> player.getInventory().addItem(j));

    }

    /**
     * This will save the current open inventory minus the wand
     */
    private void saveWandInventory(){

        ItemStack[] items = player.getInventory().getStorageContents();
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        for(int i = 0; i < items.length; i++){
            if(items[i] != null && !items[i].equals(wand))
                itemStackMap.put(i, items[i]);
        }

        YamlGenerator yaml = new YamlGenerator(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();
        data.set(YamlPaths.SPELL_INVENTORY.getPath()+"."+wandPage, itemStackMap);

        yaml.save();
    }

    /**
     * Will go though players inventory and clear everything except the wand
     */
    private void clearAllNotWand(){

        for(ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null) {
                if (!item.isSimilar(this.wand)) {
                    player.getInventory().removeItem(item);
                }
            }
        }

    }

    public List<Spell> getActiveSpells() {
        return activeSpells;
    }

    public void setActiveSpells(List<Spell> activeSpells) {
        this.activeSpells = activeSpells;
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
