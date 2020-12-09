package me.nanigans.potterworldspells;

import de.slikey.effectlib.EffectManager;
import me.nanigans.potterworldspells.Commands.GiveWand;
import me.nanigans.potterworldspells.Events.WandClickEvents;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Config.FilePaths;
import me.nanigans.potterworldspells.Utils.Config.PathCreator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ConcurrentModificationException;

public final class PotterWorldSpells extends JavaPlugin {

    public final EffectManager manager = new EffectManager(this);

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
        Wand.inWand.forEach((i, j) -> {
            try {
                j.closeWand();
            }catch(ConcurrentModificationException ignored){}
        });
    }
}
