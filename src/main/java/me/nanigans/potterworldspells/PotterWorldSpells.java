package me.nanigans.potterworldspells;

import me.nanigans.potterworldspells.Commands.GiveWand;
import org.bukkit.plugin.java.JavaPlugin;

public final class PotterWorldSpells extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getCommand("wand").setExecutor(new GiveWand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
