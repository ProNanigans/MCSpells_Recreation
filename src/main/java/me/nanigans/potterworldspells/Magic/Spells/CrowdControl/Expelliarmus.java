package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import de.slikey.effectlib.effect.AnimatedBallEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Expelliarmus extends Crowd_Control implements SpellCasting {

    private Entity hitEnt;
    private HitTypes hit;

    public Expelliarmus(Wand wand) {
        super(wand);
        double range = 13D;
        double space = 0.25D;
        long speed = 1;
        player.playSound(player.getLocation(), "magic.chargeup4", 100, 1);
        super.cast(range, space, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {
        player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 5, 0, 0, 0, 0.1);

        if(hit != null && hit == HitTypes.ENTITY){

            AnimatedBallEffect ball = new AnimatedBallEffect(plugin.manager);
            ball.color = Color.RED;
            ball.setEntity(player);
            ball.asynchronous = true;
            ball.duration = 2000;
            ball.yOffset = -0.32F;
            ball.yFactor = 1.5F;
            ball.particle = Particle.REDSTONE;
            ball.start();
            hitEnt.getWorld().playSound(hitEnt.getLocation(), "magic.hit", 100, 1);

            if(hitEnt instanceof Player){

                Player pHit = (Player) hitEnt;
                if (Wand.inWandInv(player)) {
                    Wand.inWand.get(pHit.getUniqueId()).closeWand();
                }

                int pos = (int)(Math.random() * pHit.getInventory().getStorageContents().length);
                ItemStack item = pHit.getInventory().getItem(pos);
                ItemStack inHand = pHit.getInventory().getItemInMainHand();
                pHit.getInventory().setItem(pos, inHand);
                pHit.getInventory().setItemInMainHand(item);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        pHit.damage(0);
                    }
                }.runTask(plugin);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ItemStack item = pHit.getInventory().getItem(pos);
                        ItemStack inHand = pHit.getInventory().getItemInMainHand().clone();
                        pHit.getInventory().setItem(pos, inHand);
                        pHit.getInventory().setItemInMainHand(item);
                    }
                }.runTaskLaterAsynchronously(plugin, 50);

            }

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        player.getWorld().spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 2,  new Particle.DustOptions(
                Color.RED, 1
        ));
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, p1.getX(), p1.getY(), p1.getZ(), 0, 0, 0, 0);

        final Location location = p1.toLocation(player.getWorld());
        final Block blockAt = player.getWorld().getBlockAt(location);
        if(blockAt.getType().isSolid()){

            if(blockAt.hasMetadata("Protego")){
                if (reflectSpell(blockAt, player, p1, vector)) {
                    canHitCaster = true;
                    return null;
                }
            }
            hit = HitTypes.BLOCK;
            return location;
        }

        final Entity[] entities = location.getWorld().getChunkAt(location).getEntities();
        for (Entity entity : entities) {
            if(canHitCaster || !entity.equals(player))
                if(entity instanceof LivingEntity && !((LivingEntity) entity).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
                    if(entity.getBoundingBox().expand(0.12).contains(p1)){
                        hitEnt = entity;
                        hit = HitTypes.ENTITY;
                        return location;
                    }
                }
        }

        p1.add(vector);
        return null;
    }
}
