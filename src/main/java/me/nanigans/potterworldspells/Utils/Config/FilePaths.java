package me.nanigans.potterworldspells.Utils.Config;

import me.nanigans.potterworldspells.PotterWorldSpells;

public enum FilePaths {

    USERS(PotterWorldSpells.getPlugin(PotterWorldSpells.class).getDataFolder()+"/Users");
    String s;
    FilePaths(String s) {
        this.s = s;
    }

    public String getPath() {
        return s;
    }
}
