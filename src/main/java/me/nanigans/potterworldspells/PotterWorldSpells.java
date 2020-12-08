package me.nanigans.potterworldspells;

import me.nanigans.potterworldspells.Commands.GiveWand;
import me.nanigans.potterworldspells.Events.WandClickEvents;
import me.nanigans.potterworldspells.Utils.Config.FilePaths;
import me.nanigans.potterworldspells.Utils.Config.PathCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class PotterWorldSpells extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getCommand("wand").setExecutor(new GiveWand());
        getServer().getPluginManager().registerEvents(new WandClickEvents(), this);
        try {
            PathCreator.createPath(FilePaths.USERS.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
