package me.nanigans.potterworldspells.Utils.Particles;

import com.google.common.collect.Maps;
import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.VectorUtils;
import me.nanigans.potterworldspells.Magic.SpellsTypes.Spell;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class VortexEffect extends Effect {
    public Particle particle;
    public float radius;
    public float grow;
    public double radials;
    public int circles;
    public int helixes;
    public Consumer<Location> callback;
    protected int step;

    public VortexEffect(EffectManager effectManager) {
        super(effectManager);
        this.particle = Particle.FLAME;
        this.radius = 2.0F;
        this.grow = 0.05F;
        this.radials = 0.19634954084936207D;
        this.circles = 3;
        this.helixes = 4;
        this.step = 0;
        this.type = EffectType.REPEATING;
        this.period = 1;
        this.iterations = 200;
    }

    @Override
    public void onRun() {
        final Location location = this.onRun1();
        callback.accept(location);
    }

    public void reset() {
        this.step = 0;
    }

    public Location onRun1() {
        Location location = this.getLocation();
        Vector v = null;

        for(int x = 0; x < this.circles; ++x) {
            for(int i = 0; i < this.helixes; ++i) {
                double angle = (double)this.step * this.radials + 6.283185307179586D * (double)i / (double)this.helixes;
                v = new Vector(Math.cos(angle) * (double)this.radius, (double)((float)this.step * this.grow), Math.sin(angle) * (double)this.radius);
                VectorUtils.rotateAroundAxisX(v, (double)((location.getPitch() + 90.0F) * 0.017453292F));
                VectorUtils.rotateAroundAxisY(v, (double)(-location.getYaw() * 0.017453292F));
                location.add(v);
                this.display(this.particle, location);
                location.subtract(v);
            }

            ++this.step;
        }

        return location.add(v);
    }
}
