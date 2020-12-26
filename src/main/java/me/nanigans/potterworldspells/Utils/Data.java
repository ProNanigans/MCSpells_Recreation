package me.nanigans.potterworldspells.Utils;

import org.bukkit.persistence.PersistentDataType;

public enum Data {

    COOLDOWN("Cooldown", PersistentDataType.LONG),
    LASTSPELL("Last_Spell", PersistentDataType.STRING),
    ISWAND("IsWand", PersistentDataType.BYTE),
    PAGENUM("Page", PersistentDataType.INTEGER),
    HOTBARNUM("Hotbar_Num", PersistentDataType.INTEGER),
    SPELLTYPE("SpellType", PersistentDataType.STRING),
    SPELLVALUE("SpellValue", PersistentDataType.STRING),
    SPELLNAME("SpellName", PersistentDataType.STRING),
    REFLECT("Reflect", PersistentDataType.STRING),
    LOCATION("Location", PersistentDataType.STRING),
    SPREAD("Spread", null);

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
