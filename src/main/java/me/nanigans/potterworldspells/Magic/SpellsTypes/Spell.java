package me.nanigans.potterworldspells.Magic.SpellsTypes;

import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.PotterWorldSpells;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

abstract public class Spell {
    protected double cooldDown = 0D;// in seconds
    protected Wand wand;
    protected Player player;
    protected PotterWorldSpells plugin;
    protected ItemStack spell;
    protected BukkitTask task;

    public Spell(Wand wand){
        this.wand = wand;
        this.player = wand.getPlayer();
        this.plugin = wand.getPlugin();
        this.spell = wand.getLastSpell();
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
        this.wand.getActiveSpells().put(lastSpell, task);

    }

    public static void reloadCooldown(ItemStack item, Wand wand, PotterWorldSpells plugin, int pos) {

        System.out.println("item = " + item);
        if (ItemUtils.hasNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType())) {

            long time = (long) ItemUtils.getNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
            System.out.println("time = " + time);
            long currentTime = System.currentTimeMillis();
            long remainingTime = time - currentTime;
            if (remainingTime > 0) {

                int amount = (int) Math.ceil(remainingTime/1000D);
                if(amount > 0)
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
                wand.getActiveSpells().put(item, task);

            }else{
                ItemUtils.removeNBT(item, Data.COOLDOWN.toString(), Data.COOLDOWN.getType());
                item.setAmount(1);
            }

        }
    }


    /**
     * Removes the cooldown on a spell if it exists
     */
    public static void removeCooldown(Wand wand, ItemStack item){
        if(wand.getActiveSpells().containsKey(item)){
            System.out.println("item = " + item);
            wand.getActiveSpells().get(item).cancel();

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
