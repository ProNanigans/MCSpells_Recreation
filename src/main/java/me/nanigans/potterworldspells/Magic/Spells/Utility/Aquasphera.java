package me.nanigans.potterworldspells.Magic.Spells.Utility;

import de.slikey.effectlib.effect.SphereEffect;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Utility;
import me.nanigans.potterworldspells.Magic.Wand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Aquasphera extends Utility implements Listener {
    private ItemStack prevHelmet;
    public Aquasphera(Wand wand) {
        super(wand);

        SphereEffect sphere = new SphereEffect(plugin.manager);
        sphere.asynchronous = true;
        sphere.setEntity(player);
        sphere.radius = 0.35;
        sphere.particle = Particle.FALLING_WATER;
        sphere.duration = 1500;
        sphere.radiusIncrease = 0;
        sphere.start();
        player.getWorld().playSound(player.getLocation(), "magic.disillusion", 50, 1);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WATER_AMBIENT, 50, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 2));
            }
        }.runTask(plugin);

        addHelmet();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void invClick(InventoryClickEvent event){
        if(this.prevHelmet != null && event.getWhoClicked().equals(player)) {
            final ItemStack item = event.getCurrentItem();
            if (item.getType() == Material.WHITE_STAINED_GLASS && item.getItemMeta().getDisplayName().contains("Bubble")) {
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getEquipment().setHelmet(prevHelmet);
                        HandlerList.unregisterAll(Aquasphera.this);
                    }
                }.runTaskLaterAsynchronously(plugin, 0);
            }
        }

    }

    protected void addHelmet(){

        final EntityEquipment armor = player.getEquipment();
        final ItemStack head = armor.getHelmet() == null ? null : armor.getHelmet().clone();
        if(!(head != null && head.getType() == Material.WHITE_STAINED_GLASS)) {
            this.prevHelmet = head;
            final ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS);
            final ItemMeta itemMeta = glass.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD+"Enchanted Bubble");
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            glass.setItemMeta(itemMeta);
            glass.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 3);
            armor.setHelmet(glass);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                final ItemStack helmet = player.getEquipment().getHelmet();
                if(helmet != null && helmet.getType() == Material.WHITE_STAINED_GLASS && helmet.getItemMeta().getDisplayName().contains("Bubble")){
                    armor.setHelmet(head);
                    HandlerList.unregisterAll(Aquasphera.this);
                }
            }
        }.runTaskLaterAsynchronously(plugin, 1200);

    }

}
