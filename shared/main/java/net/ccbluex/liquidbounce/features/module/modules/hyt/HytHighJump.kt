package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils.forward
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "HytHighJump", description = "faq", category = ModuleCategory.HYT)
class HytHighJump : Module() {

    private val packets = LinkedBlockingQueue<IPacket>()
    private val positions = LinkedList<DoubleArray>()
    private var disableLogger = false

    private val modeValue = ListValue("Mode", arrayOf("Bug", "Lag"), "Bug")
    private val bugPowerValue = IntegerValue("BugPower", 5, 1, 5)
    private val lagPowerValue = IntegerValue("FlagPower", 5, 3, 10)

    override fun onDisable() {
        if (modeValue.get().equals("lag", ignoreCase = true)) {
            if (mc.thePlayer == null) return
            blink()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (modeValue.get().equals("lag", ignoreCase = true)) {
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

    override fun onEnable() {
        if (modeValue.get().equals("lag", ignoreCase = true)) {
            synchronized(positions) {
                positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY + mc.thePlayer!!.eyeHeight / 2, mc.thePlayer!!.posZ))
                positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY, mc.thePlayer!!.posZ))
            }
        }
        if (modeValue.get().equals("bug", ignoreCase = true)) {
            mc.thePlayer!!.addChatMessage(classProvider.createChatComponentText("§c请按下空格执行高跳"))
        }
    }

    @EventTarget
    fun onJump(e: JumpEvent) {
        if (modeValue.get().equals("lag", ignoreCase = true)) {
            e.motion = e.motion * lagPowerValue.get()
        }
    }

    @EventTarget
    fun onKey(e: KeyEvent) {
        if (modeValue.get().equals("bug", ignoreCase = true)) {
            if (e.key == 57) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY + bugPowerValue.get(), mc.thePlayer!!.posZ, true))
                forward(0.04)
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (modeValue.get().equals("lag", ignoreCase = true)) {
            synchronized(positions) { positions.add(doubleArrayOf(mc.thePlayer!!.posX, mc.thePlayer!!.entityBoundingBox.minY, mc.thePlayer!!.posZ)) }
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

    override val tag: String
        get() = modeValue.get()
}