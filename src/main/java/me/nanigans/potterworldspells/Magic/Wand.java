package me.nanigans.potterworldspells.Magic;

import me.nanigans.potterworldspells.Magic.SpellsTypes.Spell;
import me.nanigans.potterworldspells.PotterWorldSpells;
import me.nanigans.potterworldspells.Utils.Config.FilePaths;
import me.nanigans.potterworldspells.Utils.Config.YamlGenerator;
import me.nanigans.potterworldspells.Utils.Config.YamlPaths;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import me.nanigans.potterworldspells.Utils.Spells;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Wand implements Listener {

    private final Player player;
    private ItemStack wand;
    private int wandPage = 1;
    private int hotbarPage = 1;
    private final PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);
    public static Map<UUID, Wand> inWand = new HashMap<>();
    private List<Spell> activeSpells = new ArrayList<>();
    private double mana = 100;
    private ItemStack lastSpell;
    public final static short maxHotBarPages = 2;
    public final static int maxInventoryPages = 3;
    private boolean canCastSpells = true;


    public Wand(Player player){
        this.player = player;
        this.wand = player.getInventory().getItemInMainHand().clone();
        inWand.put(player.getUniqueId(), this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        if(ItemUtils.hasNBT(wand, Data.PAGENUM.toString(), Data.PAGENUM.getType())){
            this.wandPage = (int) ItemUtils.getNBT(wand, Data.PAGENUM.toString(), Data.PAGENUM.getType());
        }
        if(ItemUtils.hasNBT(wand, Data.HOTBARNUM.toString(), Data.HOTBARNUM.getType())){
            this.hotbarPage = (int) ItemUtils.getNBT(wand, Data.HOTBARNUM.toString(), Data.HOTBARNUM.getType());
        }

    }


    /**
     * Event for swapping inventory
     * @param event InventoryCLickEvent
     */
    @EventHandler
    public void swapInventories(InventoryClickEvent event){

        swapInventory(event);

    }

    /**
     * Handles wand inventory clicks
     * @param event InventoryClickEvent
     */
    @EventHandler
    private void wandClick(InventoryClickEvent event){

        swapInventory(event);
        Player clicked = ((Player) event.getWhoClicked());
        if(clicked.getUniqueId().equals(this.player.getUniqueId())){

            if(event.getCurrentItem() != null && event.getCurrentItem().equals(this.wand)){
                swapHotbar(event.getClick());
                event.setCancelled(true);

            }

        }

    }

    /**
     * WHen the player clicks outside the inventory, we swap their inventory to the next page
     * @param event InventoryClickEvent
     */
    private void swapInventory(InventoryClickEvent event){

        Player clicked = ((Player) event.getWhoClicked());
        if(clicked.getUniqueId().equals(this.player.getUniqueId()) && inWandInv(clicked)){

            if(event.getClickedInventory() == null){
                saveWandInventory();
                ClickType click = event.getClick();
                if(click.isLeftClick()){
                    if(this.wandPage+1 > maxInventoryPages)
                        this.wandPage = 1;
                    else this.wandPage++;
                }else if(click.isRightClick()){
                    if(this.wandPage - 1 == 0)
                        this.wandPage = maxInventoryPages;
                    else this.wandPage--;
                }
                clearInventory();
                loadSpellInventory(new File(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml"));

            }

        }

    }

    /**
     * Handles inventory drops
     * @param event PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemDrop(PlayerDropItemEvent event){

        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())){
            canCastSpells = false;
            event.setCancelled(true);
            swapHotbar(ClickType.RIGHT);
            new BukkitRunnable() {
                @Override
                public void run() {
                    canCastSpells = true;
                }
            }.runTaskLater(plugin, 0);
        }

    }

    /**
     * When the player wants to swap hotbars, it'll swap them to the next one
     */
    public void swapHotbar(ClickType type){

        if(inWandInv(player)) {
            saveWandHotbar();
            if(type.isLeftClick()) {
                if (hotbarPage + 1 > maxHotBarPages)
                    hotbarPage = 1;
                else hotbarPage++;
            }else if(type.isRightClick()){
                if(hotbarPage - 1 == 0)
                    hotbarPage = maxHotBarPages;
                else hotbarPage--;
            }
            clearHotBar();
            loadHotbar(new File(FilePaths.USERS.getPath() + "/" + player.getUniqueId() + ".yml"));

        }

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
     * When a player wants to cast a spell, this method will call the spell class and invoke its constructor
     * @param event PlayerInteractEvent for when a player left clicks
     * @throws ClassNotFoundException if the spell is unknown and is not registered as a created class
     * @throws NoSuchMethodException when the constructor isn't found
     * @throws IllegalAccessException when we cant access the constructor
     * @throws InvocationTargetException error
     * @throws InstantiationException error
     */
    @EventHandler(priority = EventPriority.LOW)
    public void leftClick(PlayerInteractEvent event) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId()) && canCastSpells){
            if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR){
                if(ItemUtils.hasNBT(wand, Data.SPELLNAME.toString(), Data.SPELLNAME.getType())) {

                    String spell = ItemUtils.getNBT(wand, Data.SPELLNAME.toString(), Data.SPELLNAME.getType()).toString().replace(" ", "");
                    String spellType = ItemUtils.getNBT(wand, Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType()).toString();
                    final Class<?> aClass = Class.forName("me.nanigans.potterworldspells.Magic.Spells."+spellType+"."+spell);
                    aClass.getConstructor(Wand.class).newInstance(this);

                }

            }

        }
    }

    /**
     * For when a player swaps to a different item in their hotbar, we tell the wand that its current primed
     * spell is that spell that was just switched to. Then the event cancels
     * @param event PlayerItemHeldEvent for when a player swaps hotbar slots
     */
    @EventHandler
    public void swapSpellInHotBar(PlayerItemHeldEvent event){
        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())) {
            event.setCancelled(true);
            ItemStack itemSwappedTo = player.getInventory().getItem(event.getNewSlot());
            if (itemSwappedTo != null && !itemSwappedTo.equals(this.wand)) {

                if (ItemUtils.hasNBT(itemSwappedTo, Data.SPELLNAME.toString(), Data.SPELLNAME.getType())) {

                    String spellName = ItemUtils.getNBT(itemSwappedTo, Data.SPELLNAME.toString(), Data.SPELLNAME.getType()).toString();
                    final ItemMeta meta = wand.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + spellName + " " + ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + "Wand" +
                            ChatColor.DARK_GRAY + ")");
                    wand.setItemMeta(meta);
                    if (ItemUtils.hasNBT(itemSwappedTo, Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType())) {
                        ItemUtils.setData(wand, Data.SPELLTYPE.toString(), Data.SPELLNAME.getType(),
                                ItemUtils.getNBT(itemSwappedTo, Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType()));
                    }
                    lastSpell = itemSwappedTo;
                    final ItemStack wandStack = ItemUtils.setData(wand, Data.SPELLNAME.toString(), Data.SPELLNAME.getType(), spellName.replace(" ", "_"));
                    if(wandStack != null && wandStack.getType() != Material.AIR)
                    player.getInventory().setItemInMainHand(wandStack);
                    player.updateInventory();

                }
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
                ItemUtils.setData(wand, Data.PAGENUM.toString(), Data.PAGENUM.getType(), this.wandPage);
                player.getInventory().setItemInMainHand(ItemUtils.setData(wand, Data.HOTBARNUM.toString(), Data.HOTBARNUM.getType(), this.hotbarPage));
                saveWandInventory();
                saveWandHotbar();
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
                ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);

                loadPlayerSpells(file);

            } else {
                ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);
                setUpInventory();
            }
        }else{

            if(file.exists()){
                ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);
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
        clearAllNotWand();
        loadSpellInventory(fromFile);
        loadHotbar(fromFile);
    }

    /**
     * Loads a players inventory from the current page
     * @param fromFile the yaml file to the inventory from
     * @requires fromFile to exist and to be a yaml file
     */
    private void loadSpellInventory(File fromFile) {
        YamlGenerator yaml = new YamlGenerator(fromFile.getAbsolutePath());
        final FileConfiguration data = yaml.getData();
        Map<String, Object> spells =
                YamlGenerator.getConfigSectionValue(data.get(YamlPaths.INVENTORIES.getPath()+"."+wandPage), true);
        spells = spells == null ? new HashMap<>() : spells;

        if (spells.size() > 0) {
            spells.forEach((i, j) -> {
                int pos = Integer.parseInt(i);
                player.getInventory().setItem(pos, (ItemStack) j);
            });

        }
    }

    /**
     * Loads a players hotbar from the current page
     * @param fromFile the yaml file to get the hotbar from
     * @requires fromFile to exist and to be a yaml file
     */
    private void loadHotbar(File fromFile){

        YamlGenerator yaml = new YamlGenerator(fromFile.getAbsolutePath());
        final FileConfiguration data = yaml.getData();

        Map<String, Object> hotbar = YamlGenerator.getConfigSectionValue(data.get(YamlPaths.HOTBARS.getPath()+"."+hotbarPage), true);
        hotbar = hotbar == null ? new HashMap<>() : hotbar;
        if(hotbar.size() > 0){
            hotbar.forEach((i, j) -> {
                int pos = Integer.parseInt(i);
                if(pos < 9){
                    int handPos = player.getInventory().getHeldItemSlot();
                    if(pos == handPos)
                        pos = player.getInventory().firstEmpty();
                }
                player.getInventory().setItem(pos, (ItemStack) j);
            });
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
        if(playerItems != null && playerItems.size() > 0){
            playerItems.forEach((i, j) -> player.getInventory().setItem(Integer.parseInt(i), (ItemStack) j));
        }

    }

    /**
     * This will setup a wand inventory if it has not been set up before (has been opened before) while avoiding
     * modifying the wand
     */
    private void setUpInventory(){

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
            ItemUtils.setData(ItemUtils.setData(item, Data.SPELLNAME.toString(), Data.SPELLNAME.getType(), values[i].getName()),
                    Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType(), values[i].getSpellType());

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
        for(int i = 9; i < items.length; i++){
            if(items[i] != null && !items[i].equals(wand)) {
                itemStackMap.put(i, items[i]);
            }
        }

        YamlGenerator yaml = new YamlGenerator(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();
        data.set(YamlPaths.INVENTORIES.getPath()+"."+wandPage, itemStackMap);

        yaml.save();
    }

    private void saveWandHotbar(){

        ItemStack[] items = player.getInventory().getStorageContents();
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        for(int i = 0; i < 9; i++){
            if(items[i] != null && !items[i].equals(wand)){
                itemStackMap.put(i, items[i]);
            }
        }
        YamlGenerator yaml = new YamlGenerator(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();
        data.set(YamlPaths.HOTBARS.getPath()+"."+hotbarPage, itemStackMap);
        yaml.save();

    }

    /**
     * Will go though players inventory and clear everything except the wand
     */
    private void clearAllNotWand(){

        final ItemStack[] storageContents = player.getInventory().getStorageContents();
        for (ItemStack storageContent : storageContents) {
            if (storageContent != null && !storageContent.isSimilar(this.wand)) {
                if (!storageContent.isSimilar(player.getInventory().getItemInMainHand()))
                    player.getInventory().removeItem(storageContent);
            }
        }
    }

    /**
     * Will clear the players hotbar except for the wand
     */
    private void clearHotBar(){
        for (int i = 0; i < 9; i++) {
            final ItemStack item = player.getInventory().getStorageContents()[i];
            if(item != null && !item.isSimilar(this.wand))
                player.getInventory().removeItem(item);
        }
    }

    /**
     * will clear the players inventory except for the wand although the wand can't be inside the inventory when open
     */
    private void clearInventory(){
        for(int i = 9; i < player.getInventory().getStorageContents().length; i++){
            final ItemStack item = player.getInventory().getStorageContents()[i];
            if(item != null && !item.isSimilar(this.wand)){
                player.getInventory().removeItem(item);
            }
        }
    }

    public ItemStack getLastSpell() {
        return lastSpell;
    }

    public PotterWorldSpells getPlugin() {
        return plugin;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
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
