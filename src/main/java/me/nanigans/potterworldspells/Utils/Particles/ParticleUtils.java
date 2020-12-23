package me.nanigans.potterworldspells.Utils.Particles;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ParticleUtils {

    public static List<Location> verticleCircle(Location center, float radius, int amount, Vector dir) {

        double inc = (2 * Math.PI) / amount;
        int steps = amount;
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            double angle = i * inc;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * radius);
            v.setZ(Math.sin(angle) * radius);

            v.rotateAroundNonUnitAxis(dir,  90 * Math.PI/180);
            v.rotateAroundY(Math.PI/2);
            locations.add(new Location(center.getWorld(), v.getX()+center.getX(), v.getY()+center.getY(), v.getZ()+center.getZ()));
        }
        return locations;
    }

}
