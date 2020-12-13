package me.nanigans.potterworldspells.Utils.Config;

public enum JsonPaths {

    RANGE("range"),
    HITBOX("hitbox"),
    COOLDOWN("cooldown"),
    DURATION("duration"),
    HEALAMT("healamt");
    public String path;

    JsonPaths(String s) {
        this.path = s;
    }

    public static String getSpell(String spell){
        return "magic.spells."+spell;
    }

    public String getPath() {
        return path;
    }
}
