package net.ccbluex.liquidbounce.features.module.modules.hyt

import me.utils.PacketUtils
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketChat
import net.minecraft.network.play.server.SPacketPlayerPosLook
import org.apache.commons.lang3.RandomUtils

@ModuleInfo(name = "Disabler", description = "Anti 欣欣.", category = ModuleCategory.HYT)
class LZQDisabler: Module() {
    val modeValue = ListValue("Mode", arrayOf("HuaYuTing", "Hypixel"), "HuaYuTing")
    private val hypStrafeValue = BoolValue("Hypixel-StrafeTest", true)
    private val hytMoveValue = BoolValue("HYT-Move", true)
    private val hytTimerValue = BoolValue("HYT-Timer", true)
    private val hytBlinkTestValue = BoolValue("HYT-BlinkTest", true)
    private val hytLagValue = BoolValue("HYT-S08Silent", true)
    private var sb = true
    private var movePackets = arrayListOf<CPacketConfirmTransaction>()
    private var timerPackets = HashMap<CPacketKeepAlive, Int>()
    val msTimer: MSTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(sb){
            if (movePackets.size >= 4) {
                for (packet in movePackets) {
                    PacketUtils.sendPacketNoEvent(packet)
                }
                movePackets.clear()
            }

            for (packet in timerPackets) {
                if (msTimer.hasTimePassed(packet.value.toLong())) {
                    PacketUtils.sendPacketNoEvent(packet.key)
                    timerPackets.remove(packet.key)
                    msTimer.reset()
                }
            }
        }
//        if(mc.theWorld == null) return

    }
    @EventTarget
    fun onWorld(event : WorldEvent){
        msTimer.reset()
        movePackets.clear()
        sb = false
        timerPackets.clear()
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet is SPacketChat) {
            if(packet.chatComponent.unformattedText.contains("游戏开始")){
                sb = true
                ClientUtils.displayChatMessage(sb.toString())
            }
        }
    if(sb){

        if (packet is CPacketConfirmTransaction) {
            if (modeValue.get() == "HuaYuTing" && hytMoveValue.get()) if (!PacketUtils.handleSendPacket(packet)) movePackets.add(packet)
            if (modeValue.get() == "Hypixel" && hypStrafeValue.get()) if (!PacketUtils.handleSendPacket(packet)) movePackets.add(packet)
        }

        if (packet is CPacketKeepAlive) {
            if (modeValue.get() == "HuaYuTing" && hytTimerValue.get()) if (!PacketUtils.handleSendPacket(packet)) timerPackets[packet] =
                RandomUtils.nextInt(400, 500)
        }

        if (packet is SPacketPlayerPosLook) {
            if (modeValue.get() == "HuaYuTing" && hytLagValue.get()) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(packet.x, packet.y, packet.z, packet  .yaw    , packet.pitch, mc.thePlayer!!.onGround))
                event.cancelEvent()
            }
        }

        if (packet is CPacketPlayer.Position) {
            if (modeValue.get() == "HuaYuTing" && hytBlinkTestValue.get()) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(packet.x, packet.y, packet.z, mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch, mc.thePlayer!!.onGround))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(packet.x, packet.y + 1E7, packet.z, mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch, mc.thePlayer!!.onGround))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(packet.x, packet.y - 1E8, packet.z, mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch, mc.thePlayer!!.onGround))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(packet.x, packet.y - 0.000001, packet.z, mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch, mc.thePlayer!!.onGround))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(packet.x, packet.y, packet.z, mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch, mc.thePlayer!!.onGround))
                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SNEAKING))
            }
        }
    }
//        if(mc.theWorld == null) return

    }

    override val tag: String?
        get() = "Lzq1337"
}