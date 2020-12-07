package me.nanigans.potterworldspells.Utils;

import me.nanigans.potterworldspells.PotterWorldSpells;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemUtils {
    private static PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);

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
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, key), type);
    }

}
