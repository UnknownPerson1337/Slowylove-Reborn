/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
class HUD : Module() {
    val blackHotbarValue = BoolValue("BlackHotbar", true)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val fontChatValue = BoolValue("FontChat", false)

    val hueInterpolation = BoolValue("Hue Interpolate", false)
    public val Radius = FloatValue("HudBlurRadius", 20f, 1f, 30f)
    public val rainbowStartValue = FloatValue("RainbowStart", 0.41f, 0f, 1f)
    public val rainbowStopValue = FloatValue("RainbowStop", 0.58f, 0f, 1f)

    public val rainbowSaturationValue = FloatValue("RainbowSaturation", 0.7f, 0f, 1f)
    public val rainbowBrightnessValue = FloatValue("RainbowBrightness", 1f, 0f, 1f)
    public val rainbowSpeedValue = IntegerValue("RainbowSpeed", 1500, 500, 7000)
    val r = IntegerValue("Red", 255, 0, 255)
    val g = IntegerValue("Green", 255, 0, 255)
    val b = IntegerValue("Blue", 255, 0, 255)

    val r2 = IntegerValue("Red2", 255, 0, 255)
    val g2 = IntegerValue("Green2", 255, 0, 255)
    val b2 = IntegerValue("Blue2", 255, 0, 255)

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return

        LiquidBounce.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
                !(classProvider.isGuiChat(event.guiScreen) || classProvider.isGuiHudDesigner(event.guiScreen))) mc.entityRenderer.loadShader(classProvider.createResourceLocation("liquidbounce" + "/blur.json")) else if (mc.entityRenderer.shaderGroup != null &&
                mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("liquidbounce/blur.json")) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}