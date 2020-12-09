package me.nanigans.potterworldspells.Utils;

import org.bukkit.persistence.PersistentDataType;

public enum Data {

    ISWAND("IsWand", PersistentDataType.BYTE),
    PAGENUM("Page", PersistentDataType.INTEGER),
    HOTBARNUM("Hotbar_Num", PersistentDataType.SHORT),
    SPELLTYPE("SpellType", PersistentDataType.STRING),
    SPELLNAME("SpellName", PersistentDataType.STRING);

    String name;
    String spellType;
    PersistentDataType type;

    <T, Z> Data(String name, PersistentDataType<T, Z> type) {
        this.name = name;
        this.type = type;
        this.spellType = spellType;
    }

    public String toString() {
        return this.name;
    }

    public PersistentDataType getType(){
        return this.type;
    }
}
