package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.isOnGround
import net.ccbluex.liquidbounce.utils.TimeUtils2
import net.ccbluex.liquidbounce.utils.createUseItemPacket
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.FloatValue
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "HytRun", description = "faq", category = ModuleCategory.HYT)
class HytRun : Module() {

    private val MotionF = FloatValue("MotionSpike", 5.0f, 0.1f, 30f)
    private val MotionY = FloatValue("MotionY", 5.0f, 0.1f, 30f)

    private val packets = LinkedBlockingQueue<IPacket>()
    private val positions = LinkedList<DoubleArray>()
    private var disableLogger = false
    var killAura: KillAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
    private val timer = TimeUtils2()

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

    override fun onDisable() {
        if (mc.thePlayer == null) return
        blink()
    }

    override fun onEnable() {
        synchronized(positions) {
            positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY + mc.thePlayer!!.eyeHeight / 2, mc.thePlayer!!.posZ))
            positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY, mc.thePlayer!!.posZ))
        }
        mc.thePlayer!!.motionY = MotionY.get().toDouble()
        mc.thePlayer!!.motionX *= MotionF.get()
        mc.thePlayer!!.motionZ *= MotionF.get()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        synchronized(positions) { positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY, mc.thePlayer!!.posZ)) }
        if (mc.thePlayer!!.onGround || isOnGround(0.5)) mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)))
        if (mc.thePlayer!!.isBlocking && isMoving && isOnGround(0.42) && killAura.target == null) {
            if (timer.delay(65F)) {
                mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.MAIN_HAND))
                mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.OFF_HAND))
                timer.reset()
            }
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
}