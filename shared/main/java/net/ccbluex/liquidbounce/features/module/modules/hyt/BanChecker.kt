package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.minecraft.network.play.server.SPacketChat
import java.util.regex.Pattern


/**
 *
 * Skid by Paimon.
 * @Date 2022/12/10
 */
@ModuleInfo(name = "BanChecker", description = "Hyt", category = ModuleCategory.HYT)
class BanChecker : Module(){

    var ban = 0
    @EventTarget
    fun onPacket(event : PacketEvent){
        val packet = event.packet.unwrap()
        if(packet is SPacketChat){
            val matcher = Pattern.compile("玩家(.*?)在本局游戏中行为异常").matcher(packet.chatComponent.unformattedText)
            if(matcher.find()){
                ban ++
                val banname = matcher.group(1)
                LiquidBounce.hud.addNotification(Notification("$banname was banned. ("+ban.toString()+"/114514)",Notification.Type.WARNING))
            }
        }
    }
}