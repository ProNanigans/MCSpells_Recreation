package me.nanigans.potterworldspells.Utils.Config;

public enum YamlPaths {

    INVENTORY("inventories.PlayerInventory"),
    SPELL_INVENTORY_PATH("inventories.SpellInventory"),
    HOTBARS_PATH("inventories.SpellInventory.Hotbars"),
    PARTICLECOLOR("magic.particlecolor"),
    HOTBAR("Hotbars"),
    SPELLINV("inventory"),
    INVENTORIES_PATH("inventories.SpellInventory.inventory");
    String path;
    YamlPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
