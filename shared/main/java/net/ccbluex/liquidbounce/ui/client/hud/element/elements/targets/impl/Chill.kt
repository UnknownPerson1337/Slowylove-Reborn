/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

//import net.ccbluex.liquidbounce.utils.extensions.darker
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.utils.CharRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.darker
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

class Chill(inst: Target): TargetStyle("Chill", inst, true) {

    val chillFontSpeed = FloatValue("Chill-FontSpeed", 0.5F, 0.01F, 1F)
    val chillRoundValue = BoolValue("Chill-RoundedBar", true)

    private val numberRenderer = CharRenderer(false)

    private var calcScaleX = 0F
    private var calcScaleY = 0F
    private var calcTranslateX = 0F
    private var calcTranslateY = 0F

    fun updateData(_a: Float, _b: Float, _c: Float, _d: Float) {
        calcTranslateX = _a
        calcTranslateY = _b
        calcScaleX = _c
        calcScaleY = _d
    }

    override fun drawTarget(entity: IEntityLivingBase) {
        updateAnim(entity.health)

        val name = entity.name
        val health = entity.health
        val tWidth = (45F + Fonts.font40.getStringWidth(name!!).coerceAtLeast(Fonts.fontBold72.getStringWidth(decimalFormat.format(health)))).coerceAtLeast(120F)
        val playerInfo = mc.netHandler.getPlayerInfo(entity.uniqueID)

        // background
        RenderUtils.drawRoundedRect(0F, 0F, tWidth, 48F, 7, targetInstance.bgColor.rgb)
        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)
        
        // head
        if (playerInfo != null) {
            Stencil.write(false)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderUtils.fastRoundedRect(4F, 4F, 34F, 34F, 7F)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            Stencil.erase(true)
            drawHead(playerInfo.locationSkin, 4, 4, 30, 30, 1F - targetInstance.getFadeProgress())
            Stencil.dispose()
        }

        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)

        // name + health
        Fonts.font40.drawString(name, 38F, 6F, getColor(-1).rgb)
        numberRenderer.renderChar(health, calcTranslateX, calcTranslateY, 38F, 17F, calcScaleX, calcScaleY, false, chillFontSpeed.get(), getColor(-1).rgb)

        // health bar
        RenderUtils.drawRoundedRect(4F, 38F, tWidth - 4F, 44F, 3, targetInstance.barColor.darker(0.5F).rgb)

        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.fastRoundedRect(4F, 38F, tWidth - 4F, 44F, 3F)
        GL11.glDisable(GL11.GL_BLEND)
        Stencil.erase(true)
        if (chillRoundValue.get())
            RenderUtils.customRounded(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, 0F, 3F, 3F, 0F, targetInstance.barColor.rgb)
        else
            RenderUtils.drawRect(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, targetInstance.barColor.rgb)
        Stencil.dispose()
    }

    override fun handleBlur(player: IEntityPlayer) {
        val tWidth = (45F + Fonts.font40.getStringWidth(player.name!!).coerceAtLeast(Fonts.fontBold72.getStringWidth(decimalFormat.format(player.health)))).coerceAtLeast(120F)
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.fastRoundedRect(0F, 0F, tWidth, 48F, 7F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun handleShadowCut(player: IEntityPlayer) = handleBlur(player)

    override fun handleShadow(player: IEntityPlayer) {
        val tWidth = (45F + Fonts.font40.getStringWidth(player.name!!).coerceAtLeast(Fonts.fontBold72.getStringWidth(decimalFormat.format(player.health)))).coerceAtLeast(120F)
        RenderUtils.drawRoundedRect(0F, 0F, tWidth, 48F, 7, shadowOpaque.rgb)
    }

    override fun getBorder(entity: IEntityLivingBase?): Border? {
        entity ?: return Border(0F, 0F, 120F, 48F)
        val tWidth = (45F + Fonts.font40.getStringWidth(entity.name!!).coerceAtLeast(Fonts.fontBold72.getStringWidth(decimalFormat.format(entity.health)))).coerceAtLeast(120F)
        return Border(0F, 0F, tWidth, 48F)
    }

}