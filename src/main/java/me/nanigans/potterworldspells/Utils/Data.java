package me.nanigans.potterworldspells.Utils;

import org.bukkit.persistence.PersistentDataType;

public enum Data {

    ISWAND("IsWand", PersistentDataType.BYTE),
    INVENTORY("Inventory", PersistentDataType.TAG_CONTAINER_ARRAY);

    String name;
    PersistentDataType type;

    <T, Z> Data(String name, PersistentDataType<T, Z> type) {
        this.name = name;
        this.type = type;
    }

    public String toString() {
        return this.name;
    }

    public PersistentDataType getType(){
        return this.type;
    }
}
