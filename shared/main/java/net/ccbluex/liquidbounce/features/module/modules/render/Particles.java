/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase;
import net.ccbluex.liquidbounce.event.AttackEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.particles.EvictingList;
import net.ccbluex.liquidbounce.utils.particles.Particle;
import net.ccbluex.liquidbounce.utils.particles.ParticleTimer;
import net.ccbluex.liquidbounce.utils.particles.Vec3;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;

@ModuleInfo(name = "Particles",description = "sb.",category = ModuleCategory.RENDER)
public final class Particles extends Module {

    private final IntegerValue amount = new IntegerValue("Amount", 10, 1, 20);

    private final BoolValue physics = new BoolValue("Physics", true);

    private final java.util.List<Particle> particles = new EvictingList<>(100);
    private final ParticleTimer timer = new ParticleTimer();
    private IEntityLivingBase target;

    @EventTarget
    public void onAttack(AttackEvent event) {
            target = LiquidBounce.combatManager.getTarget();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if (target != null && target.getHurtTime() >= 9 && mc.getThePlayer().getDistance(target.getPosX(), target.getPosY(), target.getPosZ()) < 10) {
            for (int i = 0; i < amount.get(); i++)
                particles.add(new Particle(new Vec3(target.getPosX() + (Math.random() - 0.5) * 0.5, target.getPosY() + Math.random() * 1 + 0.5, target.getPosZ() + (Math.random() - 0.5) * 0.5)));

            target = null;
        }
    }

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (particles.isEmpty())
            return;

        for (int i = 0; i <= timer.getElapsedTime() / 1E+11; i++) {
            if (physics.get())
                particles.forEach(Particle::update);
            else
                particles.forEach(Particle::updateWithoutPhysics);
        }

        particles.removeIf(particle -> mc.getThePlayer().getDistance(particle.position.xCoord, particle.position.yCoord, particle.position.zCoord) > 50 * 10);

        timer.reset();

        RenderUtils.renderParticles(particles);
    }
}