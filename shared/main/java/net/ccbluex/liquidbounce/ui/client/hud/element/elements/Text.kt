/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.CPSCounter
import net.ccbluex.liquidbounce.utils.Colors
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.extensions.getPing
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.Gui
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text")
class Text(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F,
           side: Side = Side.default()) : Element(x, y, scale, side) {

    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")

        val DECIMAL_FORMAT = DecimalFormat("0.00")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 2.0, y = 2.0, scale = 2F)

            text.displayString.set("%clientName%")
            text.shadow.set(true)
            text.fontValue.set(Fonts.font40)
            text.setColor(Color(0, 111, 255))

            return text
        }

    }

    private val displayString = TextValue("DisplayText", "")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val rainbow = BoolValue("Text-Rainbow", false)
    private val rainbowX = FloatValue("Text-Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Text-Rainbow-Y", -1000F, -2000F, 2000F)
    private val shadow = BoolValue("Shadow", true)
    private var fontValue = FontValue("Font", Fonts.font40)

    private val modeValue = ListValue("Watermark", arrayOf("None", "Top", "Onetap", "Exhibition"), "None")
    private val rainbowWM = BoolValue("Watermark-Rainbow", false)
    private val markRValue = IntegerValue("Watermark-Red", 255, 0, 255)
    private val markGValue = IntegerValue("Watermark-Green", 255, 0, 255)
    private val markBValue = IntegerValue("Watermark-Blue", 255, 0, 255)
    private val markR2Value = IntegerValue("Watermark-Red2", 255, 0, 255)
    private val markG2Value = IntegerValue("Watermark-Green2", 255, 0, 255)
    private val markB2Value = IntegerValue("Watermark-Blue2", 255, 0, 255)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "Text Element"
            else
                displayString.get()


            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        val thePlayer = mc.thePlayer

        if (thePlayer != null) {
            when (str.toLowerCase()) {
                "x" -> return DECIMAL_FORMAT.format(thePlayer.posX)
                "y" -> return DECIMAL_FORMAT.format(thePlayer.posY)
                "z" -> return DECIMAL_FORMAT.format(thePlayer.posZ)
                "xdp" -> return thePlayer.posX.toString()
                "ydp" -> return thePlayer.posY.toString()
                "zdp" -> return thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(thePlayer.motionX * thePlayer.motionX + thePlayer.motionZ * thePlayer.motionZ))
                "ping" -> return thePlayer.getPing().toString()
                "health" -> return DECIMAL_FORMAT.format(thePlayer.health)
                "maxhealth" -> return DECIMAL_FORMAT.format(thePlayer.maxHealth)
                "food" -> return thePlayer.foodStats.foodLevel.toString()
            }
        }

        return when (str.toLowerCase()) {
            "username" -> mc.session.username
            "clientname" -> LiquidBounce.CLIENT_NAME
            "clientversion" -> "b${LiquidBounce.CLIENT_VERSION}"
            "clientcreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> mc.debugFPS.toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb

        val fontRenderer = fontValue.get()

        val rainbow = rainbow.get()

        val mwc1 = Color(this.markRValue.get(),this.markGValue.get(),this.markBValue.get()).rgb
        val mwc2 = Color(this.markR2Value.get(),this.markG2Value.get(),this.markB2Value.get()).rgb
        if (this.modeValue.get().equals("Top")) {
            Gui.drawRect(-2,-2, fontRenderer.getStringWidth(displayText) + 2, fontRenderer.fontHeight, Color(0,0,0,100).rgb)
            RenderUtils.drawGradientSideways(-2.0,-3.0,
                (fontRenderer.getStringWidth(displayText) + 2).toDouble(), -2.0, mwc1, mwc2)
        } else if (this.modeValue.get().equals("Onetap")) {
            Gui.drawRect(-2,-5, fontRenderer.getStringWidth(displayText) + 2, fontRenderer.fontHeight, Color(0,0,0,90).rgb)
            RenderUtils.drawGradientSideways(-1.0,-2.0,
                (fontRenderer.getStringWidth(displayText) + 1).toDouble(), -4.0, mwc1, mwc2)
        }

        if (this.modeValue.get().equals("Exhibition")) {
            fontRenderer.drawString(displayText.substring(0, 1), 0F, 0F,
                if (rainbow) Colors.getRainbow(-2000, 0) else Color(this.markRValue.get(),this.markGValue.get(),this.markBValue.get()).rgb, shadow.get())

            if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText) + 2F,
                    0F, -1, shadow.get())
            fontRenderer.drawString(displayText.substring(1), fontRenderer.getStringWidth(displayText.substring(0, 1)).toFloat(), 0F, color, shadow.get())
        } else {
            RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                fontRenderer.drawString(displayText, 0F, 0F, if (rainbow)
                    0 else color, shadow.get())

                if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                    fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText) + 2F,
                        0F, if (rainbow) ColorUtils.rainbow(400000000L).rgb else color, shadow.get())
            }
        }

        if (editMode && !classProvider.isGuiHudDesigner(mc.currentScreen)) {
            editMode = false
            updateElement()
        }
        if (this.modeValue.get().equals("Outline")) {
            return Border(
                -2.5F,
                -2.5F,
                fontRenderer.getStringWidth(displayText) + 2.5F,
                fontRenderer.fontHeight.toFloat() + 0.5F
            )
        } else {
            return Border(
                -2F,
                if (this.modeValue.get().equals("Top")) -4F else -2F,
                fontRenderer.getStringWidth(displayText) + 2F,
                fontRenderer.fontHeight.toFloat()
            )
        }
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ColorUtils.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }

}
