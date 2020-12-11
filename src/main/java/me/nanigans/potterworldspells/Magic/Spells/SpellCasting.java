package me.nanigans.potterworldspells.Magic.Spells;

import org.bukkit.Location;
import org.bukkit.util.Vector;

 public interface SpellCasting {

    void onHit(Location hit);
    Location whileFiring(Vector p1, Vector vector);

}
