package me.nanigans.potterworldspells.Utils;

public enum Spells {

    FLIPPENDO("Anti Apparate", 2),//name, custom model data
    ACCIO("Accio", 3),
    AGUAMENTI("Aguamenti", 4);
    String name;
    int data;

    Spells(String name, int modelData) {
        this.name = name;
        this.data = modelData;
    }

    public String getName() {
        return name;
    }

    public int getData() {
        return data;
    }
}
