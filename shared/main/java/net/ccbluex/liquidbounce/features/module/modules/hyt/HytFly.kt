package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.Velocity
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "HytFly", description = "Asone & 傻逼", category = ModuleCategory.HYT)
class HytFly : Module() {

    var doAsFly = false
    var stage = 0
    val jump = false
    var timer = 0
    val packets = LinkedBlockingQueue<IPacket>()
    var y = 0
    private val motionXZ = FloatValue("MotionXZ", 2.5f, 0f, 10f)
    private val motionY = FloatValue("MotionY", 1.8f, 0.42f, 6f)
    private val Bob = FloatValue("Bob", 0.5f, 0f, 2f)
    private val delay = IntegerValue("Delay", 2, 0, 10)
    private val Timer = FloatValue("Timer", 1.1f, 0.1f, 2f)

    fun move() {
        mc.thePlayer!!.cameraYaw = Bob.get()
        mc.timer.timerSpeed = Timer.get()
        if (mc.thePlayer!!.posY <= y) {
            mc.thePlayer!!.motionY = motionY.get().toDouble()
        } else {
            var dir = mc.thePlayer!!.rotationYaw / 180 * Math.PI
            if (mc.thePlayer!!.motionY < 0) mc.thePlayer!!.motionY = -0.05
            mc.thePlayer!!.motionX = -Math.sin(dir) * motionXZ.get()
            mc.thePlayer!!.motionZ = Math.cos(dir) * motionXZ.get()
        }
    }

    override fun onEnable() {
        y = mc.thePlayer!!.posY.toInt()
        timer = 999
        LiquidBounce.moduleManager.getModule(Velocity::class.java).state = false
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        doAsFly = false
        if (packets.size > 0) {
            for (packet in packets) {
                mc.thePlayer!!.sendQueue.addToSendQueue(packet)
                packets.remove(packet)
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!doAsFly) timer++
        if (timer > delay.get()) {
            timer = 0
            doAsFly = true
            stage = 0
            move()
        }
        if (stage >= 1) {
            doAsFly = false
            if (packets.size > 0) {
                for (packet in packets) {
                    mc.thePlayer!!.sendQueue.addToSendQueue(packet)
                    packets.remove(packet)
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (!doAsFly) return
        var packet = event.packet
        if (classProvider.isCPacketPlayer(packet)) event.cancelEvent()
        if (classProvider.isCPacketPlayerPosition(packet) || classProvider.isCPacketPlayerPosLook(packet) ||
                classProvider.isCPacketPlayerBlockPlacement(packet) ||
                classProvider.isCPacketAnimation(packet) ||
                classProvider.isCPacketEntityAction(packet) || classProvider.isCPacketUseEntity(packet)) {
            event.cancelEvent()
            packets.add(packet)
            stage++
        }
        if (classProvider.isCPacketPlayer(packet)) {
            val playerPacket = packet.asCPacketPlayer()

            playerPacket.onGround = true
        }
    }
}