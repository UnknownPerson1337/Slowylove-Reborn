/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.init.MobEffects
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect

@ModuleInfo(name = "SuperKnockback", description = "Increases knockback dealt to other entities.", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            // 获取实体
            val entity = event.targetEntity
            val itemStack = mc.thePlayer!!.inventory.getCurrentItemInHand()
// 计算击退力度，这里取决于攻击者的攻击力


// 为实体增加击退效果

            if(classProvider.isEntityPlayer(entity)){
                if(classProvider.isItemSword(itemStack!!.item!!)){
                    val knockbackStrength = itemStack!!.item!!.asItemSword().damageVsEntity

// 计算击退持续时间，这里假设为1秒
                    val knockbackDuration = 20
                    ClientUtils.displayChatMessage("§6add-§c"+ entity!!.asEntityPlayer().name+" , §7"+ knockbackStrength + " §f(Range:§b"+mc.thePlayer!!.getDistanceSqToEntity(entity.asEntityPlayer()))
                    entity!!.asEntityPlayer().addPotionEffect(classProvider.createPotionEffect(classProvider.getPotionEnum(PotionType.MOVE_SLOWDOWN).id, knockbackDuration,
                        knockbackStrength.toInt()
                    ))
                    ClientUtils.displayChatMessage("§6end-§c"+ entity!!.asEntityPlayer().name+" , §7"+ knockbackStrength + " §f(Range:§b"+mc.thePlayer!!.getDistanceSqToEntity(entity.asEntityPlayer()))
                }

            }


            if (event.targetEntity!!.asEntityLivingBase().hurtTime > hurtTimeValue.get())
                return

            val player = mc.thePlayer ?: return

            if (player.sprinting)
                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.STOP_SPRINTING))

            mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.START_SPRINTING))
            mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.STOP_SPRINTING))
            mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.START_SPRINTING))
            player.sprinting = true
            player.serverSprintState = true
        }
    }

}