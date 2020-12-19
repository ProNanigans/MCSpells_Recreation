package me.nanigans.potterworldspells.Magic.Spells.Defensive;

import de.slikey.effectlib.util.VectorUtils;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Defensive;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
//https://stackoverflow.com/questions/49825156/getting-position-of-a-coordinate-on-the-surface-of-a-sphere

public class Protego extends Defensive {
    public Protego(Wand wand) {
        super(wand);

        float pitch = player.getLocation().getPitch();//(float) Math.atan2(p1.getY(), 3);
        float yaw = player.getLocation().getYaw();//vector.toLocation(player.getWorld()).getyaw()//player.getLocation().getYaw()+45;
        //if(pitch < -180) pitch += 180;
//        //else if(pitch > 180) pitch -= 180;

        System.out.println("yaw = " + yaw);
        System.out.println("pitch = " + pitch);
        System.out.println();

//        if(yaw <= -180) yaw += 180;
//        else if(yaw >= 180) yaw -= 180;
//        System.out.println("yaw = " + yaw);
//        System.out.println("pitch = " + pitch);
        double pPrime = Math.toRadians(pitch);//(Math.PI * pitch) / 180;
        double yawPrime = Math.toRadians(yaw);//(Math.PI*yaw) / 180 + 180;
        for(double i = 0; i < 1; i+=1){
//            for(double j = 0; j < 5; j+=1) {
//                for(double k = 0; j < 5; j+=1) {

                    double x = 3 * Math.cos(pPrime) * Math.cos(yawPrime);
                    double y = 3 * Math.sin(pPrime);
                    double z = 3 * Math.cos(pPrime) * Math.sin(yawPrime);
                    Vector locs = new Vector(x, y, z).add(player.getLocation().toVector());
            System.out.println("locs = " + locs);
             player.getWorld().getBlockAt(locs.toLocation(player.getWorld())).setType(Material.BLACK_STAINED_GLASS);

//                }
//            }

        }


    }
}
