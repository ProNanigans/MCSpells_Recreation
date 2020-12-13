package me.nanigans.potterworldspells;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.slikey.effectlib.EffectManager;
import me.nanigans.potterworldspells.Commands.GiveWand;
import me.nanigans.potterworldspells.Events.WandClickEvents;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Config.CustomizedObjectTypeAdapter;
import me.nanigans.potterworldspells.Utils.Config.FilePaths;
import me.nanigans.potterworldspells.Utils.Config.PathCreator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

public final class PotterWorldSpells extends JavaPlugin {

    public final EffectManager manager = new EffectManager(this);
    GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new CustomizedObjectTypeAdapter());
    public HashMap map = new HashMap<>();

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

        File configFile = new File(getDataFolder()+"/config.json");

        if(!configFile.exists()) {

            saveResource(configFile.getName(), false);
            try {
                Gson gson = gsonBuilder.create();

                map = gson.fromJson(new FileReader(configFile), HashMap.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

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
