package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.PotterWorldSpells;
import me.nanigans.potterworldspells.Utils.Config.JsonPaths;
import me.nanigans.potterworldspells.Utils.Config.JsonUtils;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

abstract public class Spell implements Listener {
    protected double cooldDown = 0D;// in seconds
    protected Wand wand;
    protected Player player;
    protected PotterWorldSpells plugin;
    protected ItemStack spell;
    protected BukkitTask task;
    protected long saveFallTime = 0;
    protected JsonUtils data = new JsonUtils();

    public Spell(Wand wand){
        this.wand = wand;
        this.player = wand.getPlayer();
        this.plugin = wand.getPlugin();
        this.spell = wand.getLastSpell();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Casts the spell. This will only create a loop between two points and will not increment the current position. You will
     * have to do this yourself
     * @param length the distance the spell will travel
     * @param spacing the spacing per particle
     * @param speed the speed of the particle
     * @param locCb the callback for when the spell is firing
     * @param endCb when the spell has hit something or has reached its max distance
     * @requires speed >= 0
     */
    protected void cast(double length, double spacing, final long speed, Callback locCb, Consumer<Location> endCb){
        Location p2 = player.getLocation().add(player.getLocation().getDirection().multiply(length));
        double distance = getSpellCastLoc().distance(p2);
        Vector p1 = getSpellCastLoc().toVector();
        Vector vector = p2.toVector().clone().subtract(p1).normalize().multiply(spacing);

        new BukkitRunnable() {
            @Override
            public void run() {

                for(double len = 0; len < distance; len += spacing){

                    if(speed > 0){
                        try{
                            Thread.sleep(speed);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }

                    final Location call = locCb.call(p1, vector);
                    if(call != null){
                        endCb.accept(call);
                        break;
                    }
                }
                endCb.accept(p1.toLocation(player.getWorld()));

            }
        }.runTaskAsynchronously(plugin);
    }

    public interface Callback{
        Location call(Vector p1, Vector vector);
    }

    /**
     * Adds a cooldown to the spell casted and times it per second with a runnable
     */
    protected void addCooldown(){

        final ItemStack lastSpell = wand.getLastSpell();
        lastSpell.setAmount((int)this.cooldDown);
        long time = (long) (System.currentTimeMillis() + (this.cooldDown*1000));
        ItemUtils.setData(lastSpell, Data.COOLDOWN.toString(), Data.COOLDOWN.getType(), time);

        final Spell that = this;
        this.task = new BukkitRunnable() {
            final long spellTime = (long) ItemUtils.getNBT(lastSpell, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
            @Override
            public void run() {
                if (wand.getPlayer().getInventory().first(lastSpell) == -1) {
                    this.cancel();
                }
                final long time = System.currentTimeMillis();
                if(spellTime > time){
                    if(lastSpell.getAmount() > 1)
                        lastSpell.setAmount(Math.max(lastSpell.getAmount()-1, 1));
                }else{
                    ItemUtils.removeNBT(lastSpell, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
                    wand.getActiveSpells().remove(that);
                    this.cancel();
                }

            }
        }.runTaskTimerAsynchronously(plugin, 20, 20);
        this.wand.getActiveSpells().put(lastSpell.getItemMeta().getDisplayName(), task);

    }

    public static void reloadCooldown(ItemStack item, Wand wand, PotterWorldSpells plugin, int pos) {

        if (ItemUtils.hasNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType())) {

            long time = (long) ItemUtils.getNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
            long currentTime = System.currentTimeMillis();
            long remainingTime = time - currentTime;
            if (remainingTime > 0) {

                int amount = (int) Math.ceil(remainingTime/1000D);
                if(amount > 1)
                item.setAmount(amount);
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (wand.getPlayer().getInventory().first(item) == -1) {
                            this.cancel();
                        }
                        long cTime = System.currentTimeMillis();
                        if (time > cTime) {
                            if (item.getAmount() > 1) {
                                item.setAmount(Math.max(item.getAmount()-1, 1));
                                wand.getPlayer().getInventory().setItem(pos, item);
                            }
                        } else {
                            ItemUtils.removeNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
                            wand.getPlayer().getInventory().setItem(pos, item);
                            this.cancel();
                        }
                    }

                }.runTaskTimerAsynchronously(plugin, 20, 20);
                wand.getActiveSpells().put(item.getItemMeta().getDisplayName(), task);

            }else{
                ItemUtils.removeNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
                item.setAmount(1);
            }

        }
    }

    @EventHandler
    public void fallDamage(EntityDamageEvent event){

        if(event.getEntity() instanceof Player){
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL)
                event.setCancelled(System.currentTimeMillis() < saveFallTime);

        }

    }

    /**
     * Removes the cooldown on a spell if it exists
     */
    public static void removeCooldown(Wand wand, String name){
        if(wand.getActiveSpells().containsKey(name)){
            wand.getActiveSpells().get(name).cancel();
        }
    }


    protected static Entity[] getEntitiesRadius(Location l, double radius) {
        double chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
        HashSet<Entity> radiusEntities = new HashSet<>();
        try {
            for (double chX = -chunkRadius; chX <= chunkRadius; chX++) {
                for (double chZ = -chunkRadius; chZ <= chunkRadius; chZ++) {
                    int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
                    for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
                        if (e != null)
                            if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
                                radiusEntities.add(e);
                    }
                }
            }
        }catch(NoSuchElementException ignored){}

        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }

    protected Entity getEntityAt(double hitboxRadius, Location loc) {
        final Entity[] ent = new Entity[1];
        new BukkitRunnable() {
            @Override
            public void run() {
                BoundingBox box = new BoundingBox(loc.getX() - hitboxRadius, loc.getY() - hitboxRadius, loc.getZ() - hitboxRadius, loc.getX() + hitboxRadius, loc.getY() + hitboxRadius, loc.getZ() + hitboxRadius);

                Collection<Entity> ents = loc.getWorld().getNearbyEntities(box);
                if (ents.size() > 0) {
                    Entity[] entArr = ents.toArray(new Entity[0]);
                    ent[0] = entArr[0];

                }
            }
        }.runTask(plugin);
        return ent[0];
    }

    protected String getData(Spell spell, String path) {
        try {
            return data.getData(JsonPaths.getSpell(spell.getClass().getSimpleName()) + "." + path).toString();
        }catch (IOException | ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    protected Location getSpellCastLoc(){
        if(player.getMainHand() == MainHand.RIGHT)
            return getRightArm();
        else return getLeftArm();
    }

    private Location getRightArm() {
        Location location = player.getEyeLocation().subtract(0, 0.25, 0);
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(0.3));
    }

    private Location getLeftArm() {
        Location location = player.getEyeLocation().subtract(0, 0.25, 0);
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(0.3));
    }


}
