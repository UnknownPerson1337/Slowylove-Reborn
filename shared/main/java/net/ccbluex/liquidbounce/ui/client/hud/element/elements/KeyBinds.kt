package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/*
dont look shitcode bruhhhhhh
 */

@ElementInfo(name = "KeyBinds")
class KeyBinds : Element() {

    private val decimalFormat3 = DecimalFormat("0.#", DecimalFormatSymbols(Locale.ENGLISH))
    private val fontOffsetValue = FloatValue("Font-Offset", 0F, 3F, -3F)
    private val reverseValue = BoolValue("Reverse", false)
    private val fontValue = FontValue("Font", Fonts.font35)
    private val shadowValue = BoolValue("Shadow", false)
    private val lineValue = BoolValue("Line", true)
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 103, 0, 255)
    private val bgredValue = IntegerValue("Background-Red", 255, 0, 255)
    private val bggreenValue = IntegerValue("Background-Green", 255, 0, 255)
    private val bgblueValue = IntegerValue("Background-Blue", 255, 0, 255)
    private val bgalphaValue = IntegerValue("Background-Alpha", 120, 0, 255)
    private var GameInfoRows = 0

    override fun drawElement(): Border {

        val reverse = reverseValue.get()
        val font = fontValue.get()
        val fontOffset = fontOffsetValue.get()
        var modules = 0
        val bindM: ArrayList<Module> = ArrayList<Module>()



        for (m : Module in LiquidBounce.moduleManager.modules){
            if(m.keyBind != 0){
                bindM.add(m)
                modules++
            }
        }
        var y2 = this.GameInfoRows * 18+16+20
//sb dis shi shen me

        RenderUtils.drawRoundedRect(0F, this.GameInfoRows * 18F + 12, 176F, this.GameInfoRows * 18F + 25F,
            5, Color(redValue.get(), greenValue.get(), blueValue.get(), bgalphaValue.get()).rgb)
        font.drawStringWithShadow("KeyBinds",7,this.GameInfoRows * 18 + 16,Color(255,255,255,255).rgb)
               for (m : Module in bindM){
            font.drawStringWithShadow(m.name,6,y2, Color(redValue.get(),greenValue.get(),blueValue.get(),alphaValue.get()).rgb)
            font.drawStringWithShadow(Keyboard.getKeyName(m.keyBind),100 - font.getStringWidth(m.keyBind.toString())  ,y2,Color(redValue.get(),greenValue.get(),blueValue.get(),alphaValue.get()).rgb)
            y2 += font.fontHeight + 2;

        }
        RenderUtils.drawRoundedRect(0F,this.GameInfoRows * 18F + 30F,176F,y2.toFloat(),
            5,Color(bgredValue.get(),bggreenValue.get(),bgblueValue.get(),bgalphaValue.get()).rgb)


        return Border(0F, this.GameInfoRows * 18F + 12F, 176F, 80F)
    }
}