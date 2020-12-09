package me.nanigans.potterworldspells.Utils.Config;

public enum YamlPaths {

    INVENTORY("inventories.PlayerInventory"),
    SPELL_INVENTORY("inventories.SpellInventory"),
    HOTBARS("inventories.SpellInventory.Hotbars"),
    INVENTORIES("inventories.SpellInventory.inventory");
    String path;
    YamlPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
