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
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Wand implements Listener {

    private final Player player;
    private ItemStack wand;
    private int wandPage = 1;
    private int hotbarPage = 1;
    private final PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);
    public static Map<UUID, Wand> inWand = new HashMap<>();
    private Map<String, BukkitTask> activeSpells = new HashMap<>();
    private double mana = 100;
    private ItemStack lastSpell;
    public final static short maxHotBarPages = 2;
    public final static int maxInventoryPages = 3;
    private boolean canCastSpells = true;
    private final Map<String, Integer[]> spellLoc = new HashMap<String, Integer[]>(){{
        put(Data.HOTBARNUM.toString(), new Integer[2]);
        put(Data.PAGENUM.toString(), new Integer[2]);
    }};


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
        if(event.getWhoClicked().getUniqueId().equals(player.getUniqueId())) {
            swapInventory(event);
            spellRightClicked(event);
            dropInventorySpell(event);
            moveItem(event);
        }

    }

    /**
     * When a player drops a spell in their inventory, we cast it
     * @param event InventoryClickEvent because for some reason, an inventory drop is a click
     */
    private void dropInventorySpell(InventoryClickEvent event){

        if(event.getAction() == InventoryAction.DROP_ONE_SLOT) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (ItemUtils.hasNBT(item, Data.SPELLNAME.toString(), Data.SPELLTYPE.getType())) {
                final Wand that = this;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        lastSpell = player.getInventory().getItem(player.getInventory().first(item));
                        if (!ItemUtils.hasNBT(lastSpell, Data.COOLDOWN.toString(), Data.COOLDOWN.getType())) {
                            String spell = ItemUtils.getNBT(item, Data.SPELLNAME.toString(), Data.SPELLNAME.getType()).toString().replace(" ", "");
                            String spellType = ItemUtils.getNBT(item, Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType()).toString();
                            Class<?> aClass = null;
                            try {
                                aClass = Class.forName("me.nanigans.potterworldspells.Magic.Spells." + spellType + "." + spell);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            try {
                                aClass.getConstructor(Wand.class).newInstance(that);
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }else{
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        }
                    }
                }.runTaskLater(plugin, 0);
            }
        }

    }

    /**
     * Handles when the player moves a spell around. We need to update the cooldown to the new slot the player placed the spell at
     * @param event InventoryClickEvent
     */
    private void moveItem(InventoryClickEvent event){

        if(event.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
            if (event.getAction() == InventoryAction.PLACE_ALL) {
                System.out.println(event.getCursor() + " " + event.getSlot());
                final Wand that = this;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Spell.reloadCooldown(player.getInventory().getItem(event.getSlot()), that, plugin, event.getSlot());
                    }
                }.runTaskLater(plugin, 0);
            } else if (event.getAction() == InventoryAction.PICKUP_ALL) {
                if (event.getCurrentItem() != null)
                    Spell.removeCooldown(this, event.getCurrentItem().getItemMeta().getDisplayName());
            } else if (event.getAction() != InventoryAction.DROP_ONE_SLOT) event.setCancelled(true);
        }//curren item is the new thing being held, cursor is the old thing being held
        else{
            Spell.removeCooldown(this, event.getCurrentItem().getItemMeta().getDisplayName());//remove cooldown for item being picked up
            final Wand that = this;
            new BukkitRunnable() {//reload cooldown for item being placed
                @Override
                public void run() {
                    Spell.reloadCooldown(player.getInventory().getItem(event.getSlot()), that, plugin, event.getSlot());
                }
            }.runTaskLater(plugin, 0);
        }
    }

    /**
     * When a player right clicks a spell, we will switch the current spell to that spell
     * @param event InventoryClickEvent
     */
    private void spellRightClicked(InventoryClickEvent event){
        if(event.getClick().isRightClick()) {
            Player clicked = (Player) event.getWhoClicked();
            if (clicked.getUniqueId().equals(this.player.getUniqueId())) {
                if (event.getCurrentItem() != null && !event.getCurrentItem().equals(this.wand)) {
                    final int position = event.getSlot();
                    Data loc = Data.PAGENUM;
                    if (position < 9) loc = Data.HOTBARNUM;
                    changeSpell(event.getCurrentItem(), position, loc);
                    player.closeInventory();

                }
            }
        }
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
                player.playSound(player.getEyeLocation(), "magic.paperturn", 1, 1);
                clearInventory();
                loadSpellInventory(new File(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml"));

            }

        }

    }

    /**
     * Handles inventory drops.
     * When player drops wand, we switch the current hotbar.
     * If player drops spell from inventory, we cast the spell.
     * All actions are cancelled if the player is in the spell inventory.
     * @param event PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemDrop(PlayerDropItemEvent event) {
        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())) {
            event.setCancelled(true);
            if (event.getItemDrop().getItemStack().getType() == this.wand.getType()) {
                canCastSpells = false;
                player.playSound(player.getEyeLocation(), "magic.paperturn", 1, 1);
                swapHotbar(ClickType.RIGHT);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        canCastSpells = true;
                    }
                }.runTaskLater(plugin, 0);
            }
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
                    if(!ItemUtils.hasNBT(lastSpell, Data.COOLDOWN.toString(), Data.COOLDOWN.getType())) {
                        String spell = ItemUtils.getNBT(wand, Data.SPELLNAME.toString(), Data.SPELLNAME.getType()).toString().replace(" ", "_");
                        String spellType = ItemUtils.getNBT(wand, Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType()).toString();
                        final Class<?> aClass = Class.forName("me.nanigans.potterworldspells.Magic.Spells." + spellType + "." + spell);
                        aClass.getConstructor(Wand.class).newInstance(this);

                    }else{
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    }
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

                    changeSpell(itemSwappedTo, event.getNewSlot(), Data.HOTBARNUM);

                }
            }

        }
    }

    /**
     * Changes the current spell bound to the wand in order for that spell to be casted
     * @param itemSwappedTo the spell itemstack that was swapped to
     * @param slotSwapped the location in the inventory where that was
     * @param inventoryPosition weather or not the spell is in the inventory or the hotbar.
     * @requires inventoryPosition to be Data.HOTBARNUM or DATA.PAGENUM
     */
    private void changeSpell(ItemStack itemSwappedTo, int slotSwapped, Data inventoryPosition){
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
        final Integer[] integers = spellLoc.get(Data.HOTBARNUM.toString());
        if(inventoryPosition == Data.HOTBARNUM)
            integers[0] = this.hotbarPage;
        else if(inventoryPosition == Data.PAGENUM)
            integers[0] = this.wandPage;
        integers[1] = slotSwapped;
        spellLoc.put(inventoryPosition.toString(), integers);
        final ItemStack wandStack = ItemUtils.setData(wand, Data.SPELLNAME.toString(), Data.SPELLNAME.getType(), spellName.replace(" ", "_"));
        if(wandStack != null && wandStack.getType() != Material.AIR)
            player.getInventory().setItemInMainHand(wandStack);
        player.updateInventory();
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
                this.activeSpells.forEach((i, j) -> j.cancel());
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
            player.playSound(player.getLocation(), "magic.wandup", 1, 1);
            if (file.exists()) {
                ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);

                loadPlayerSpells(file);

            } else {
                ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);
                setUpInventory();
            }
        }else{

            if(file.exists()){
                player.playSound(player.getLocation(), "magic.wanddown", 1, 1);
                ItemUtils.saveInventory(player, FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml", YamlPaths.INVENTORY.getPath(), wand);
                this.activeSpells.forEach((i, j) -> j.cancel());
                this.activeSpells.clear();
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
        yaml.reloadData();
        final FileConfiguration data = yaml.getData();
        Map<String, Object> spells =
                YamlGenerator.getConfigSectionValue(data.get(YamlPaths.INVENTORIES.getPath()+"."+wandPage), true);
        spells = spells == null ? new HashMap<>() : spells;

        if (spells.size() > 0) {
            spells.forEach((i, j) -> {
                int pos = Integer.parseInt(i);
                ItemStack item = ((ItemStack) j).clone();
                Spell.reloadCooldown(item, this, plugin, pos);
                player.getInventory().setItem(pos, item);
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
           AtomicBoolean add = new AtomicBoolean(false);

            hotbar.forEach((i, j) -> {
                int pos = Integer.parseInt(i);
                if(add.get()) pos++;
                if(pos < 9){
                    int handPos = player.getInventory().getHeldItemSlot();
                    if(pos == handPos) {
                        pos = player.getInventory().firstEmpty();
                        add.set(true);
                    }
                }
                ItemStack item = ((ItemStack) j);
                player.getInventory().setItem(pos, item);
                Spell.reloadCooldown(item, this, plugin, pos);
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
        Map<String, Map<String, Map<Integer, ItemStack>>> wandInventory = new HashMap<>();
        Map<String, Map<Integer, ItemStack>> inventorySpellPlacement = new HashMap<>();
        Map<Integer, ItemStack> inventorySpells = new HashMap<>();
        Map<String, Map<Integer, ItemStack>> hotbarSpellsPlacement = new HashMap<>();
        Map<Integer, ItemStack> hotbarSpells = new HashMap<>();
        File playerFile = new File(FilePaths.USERS+"/"+player.getUniqueId()+".yml");

        int invNum = 1, hotbarNum = 1, hbIndx = player.getInventory().firstEmpty();

        for (Spells value : values) {
            System.out.println("value = " + value);

            if (hbIndx > player.getInventory().getStorageContents().length) {
                inventorySpellPlacement.put(String.valueOf(invNum), inventorySpells);
                hotbarSpellsPlacement.put(String.valueOf(hotbarNum), hotbarSpells);
                hotbarSpells.clear();
                inventorySpells.clear();
                hbIndx = 0;
                invNum++;
                hotbarNum++;
            }

            ItemStack item = new ItemStack(Material.DIAMOND_AXE);
            final ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(value.getName());
            itemMeta.setCustomModelData(value.getData());
            item.setItemMeta(itemMeta);
            System.out.println("item = " + item);
            ItemUtils.setData(ItemUtils.setData(item, Data.SPELLNAME.toString(), Data.SPELLNAME.getType(), value.getName()),
                    Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType(), value.getSpellType());

            if (hbIndx < 9) {
                hotbarSpells.put(hbIndx, item);
            } else inventorySpells.put(hbIndx, item);

            hbIndx++;

        }
        inventorySpellPlacement.put(String.valueOf(invNum), inventorySpells);
        hotbarSpellsPlacement.put(String.valueOf(hotbarNum), hotbarSpells);
        System.out.println("hotbarSpellsPlacement = " + hotbarSpellsPlacement);
        wandInventory.put("Hotbars", hotbarSpellsPlacement);
        wandInventory.put("inventory", inventorySpellPlacement);

        YamlGenerator yaml = new YamlGenerator(playerFile.getAbsolutePath());
        final FileConfiguration data = yaml.getData();

        wandInventory.keySet().forEach(i -> wandInventory.get(i).keySet().forEach(j ->
                wandInventory.get(i).get(j).keySet().forEach(k ->
                        data.set(YamlPaths.SPELL_INVENTORY.getPath() + "." + i + "." + j + "." + k, wandInventory.get(i).get(j).get(k)))));

        Random rand = new Random();
        java.awt.Color color = new java.awt.Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        data.set(YamlPaths.PARTICLECOLOR.getPath(), color.getRGB());
        yaml.save();

        System.out.println(getWandColor());

        loadPlayerSpells(playerFile);

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

    /**
     * Saves the current hotbar the player is on. Will not save the wand
     */
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

    public Color getWandColor(){
        YamlGenerator yaml = new YamlGenerator(FilePaths.USERS.getPath()+"/"+player.getUniqueId()+".yml");
        final FileConfiguration data = yaml.getData();
        final int rgbCol = data.getInt(YamlPaths.PARTICLECOLOR.getPath());
        return Color.fromRGB(rgbCol);

    }

    public Map<String, Integer[]> getSpellLoc() {
        return spellLoc;
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

    public Map<String, BukkitTask> getActiveSpells() {
        return activeSpells;
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
