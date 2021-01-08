package me.nanigans.potterworldspells.Utils;

import me.nanigans.potterworldspells.PotterWorldSpells;
import me.nanigans.potterworldspells.Utils.Config.YamlGenerator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemUtils {
    private static final PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);

    public static <T, Z> ItemStack setData(ItemStack itemStack, String key, PersistentDataType<T, Z> type, Z value){
        NamespacedKey keyy = new NamespacedKey(plugin, key);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(keyy, type, value);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static <T, Z> Z getNBT(ItemStack item, String key, PersistentDataType<T, Z> type) {
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, key), type);
    }

    public static <T, Z> boolean hasNBT(ItemStack item, String key, PersistentDataType<T, Z> type){
        if(item != null) {
            if (item.getItemMeta() == null) return false;
            return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, key), type);
        }else return false;
    }


    public static <T, Z> void removeNBT(ItemStack item, String key, PersistentDataType<T,Z> type){
        if(item.getItemMeta() == null) return;
        if(hasNBT(item, key, type)){
            ItemMeta meta = item.getItemMeta().clone();
            meta.getPersistentDataContainer().remove(new NamespacedKey(plugin, key));
            item.setItemMeta(meta);

        }
    }

    public static Inventory cloneInvContents(Inventory toClone){
        Inventory inv = Bukkit.createInventory(toClone.getHolder(), toClone.getType());
        inv.setContents(toClone.getContents());
        return inv;
    }

    public static void saveInventory(Player player, String path, String yamlPath, ItemStack... ignored){

        YamlGenerator yaml = new YamlGenerator(path);
        Map<Integer, ItemStack> saveMap = new HashMap<>();
        final ItemStack[] storageContents = player.getInventory().getStorageContents();
        for (int i = 0; i < storageContents.length; i++) {
            final int finalI = i;
            if(storageContents[i] != null && Arrays.stream(ignored).noneMatch(j -> j.equals(storageContents[finalI]))){
                saveMap.put(i, storageContents[i]);
            }
        }
        yaml.getData().set(yamlPath, saveMap);
        yaml.save();

    }

    public static Map<String, Object> deserializeString(String value){
        value = value.substring(1, value.length()-1);
        String[] keyValuePairs = value.split(",");
        Map<String, Object> map = new HashMap<>();

        for(String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            map.put(entry[0].trim(), entry[1].trim());
        }
        return map;
    }

}
