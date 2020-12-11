package me.nanigans.potterworldspells.Utils;

public enum Spells {

    ANTI_APPARATE("Anti Apparate", 2, "CrowdControl"),//name, custom model data
    ACCIO("Accio", 3, "CrowdControl"),
    APPARATE("Apparate", 9, "Mobility"),
    ASCENDIO("Ascendio", 14, "Mobility"),
    TRIPUDIO("Tripudo", 181, "Mobility"),
    VOLO("Volo", 182, "Mobility"),
    EPISKEY("Episkey", 55, "Healing"),
    FERUVIO("Feruvio", 141, "Healing"),
    VULNERA("Vulnera", 140, "Healing"),
    AGUAMENTI("Aguamenti", 4, "Combat");
    String name, spellType;
    int data;

    Spells(String name, int modelData, String spellType) {
        this.name = name;
        this.data = modelData;
        this.spellType = spellType;
    }

    public String getSpellType() {
        return spellType;
    }

    public String getName() {
        return name;
    }

    public int getData() {
        return data;
    }
}
