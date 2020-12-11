package me.nanigans.potterworldspells.Magic.Spells.Healing;

import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Healing;
import me.nanigans.potterworldspells.Magic.Wand;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Episkey extends Healing implements SpellCasting {
    private double spacing = 0.25;
    private double range = 58.5;
    private Particle particle = Particle.REDSTONE;
    private double hitRad = 0.25;
    private boolean endSpell = false;
    private short healAmt = 4;
    private Particle.DustOptions color = new Particle.DustOptions(Color.MAROON, 1);
    private HitTypes hit;
    private List<Entity> hitEnts;

    public Episkey(Wand wand) {
        super(wand);
        player.playSound(player.getEyeLocation(), "magic.hit", 1, 1);
        super.cooldDown = 10D;
        cast(range, spacing, 0, this::whileFiring, this::onHit);
        addCooldown();
    }

    @Override
    public void onHit(Location hitLoc) {

        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 5, 0, 0, 0, 0.1);

        if(hit == null || hit == HitTypes.ENTITY){

            Entity healed = player;
            final Entity entityAt = getEntityAt(hitRad, hitLoc);
            System.out.println("entityAt = " + entityAt);;
            if(entityAt != null) healed = entityAt;
            if(hitEnts != null && !hitEnts.isEmpty()) healed = hitEnts.get(0);
             healed.getWorld().playSound(healed.getLocation(), "magic.heal1", 1, 1);
            System.out.println("hitEnts = " + healed);

            WarpEffect effect = new WarpEffect(plugin.manager);
            effect.particle = Particle.HEART;
            effect.asynchronous = true;
            effect.particles = 4;
            effect.offset = new Vector(0, -1.5, 0);
            effect.iterations = 8;
            effect.grow = 0.5F;
            effect.setEntity(healed);
            effect.start();

            Entity finalHealed = healed;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(finalHealed instanceof LivingEntity){
                        final LivingEntity healed1 = (LivingEntity) finalHealed;
                        healed1.setHealth(Math.min(healed1.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                                healed1.getHealth()+healAmt));
                    }
                }
            }.runTaskLaterAsynchronously(plugin, 40);

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        player.getWorld().spawnParticle(particle, p1.getX(), p1.getY(), p1.getZ(), 2, color);
        if(Math.random() > .5)
        player.getWorld().spawnParticle(Particle.CRIT, p1.getX(), p1.getY()-0.005, p1.getZ(), 2, 0, 0, 0, 0);

        Location loc = p1.toLocation(player.getWorld());
        final Block blockAt = player.getWorld().getBlockAt(loc);
        if(blockAt.getType().isSolid()){
            hit = HitTypes.ENTITY;
            return loc;
        }
        final Entity entityAt = getEntityAt(hitRad, loc);
        System.out.println("entityAt = " + entityAt);
        if(entityAt != null)
            return loc;
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                final List<Entity> nearbyEntities = player.getWorld().getNearbyEntities(loc, hitRad, hitRad, hitRad).stream()
//                        .filter(i -> !(i instanceof LivingEntity)).collect(Collectors.toList());
//                if(!(nearbyEntities.contains(player) && nearbyEntities.size() == 1)){
//                    hitEnts = nearbyEntities;
//                    endSpell = true;
//                    hit = HitTypes.ENTITY;
//                }
//            }
//        }.runTask(plugin);

        if(endSpell) return loc;

        p1.add(vector.subtract(new Vector(0, 0, 0)));

        return null;
    }
}