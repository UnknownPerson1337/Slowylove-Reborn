package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketChat
import net.minecraft.network.play.server.SPacketOpenWindow
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule

@ModuleInfo(name = "AutoGG", category = ModuleCategory.HYT,description = "idk")
class AutoPlay : Module() {
    private var clickState = 0
    private val modeValue = ListValue("Server", arrayOf("RedeSky", "Minemora", "HuaYuTingGG","HuaYuTingSw","HuaYuTing16"), "HuaYuTingGG")
    private val delayValue = IntegerValue("JoinDelay", 3, 0, 7)

    private var clicking = false
    private var queued = false
    override fun onEnable() {
        clickState = 0
        clicking = false
        queued = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()

        when (modeValue.get().toLowerCase()) {
            "redesky" -> {
                if (clicking && (packet is CPacketClickWindow || packet is CPacketPlayerDigging)) {
                    event.cancelEvent()
                    return
                }
                if (clickState == 2 && packet is SPacketOpenWindow) {
                    event.cancelEvent()
                }
            }
            "hypixel" -> {
                if (clickState == 1 && packet is SPacketOpenWindow) {
                    event.cancelEvent()
                }
            }
        }

        if (packet is SPacketChat) {
            val text = packet.chatComponent.unformattedText
            when (modeValue.get().toLowerCase()) {
                "minemora" -> {
                    if (text.contains("Has click en alguna de las siguientes opciones", true)) {
                        queueAutoPlay {
                            mc.thePlayer!!.sendChatMessage("/join")
                        }
                    }
                }
                "huayutinggg" -> {
                    if (text.contains("      喜欢      一般      不喜欢", true)) {
                        LiquidBounce.hud.addNotification(Notification("Game Over", Notification.Type.INFO))
                        mc.thePlayer!!.sendChatMessage("@["+LiquidBounce.CLIENT_NAME+"]GG")
//                        MiscUtils().playSound(MiscUtils.SoundType.VICTORY, -8f)

                        Recorder.totalPlayed++
                    }
                }
                "huayutingsw" -> {

                    val matcher = Pattern.compile("你在地图 (.*?)\\(").matcher(packet.chatComponent.unformattedText)
                    if (text.contains("你现在是观察者状态. 按E打开菜单.", true)) {
                        LiquidBounce.hud.addNotification(Notification("Game Over", Notification.Type.INFO))
                        mc.thePlayer!!.sendChatMessage("@["+LiquidBounce.CLIENT_NAME+"]GG")
//                        MiscUtils().playSound(MiscUtils.SoundType.VICTORY, -8f)

                        Recorder.totalPlayed++
                    }
                }
                "huayuting16" -> {
                    if (text.contains("[起床战争] Game 结束！感谢您的参与！", true)) {
                        LiquidBounce.hud.addNotification(Notification("Game Over", Notification.Type.INFO))
                        mc.thePlayer!!.sendChatMessage("@["+LiquidBounce.CLIENT_NAME+"]GG")
//                        MiscUtils().playSound(MiscUtils.SoundType.VICTORY, -8f)

                        Recorder.totalPlayed++
                    }
                }
            }
        }
    }

    private fun queueAutoPlay(runnable: () -> Unit) {
        if (queued) {
            return
        }
        queued = true
        if (this.state) {
            Timer().schedule(delayValue.get().toLong() * 1000) {
                if (state) {
                    runnable()
                }
            }

            //play sound when everything done
//            MiscUtils().playSound(MiscUtils.SoundType.VICTORY, -8f)
            LiquidBounce.hud.addNotification(Notification("Sending you to next game in ${delayValue.get()}s...", Notification.Type.INFO))
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clicking = false
        clickState = 0
        queued = false
    }

    override val tag: String
        get() = modeValue.get()

    override fun handleEvents() = true
}
