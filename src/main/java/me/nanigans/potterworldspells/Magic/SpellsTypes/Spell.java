package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Cooldown;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.PotterWorldSpells;
import me.nanigans.potterworldspells.Utils.Config.JsonPaths;
import me.nanigans.potterworldspells.Utils.Config.JsonUtils;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.function.Consumer;

abstract public class Spell implements Listener {
    protected double cooldown = 0D;// in seconds
    protected Wand wand;
    protected Player player;
    protected PotterWorldSpells plugin;
    protected ItemStack spell;
    protected Timer task;
    protected long saveFallTime = 0;
    protected JsonUtils data = new JsonUtils();
    protected boolean ignoreCancel = false;
    protected boolean canHitCaster = false;

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
     * @param castDistance the distance the spell will travel
     * @param spacing the spacing per particle
     * @param speed the speed of the particle
     * @param locCb the callback for when the spell is firing
     * @param endCb when the spell has hit something or has reached its max distance
     * @requires speed >= 0
     */
    protected void cast(double castDistance, double spacing, final long speed, Callback locCb, Consumer<Location> endCb){

        Location p2 = player.getLocation().add(player.getLocation().getDirection().multiply(castDistance));
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
                        if(!ignoreCancel) {
                            this.cancel();
                            return;
                        }
                    }
                }
                endCb.accept(p1.toLocation(player.getWorld()));

            }
        }.runTaskAsynchronously(plugin);
    }

    public interface Callback{
        Location call(Vector p1, Vector vector);
    }


    public static boolean reflectSpell(Block blockAt, Player player, Vector p1, Vector vector){

        final BlockFace face = blockAt.getFace(player.getWorld().getBlockAt(p1.subtract(vector).toLocation(player.getWorld())));
        if(face != null && face.getDirection() != null) {
            final double dot = vector.dot(face.getDirection());
            final Vector r = vector.subtract(face.getDirection().multiply(2 * dot));
            vector.setX(r.getX());
            vector.setY(r.getY());
            vector.setZ(r.getZ());
            return true;
        }else{
            return false;
        }
    }

    /**
     * Adds a cooldown to the spell casted and times it per second with a runnable
     */
    public void addCooldown(){

        final ItemStack lastSpell = wand.getLastSpell();
        lastSpell.setAmount((int)this.cooldown);
        long time = (long) (System.currentTimeMillis() + (this.cooldown *1000));
        ItemUtils.setData(lastSpell, Data.COOLDOWN.toString(), Data.COOLDOWN.getType(), time);
        wand.updateWand();

        Timer t = new Timer();
        final Cooldown cooldown = new Cooldown(this);
        t.schedule(cooldown, 1000, 1000);

        this.task = t;
        this.wand.getActiveSpellCDS().put(ItemUtils.getNBT(lastSpell, Data.SPELLNAME.toString(), Data.SPELLNAME.getType()).toString(), t);

    }

    /**
     * Reloads a spells cooldown for when it gets loaded into a players inventory
     * @param item the item the spell is assigned to
     * @param wand the wand the spell is on
     * @param plugin PotterWorldSpells
     * @param pos the inventory position the spell is found in the inventory
     */
    public static void reloadCooldown(ItemStack item, Wand wand, PotterWorldSpells plugin, int pos) {

        if (ItemUtils.hasNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType())) {

            long time = (long) ItemUtils.getNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
            long currentTime = System.currentTimeMillis();
            long remainingTime = time - currentTime;
            if (remainingTime > 0) {

                int amount = (int) Math.ceil(remainingTime/1000D);
                if(amount > 1)
                item.setAmount(amount);
                BukkitRunnable task = new BukkitRunnable() {
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
                            wand.updateWand();
                            wand.getPlayer().getInventory().setItem(pos, item);
                            this.cancel();
                        }
                    }

                };
                task.runTaskTimerAsynchronously(plugin, 20, 20);
                wand.getActiveSpellCDS().put(item.getItemMeta().getDisplayName(), task);

            }else{
                ItemUtils.removeNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
                item.setAmount(1);
            }

        }
    }

    /**
     * Prevents a player from taking fall damage depending on the spell they casted
     * @param event
     */
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
        if(wand.getActiveSpellCDS().containsKey(name)){
            wand.getActiveSpellCDS().get(name).cancel();
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



    public Wand getWand() {
        return wand;
    }

    public Player getPlayer() {
        return player;
    }

    public double getCooldown(){
        return cooldown;
    }

}
