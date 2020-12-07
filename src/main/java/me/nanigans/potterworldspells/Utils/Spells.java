package me.nanigans.potterworldspells.Utils;

public enum Spells {

    FLIPPENDO("Flippendo", 34);//name, custom model data
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
