package me.nanigans.potterworldspells;

import me.nanigans.potterworldspells.Commands.GiveWand;
import me.nanigans.potterworldspells.Events.WandClickEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class PotterWorldSpells extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getCommand("wand").setExecutor(new GiveWand());
        getServer().getPluginManager().registerEvents(new WandClickEvents(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
