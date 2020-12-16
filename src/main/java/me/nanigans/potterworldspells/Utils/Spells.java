package me.nanigans.potterworldspells.Utils;

public enum Spells {

    ANTI_APPARATE("Anti Apparate", 2, "CrowdControl", 20),//name, custom model data
    ACCIO("Accio", 3, "CrowdControl", 0),
    APPARATE("Apparate", 9, "Mobility", 0),
    ASCENDIO("Ascendio", 14, "Mobility", 0),
    TRIPUDIO("Tripudo", 181, "Mobility", 0),
    VOLO("Volo", 182, "Mobility", 0),
    EPISKEY("Episkey", 55, "Healing", 0),
    FERUVIO("Feruvio", 141, "Healing", 0),
    VULNERA("Vulnera", 140, "Healing", 0),
    ARRESTO_MOMENTUM("Arresto Momentum", 15, "Defensive", 0),
    DISAPPEARUS("Disappearus", 43, "Defensive", 0),
    FORTIFICUS("Fortificus", 183, "Defensive", 0),
    FINITE_INCANTATEM("Finite Incantatem", 60, "Defensive", 0),
    FLAME_FREEZE("Flame Freeze", 56, "Defensive", 0),
    AGUAMENTI("Aguamenti", 4, "Combat", 0);
    String name, spellType;
    int data;
    int cost;

    Spells(String name, int modelData, String spellType, int manaCost) {
        this.name = name;
        this.data = modelData;
        this.spellType = spellType;
    }

    public int getCost(){return this.cost;}

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
