package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.speed
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "HytDash", description = "faq", category = ModuleCategory.HYT)
class HytDash : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Lag", "NewLag"), "Lag")
    private val lagPowerValue = IntegerValue("LagPower", 8, 1, 8)
    private val newLagPowerValue = IntegerValue("NewLagPower", 20, 10, 50)

    private val packets = LinkedBlockingQueue<IPacket>()
    private val positions = LinkedList<DoubleArray>()
    private var disableLogger = false

    private var jumped = false
    private var canBoost = false

    override fun onEnable() {
        synchronized(positions) {
            positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY + mc.thePlayer!!.eyeHeight / 2, mc.thePlayer!!.posZ))
            positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY, mc.thePlayer!!.posZ))
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        if (mc.thePlayer == null) return
        blink()
    }

    @EventTarget
    fun onMove(e: MoveEvent) {
        if (modeValue.get().equals("newlag", ignoreCase = true)) {
            if (!isMoving && jumped) {
                mc.thePlayer!!.motionX = 0.0
                mc.thePlayer!!.motionZ = 0.0
                e.zeroXZ()
            }
        }
    }

    private fun blink() {
        try {
            disableLogger = true

            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }

            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }

    @EventTarget
    fun onJump(e: JumpEvent?) {
        if (modeValue.get().equals("newlag", ignoreCase = true)) {
            jumped = true
            canBoost = true
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet: IPacket = event.packet

        if (mc.thePlayer == null || disableLogger)
            return

        if (classProvider.isCPacketPlayer(packet)) // Cancel all movement stuff
            event.cancelEvent()

        if (classProvider.isCPacketPlayerPosition(packet) || classProvider.isCPacketPlayerPosLook(packet) ||
                classProvider.isCPacketPlayerBlockPlacement(packet) ||
                classProvider.isCPacketAnimation(packet) ||
                classProvider.isCPacketEntityAction(packet) || classProvider.isCPacketUseEntity(packet)) {
            event.cancelEvent()
            packets.add(packet)
        }

    }

    @EventTarget
    fun onMotion(e: MotionEvent?) {
        if (modeValue.get().equals("lag", ignoreCase = true)) {
            if (isMoving) {
                mc.timer.timerSpeed = 1.0f
                if (mc.thePlayer!!.onGround) {
                    strafe(lagPowerValue.get().toFloat())
                    mc.thePlayer!!.motionY = 0.42
                }
                strafe(lagPowerValue.get().toFloat())
            } else {
                mc.thePlayer!!.motionZ = 0.0
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionZ
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        synchronized(positions) { positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY, mc.thePlayer!!.posZ)) }
        if (modeValue.get().equals("newlag", ignoreCase = true)) {
            if (jumped) {
                if (mc.thePlayer!!.onGround || mc.thePlayer!!.capabilities.isFlying) {
                    jumped = false
                    mc.thePlayer!!.motionX = 0.0
                    mc.thePlayer!!.motionZ = 0.0
                    return
                }
                strafe(speed * if (canBoost) newLagPowerValue.get() else 1)
                canBoost = false
            }
            if (mc.thePlayer!!.onGround && isMoving) {
                jumped = true
                mc.thePlayer!!.jump()
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}