package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.value.FloatValue
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "HytSpeed", description = "AsOne & 司马人", category = ModuleCategory.HYT)
class HytSpeed : Module() {

    private val speed = FloatValue("Speed", 0.5f, 0.15f, 8f)
    private val motionY = FloatValue("MotionY", 0.42f, 0.1f, 2f)
    var doAsFly = false
    var stage = 0
    var timer = 0
    val packets = LinkedBlockingQueue<IPacket>()

    fun move() {
        if(isMoving){
            strafe(speed.get())
            mc.thePlayer!!.motionY = motionY.get().toDouble()
        }
        return
        var dir = mc.thePlayer!!.rotationYaw / 180 * Math.PI
        if (mc.thePlayer!!.motionY < 0) mc.thePlayer!!.motionY = -0.05
        mc.thePlayer!!.motionX = -Math.sin(dir) * speed.get()
        mc.thePlayer!!.motionZ = Math.cos(dir) * speed.get()
    }

    override fun onEnable() {
        timer = 0
    }

    override fun onDisable() {
        doAsFly = false
        if (packets.size > 0) {
            for (packet in packets) {
                mc.thePlayer!!.sendQueue.addToSendQueue(packet)
                packets.remove(packet)
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if(!isMoving)timer=0 else timer++
        if (mc.thePlayer!!.onGround && timer>1) {
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
        if (classProvider.isCPacketPlayerPosition(packet) || classProvider.isCPacketPlayerPosLook(packet) || classProvider.isCPacketPlayer(packet)) {
            event.cancelEvent()
            packets.add(packet)
            stage++
        }
    }
}