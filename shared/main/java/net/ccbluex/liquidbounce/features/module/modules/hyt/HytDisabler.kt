package net.ccbluex.liquidbounce.features.module.modules.hyt

//import net.ccbluex.liquidbounce.utils.PacketUtils

import io.netty.buffer.Unpooled
import me.utils.PacketUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.client.CPacketPlayer
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*


@ModuleInfo(name = "HytDisabler",description = "自写", category = ModuleCategory.HYT)
class HytDisabler:Module() {
    private val packetBuffer = LinkedList<Packet<INetHandlerPlayServer>>()
    private val keepAlives = arrayListOf<CPacketKeepAlive>()

    private var vulTickCounterUID = 0
    private val lagTimer = MSTimer()
    private val msTimer = MSTimer()

    private val transactions = arrayListOf<CPacketConfirmTransaction>()
    override fun onEnable() {

        msTimer.reset()
        transactions.clear()
        keepAlives.clear()
    }

    override fun onDisable() {
        msTimer.reset()
        transactions.clear()
        keepAlives.clear()
    }
    @EventTarget
    fun onWorld(event : WorldEvent){
        msTimer.reset()
        transactions.clear()
        keepAlives.clear()
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        var packet = event.packet.unwrap()
        /*if (packet is CPacketConfirmTransaction && (transactions.size <= 0 || packet != transactions[transactions.size - 1])) {
            LiquidBounce.hud.addNotification(Notification("Disabler","c0f added",NotifyType.INFO))
            transactions.add(packet)
            event.cancelEvent()
        }*/
//        if(mc.thePlayer!!.isOnLadder&&mc.gameSettings.keyBindJump.pressed){
//            mc.thePlayer!!.motionY=0.11
//        }
        if (packet is CPacketKeepAlive && (keepAlives.size <= 0 || packet != keepAlives[keepAlives.size - 1])) {
//            debug("c00 canceled")
            keepAlives.add(packet)
            event.cancelEvent()
        }
        if (packet !is CPacketPlayer || mc.thePlayer!!.ticksExisted % 15 !== 0) return
        try {
            val b = ByteArrayOutputStream()
            val _out = DataOutputStream(b)
            _out.writeUTF(mc.thePlayer!!.gameProfile.getName())
            val buf = classProvider.createPacketBuffer(Unpooled.buffer())
            buf.writeBytes(b.toByteArray())
            mc.netHandler.addToSendQueue(classProvider.createCPacketCustomPayload("aac5:resetFlags", buf))
        } catch (b: IOException) {
        }
        if (packet is CPacketConfirmTransaction) {
            if (Math.abs(
                    (Math.abs((packet.uid).toInt()).toInt() - Math.abs(vulTickCounterUID.toInt())
                        .toInt()).toInt()
                ) <= 4
            ) {
                vulTickCounterUID = (packet.uid).toInt()
                packetBuffer.add(packet)
                event.cancelEvent()
                //debug("C0F-PingTickCounter IN ${packetBuffer.size}")
            } else if (Math.abs((Math.abs((packet.uid).toInt()).toInt() - 25767).toInt()) <= 4) {
                vulTickCounterUID = (packet.uid).toInt()
                //debug("C0F-PingTickCounter RESETED")
            }
        }
        if(classProvider.isCPacketEntityAction(packet)){
            ClientUtils.displayChatMessage("sprint dis")
            val c0B: CPacketEntityAction = packet as CPacketEntityAction

            if (c0B.getAction().equals(ICPacketEntityAction.WAction.START_SPRINTING)) {
                if (mc.thePlayer!!.serverSprintState) {
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketEntityAction(
                            mc.thePlayer!!,
                            ICPacketEntityAction.WAction.STOP_SPRINTING
                        )
                    )
                    mc.thePlayer!!.serverSprintState = false
                }
                event.cancelEvent()
            }

            if (c0B.getAction().equals(ICPacketEntityAction.WAction.STOP_SPRINTING)) {
                event.cancelEvent()
            }
        }
    }
    fun debug(text:String){
        LiquidBounce.hud.addNotification(Notification(text,Notification.Type.INFO))
    }
    @EventTarget
    fun onUpdate(event : UpdateEvent){
        if(lagTimer.hasTimePassed(5000L) && packetBuffer.size > 4) {
            lagTimer.reset()
            while (packetBuffer.size > 4) {
                PacketUtils.sendPacketNoEvent(packetBuffer.poll())
            }
        }
        /*if (msTimer.hasTimePassed(3000L) && keepAlives.size > 0) {

            mc.netHandler.addToSendQueue(classProvider.createCPacketKeepAlive())
            keepAlives.size -1


            debug("c00 no.${keepAlives.size - 1} sent.")
            //debug("c0f no.${transactions.size - 1} sent.")
            keepAlives.clear()
            //transactions.clear()
            msTimer.reset()
        }*/
    }
    override val tag: String?
        get() = "Beta"
}