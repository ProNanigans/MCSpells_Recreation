package me.nanigans.potterworldspells.Magic.Spells.CrowdControl;

import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Crowd_Control;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Incarcerum extends Crowd_Control implements SpellCasting {
    //the rope is diamond shovel with model data of 372 on head

    private static final ItemStack itemCrackData = new ItemStack(Material.OAK_PLANKS);
    private HitTypes hit;
    private Entity hitEnt;

    public Incarcerum(Wand wand) {
        super(wand);
        final double distance = 40D;
        final double spacing = 0.5D;
        final long speed = 2;
        player.playSound(player.getLocation(), "magic.whoosh4", 100, 1);
        super.cast(distance, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hit != null){
            hitLoc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, hitLoc, 3, .1, .1, .1);

            if(hit == HitTypes.BLOCK){

                final Entity[] entitiesRadius = getEntitiesRadius(hitLoc, 2);

                for (Entity entity : entitiesRadius) {
                    effectEntity(entity);
                }

            }else if(hit == HitTypes.ENTITY){
                effectEntity(hitEnt);
            }

        }

    }

    private void effectEntity(Entity entity){

        if(entity instanceof LivingEntity && (canHitCaster || !entity.equals(player))){
            final LivingEntity alive = (LivingEntity) entity;
            final EntityEquipment armor = alive.getEquipment();

            final ItemStack head = armor.getHelmet() == null ? null : armor.getHelmet().clone();

            final ItemStack rope = new ItemStack(Material.DIAMOND_SHOVEL);
            final ItemMeta itemMeta = rope.getItemMeta();
            itemMeta.setCustomModelData(372);
            rope.setItemMeta(itemMeta);
            armor.setHelmet(rope);

            new BukkitRunnable() {
                @Override
                public void run() {
                    alive.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 100));
                    alive.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 250));
                }
            }.runTask(plugin);
            new BukkitRunnable() {
                @Override
                public void run() {
                    armor.setHelmet(head);
                }
            }.runTaskLaterAsynchronously(plugin, 60);

        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location loc = p1.toLocation(player.getWorld());
        BlockData fallingDustData = Material.BIRCH_PLANKS.createBlockData();
        player.spawnParticle(Particle.BLOCK_CRACK, loc, 4, .05, .05, .05, fallingDustData);

        player.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(),2, 0, 0, 0,
                new Particle.DustOptions(Color.fromRGB(205,133,63), 1));

        final Block blockAt = loc.getWorld().getBlockAt(loc);
        if(blockAt.getType().isSolid()) {
            if (blockAt.hasMetadata("Protego")) {
                if (reflectSpell(blockAt, player, p1, vector)) {
                    canHitCaster = true;
                    return null;
                }
            }
            hit = HitTypes.BLOCK;
            return loc;
        }


    final Entity[] entities = loc.getWorld().getChunkAt(loc).getEntities();
        for (Entity entity : entities) {
        if(canHitCaster || !entity.equals(player))
            if(entity instanceof LivingEntity && !((LivingEntity) entity).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)){
                if(entity.getBoundingBox().expand(.12).contains(p1)){
                    hitEnt = entity;
                    hit = HitTypes.ENTITY;
                    return loc;
                }
            }
    }


        p1.add(vector);
        return null;
    }
}
