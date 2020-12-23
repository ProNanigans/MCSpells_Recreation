package me.nanigans.potterworldspells.Magic.Spells.Defensive;

import me.nanigans.potterworldspells.Magic.SpellsTypes.Defensive;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
//https://stackoverflow.com/questions/49825156/getting-position-of-a-coordinate-on-the-surface-of-a-sphere

public class Protego extends Defensive {//TODO: figure protego out
    public Protego(Wand wand) {
        super(wand);

        Location pLoc = player.getLocation();
        double pitch = ((pLoc.getPitch()+90) * Math.PI) / 180;
        double yaw = ((pLoc.getYaw()+90) * Math.PI) / 180;

        for(double i = 0; i < 3; i+=1) {
                double x = 3 * Math.sin(pitch+i) * Math.cos(yaw+i);
                double y = 3 * Math.sin(pitch+i) * Math.sin(yaw+i);
                double z = 3 * Math.cos(pitch+i);
                Vector v = new Vector(x, z, y);
            final Block blockAt = player.getWorld().getBlockAt(v.add(pLoc.toVector()).toLocation(pLoc.getWorld()));
            blockAt.setType(Material.WHITE_STAINED_GLASS);
            blockAt.setMetadata(Data.REFLECT.toString(), new FixedMetadataValue(plugin, player.getUniqueId().toString()));

        }

    }
}
