package me.nanigans.potterworldspells.Magic.Spells.Utility;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.HelixEffect;
import de.slikey.effectlib.effect.WarpEffect;
import me.nanigans.potterworldspells.Magic.Spells.HitTypes;
import me.nanigans.potterworldspells.Magic.Spells.SpellCasting;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Utility;
import me.nanigans.potterworldspells.Magic.Wand;
import me.nanigans.potterworldspells.Utils.Data;
import me.nanigans.potterworldspells.Utils.LocSerialization;
import me.nanigans.potterworldspells.Utils.Particles.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class Deprimo extends Utility implements SpellCasting, Listener {
    private HitTypes hit;
    private static final Map<UUID, BukkitTask> runnables = new HashMap<>();
    public Deprimo(Wand wand) {
        super(wand);
        final double range = 40D;
        final double spacing = 0.5D;
        final long speed = 10;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        super.cast(range, spacing, speed, this::whileFiring, this::onHit);
    }

    @Override
    public void onHit(Location hitLoc) {

        if(hit == HitTypes.BLOCK){

            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, hitLoc, 20, 0, 0, 0, 0.2);
            hitLoc.getWorld().playSound(hitLoc, "magic.hit", 100, 1);

            WarpEffect warp = new WarpEffect(plugin.manager);
            warp.asynchronous = true;
            warp.setLocation(hitLoc);
            warp.radius = 3;
            warp.duration = 1200;
            warp.particle = Particle.REDSTONE;
            warp.color = Color.YELLOW;
            warp.rings = 7;
            warp.start();
            warp.callback = () -> {
                hitLoc.getWorld().playSound(hitLoc, "magic.electrichit", 100, 1);
                HelixEffect helix = new HelixEffect(plugin.manager);
                helix.asynchronous = true;
                helix.offset = new Vector(0, 0.5, 0);
                helix.setLocation(hitLoc);
                helix.radius = 3;
                helix.particleCount = 1;
                helix.particles = 20;
                helix.curve = (float) (Math.PI/2);
                helix.type = EffectType.INSTANT;
                helix.particle = Particle.REDSTONE;
                helix.color = Color.YELLOW;
                helix.start();
                helix.callback = () -> breakBlocks(hitLoc);
            };

        }

    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent event){
        final Entity entity = event.getEntity();
        if(entity.hasMetadata(Data.LOCATION.toString())){
            final Block block = event.getBlock();
            block.setMetadata(Data.LOCATION.toString(),
                    new FixedMetadataValue(plugin, event.getEntity().getMetadata(Data.LOCATION.toString())));
            System.out.println("block.getMetadata(\"TASKID\") = " + entity.getMetadata("TASKID").get(0).asInt());
        }
        //System.out.println("event.getBlock() = " + event.getBlock().getMetadata(Data.LOCATION.toString()));
    }

    private void breakBlocks(Location loc){

        final List<Location> sphere = ParticleUtils.createSphere(loc, 3, false);
        final List<FallingBlock> blocks = new ArrayList<>();

        final BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (FallingBlock block : blocks) {
                    final List<MetadataValue> taskid = block.getLocation().getBlock().getMetadata("TASKID");
                    if (taskid.size() > 0 && taskid.get(0).asInt() == this.getTaskId()) {

                        final List<MetadataValue> metadata = block.getMetadata(Data.LOCATION.toString());
                        if(metadata.size() > 0) {
                            final Location loc = LocSerialization.getLiteLocationFromString(metadata.get(0).asString());
                            loc.getBlock().setType(block.getBlockData().getMaterial());
                            block.getLocation().getBlock().setType(Material.AIR);
                            block.remove();
                        }

                    }
                }
            }
        }.runTaskLater(plugin, 100);

        for (Location location : sphere) {
            final Block block = location.getBlock();
            if(block.getType().isSolid() && block.getType().getHardness() <= 1.5){

                final BlockData data = block.getBlockData();

                final FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(location, data);
                fallingBlock.setVelocity(new Vector(0, 0.2, 0));
                fallingBlock.setPersistent(true);
                if(!block.hasMetadata(Data.LOCATION.toString()))
                    fallingBlock.setMetadata(Data.LOCATION.toString(), new FixedMetadataValue(plugin, LocSerialization.getLiteStringFromLocation(location)));
                fallingBlock.setMetadata("TASKID", new FixedMetadataValue(plugin, bukkitTask.getTaskId()));

                fallingBlock.setDropItem(false);
                blocks.add(fallingBlock);
                block.setType(Material.AIR);//TODO: finish
            }
        }

    }

    @Override
    public Location whileFiring(Vector p1, Vector vector) {

        final Location location = p1.toLocation(player.getWorld());
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, new Particle.DustOptions(Color.YELLOW, 1));

        final Block blockAt = location.getBlock();
        if(blockAt.getType().isSolid()) {
            if (blockAt.hasMetadata(Data.REFLECT.toString())) {
                if (reflectSpell(blockAt, player, p1, vector)) {
                    return null;
                }
            }
            hit = HitTypes.BLOCK;
            return location;
        }

        p1.add(vector.subtract(new Vector(0, 0.008, 0)));
        return null;
    }
}
