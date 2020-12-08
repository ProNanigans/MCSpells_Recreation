package me.nanigans.potterworldspells.Utils;

import org.bukkit.persistence.PersistentDataType;

public enum Data {

    INVENTORY("Inventory", PersistentDataType.STRING),
    SPELLSEPARATOR(",,", null),
    ISWAND("IsWand", PersistentDataType.BYTE),
    SPELL_INVENTORY("Spell_Inventory", PersistentDataType.STRING);


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
