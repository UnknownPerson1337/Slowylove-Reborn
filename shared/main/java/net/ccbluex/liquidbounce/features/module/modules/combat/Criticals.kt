/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "Criticals",description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT)
class Criticals : Module() {

    val modeValue = ListValue("Mode", arrayOf("SB","FW","NMSL","NewPacket","Packet", "NcpPacket", "AAC4" ,"NoGround", "Visual" ,"Hop", "TPHop", "AAC4ByHyt" ,"HytSpartan","Jump" ,"LowJump","SpartanSemi"), "packet")
    val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    val packet = ListValue("PacketMode" , arrayOf("C04","C06"),"C04")
    private val debugValue = BoolValue("DebugMessage", false)

    val msTimer = MSTimer()
    var n = 0
    var attacks = 0
    private var target = 0

    override fun onEnable() {
        if (modeValue.get().equals("NoGround", ignoreCase = true)) mc.thePlayer!!.jump()
        attacks = 0
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            val thePlayer = mc.thePlayer ?: return
            val entity = event.targetEntity!!.asEntityLivingBase()
            target = entity.entityId

            if (!thePlayer.onGround || thePlayer.isOnLadder || thePlayer.isInWeb || thePlayer.isInWater ||
                thePlayer.isInLava || thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                LiquidBounce.moduleManager[Fly::class.java]!!.state || !msTimer.hasTimePassed(delayValue.get().toLong()))
                return

            val x = thePlayer.posX
            val y = thePlayer.posY
            val z = thePlayer.posZ

            fun sendCriticalPacket(xOffset: Double = 0.0, yOffset: Double = 0.0, zOffset: Double = 0.0, ground: Boolean) {
                val posX = thePlayer.posX + xOffset
                val posY = thePlayer.posY + yOffset
                val posZ = thePlayer.posZ + zOffset
                if (packet.get() == "C06") {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(posX, posY, posZ, thePlayer.rotationYaw, thePlayer.rotationPitch, ground))
                } else {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(posX, posY, posZ, ground))
                }
            }

            when (modeValue.get().toLowerCase()) {
                "newpacket" -> {
                    sendCriticalPacket(yOffset = 0.05250000001304, ground = true)
                    sendCriticalPacket(yOffset = 0.00150000001304, ground = false)
                    sendCriticalPacket(yOffset = 0.01400000001304, ground = false)
                    sendCriticalPacket(yOffset = 0.00150000001304, ground = false)
                }
                "packet" -> {
                    sendCriticalPacket(yOffset = 0.0625, ground = true)
                    sendCriticalPacket(ground = false)
                    sendCriticalPacket(yOffset = 1.1E-5, ground = false)
                    sendCriticalPacket(ground = false)
                }

                "aac4byhyt" ->{
                    attacks++
                    if (attacks > 5) {
                        sendCriticalPacket(yOffset = 0.0114514, ground = false)
                        sendCriticalPacket(yOffset = 0.0019 ,ground = false)
                        sendCriticalPacket(yOffset = 0.000001 ,ground = false)
                        attacks = 0
                    }
                }
                "sb" ->{

                    attacks++
                    if (this.attacks >= 5){

                    }else {
//                    if (attacks > 3) {
                        val sbnmsl = 0.015659921803854882
//                        val sbnmsl = Random().nextDouble() / 10
                        ClientUtils.displayChatMessage("§6Send-§f" + (LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura).target!!.name + ",§7" + sbnmsl.toString())
                        sendCriticalPacket(yOffset = sbnmsl, ground = false)
//                        sendCriticalPacket(yOffset = 0.0019 ,ground = false)
//                        sendCriticalPacket(yOffset = 0.000001 ,ground = false)
                        attacks = 0
//                    }
                    }
                }
                "fw" ->{
                    attacks++
                    if (this.attacks >= 5){

                    }else {
//                    attacks++
//                    if (attacks > 3) {
                    val sbnmsl = 0.05123213776456501
//                        val sbnmsl = Random().nextDouble() / 10
                    ClientUtils.displayChatMessage("§6Send-§f"+(LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura).target!!.name+",§7"+sbnmsl.toString())
                    sendCriticalPacket(yOffset = sbnmsl, ground = false)
//                        sendCriticalPacket(yOffset = 0.0019 ,ground = false)
//                        sendCriticalPacket(yOffset = 0.000001 ,ground = false)
                        attacks = 0
                    }
                }
                "nmsl" ->{
                    attacks++
                    if (this.attacks >= 5){

                    }else {
//                    attacks++
//                    if (attacks > 3) {
                    val sbnmsl = 0.04913150905855286
//                        val sbnmsl = Random().nextDouble() / 10
                    ClientUtils.displayChatMessage("§6Send-§f"+(LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura).target!!.name+",§7"+sbnmsl.toString())
                    sendCriticalPacket(yOffset = sbnmsl, ground = false)
//                        sendCriticalPacket(yOffset = 0.0019 ,ground = false)
//                        sendCriticalPacket(yOffset = 0.000001 ,ground = false)
                        attacks = 0
                    }
                }
                "aac4" ->{
                    sendCriticalPacket(yOffset = 0.00000000000002593, ground = false)
                    sendCriticalPacket(yOffset = 0.01400000001304, ground = false)
                    sendCriticalPacket(yOffset = 0.0012016413, ground = false)
                }

                "hytspartan" -> {
                    n = this.attacks;
                    this.attacks = n + 1;
                    if (this.attacks <= 6){

                    }else{
                        mc.netHandler.addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerPosition(x, y + 0.01, z, false));
                        mc.netHandler.addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerPosition(x, y + 1.0E-10, z, false));
                        mc.netHandler.addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerPosition(x, y + 0.114514, z, false));
                        this.attacks = 0;
                    }
                }
                "spartansemi" -> {
                    attacks++
                    if (attacks > 6) {
                        sendCriticalPacket(yOffset = 0.01, ground = false)
                        sendCriticalPacket(yOffset = 0.0000000001, ground = false)
                        sendCriticalPacket(yOffset = 0.114514, ground = false)
                        attacks = 0
                    }
                }

                "ncppacket" -> {
                    sendCriticalPacket(yOffset = 0.11, ground = false)
                    sendCriticalPacket(yOffset = 0.1100013579, ground = false)
                    sendCriticalPacket(yOffset = 0.0000013579, ground = false)
                }

                "hop" -> {
                    thePlayer.motionY = 0.1
                    thePlayer.fallDistance = 0.1f
                    thePlayer.onGround = false
                }

                "tphop" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.02, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.01, z, false))
                    thePlayer.setPosition(x, y + 0.01, z)
                }
                "jump" -> thePlayer.jump()
                "visual" -> thePlayer.onCriticalHit(entity)
                "lowjump" -> thePlayer.motionY = 0.3425
            }

            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (classProvider.isCPacketPlayer(packet) && modeValue.get().equals("NoGround", ignoreCase = true))
            packet.asCPacketPlayer().onGround = false

        if (classProvider.isSPacketAnimation(packet) && debugValue.get()) {
            if (packet.asSPacketAnimation().animationType == 4 && packet.asSPacketAnimation().entityID == target) {
                val name = (LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura).target!!.name
                ClientUtils.displayChatMessage("§7[§8§6Slowylove§7]§f触发§c暴击§b(§6玩家:§a$name)")
            }
        }
    }

    override val tag: String?
        get() = modeValue.get()
}
