package net.ccbluex.liquidbounce.features.module.modules.render

//import oh.yalan.NativeClass
import me.utils.ColorUtils2
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import java.awt.Color


//@NativeClass
@ModuleInfo(name = "Logo",description=":)", category = ModuleCategory.RENDER)
class Logo : Module() {

    private val info = TextValue("Info", "im legit")
    private val Clientname = TextValue("Clientname",LiquidBounce.CLIENT_NAME)
    private val colorModeValue = ListValue("Color", arrayOf("Custom","novo","rainbow","skyrainbow","anptherrainbow"), "Custom")
    private val RedValue = IntegerValue("R", 255, 0, 255)
    private val GreenValue = IntegerValue("G", 255, 0, 255)
    private val BlueValue = IntegerValue("B", 255, 0, 255)
    private val AlphaValue = IntegerValue("A", 255,0,255)
    private val rainbowSpeed = IntegerValue("RainbowSpeed", 10, 1, 10)
    private val rainbowIndex = IntegerValue("RainbowIndex", 1, 1, 20)





    @EventTarget
    fun onRender(event: Render2DEvent){

        val TextColor = when (colorModeValue.get().toLowerCase()) {
            "novo" -> ColorUtils2.novoRainbow(40).rgb
            "rainbow" -> ColorUtils2.hslRainbow(rainbowIndex.get(), indexOffset = 100 * rainbowSpeed.get()).rgb
            "skyrainbow" -> ColorUtils2.skyRainbow(rainbowIndex.get(), 1F, 1F)!!.rgb
            "anotherrainbow" -> ColorUtils2.fade(Color(RedValue.get(), GreenValue.get(), BlueValue.get(), AlphaValue.get()), 100, rainbowIndex.get()).rgb
//            "godlightsync"->ColorUtils2.GodLight(40).rgb
            else -> Color(RedValue.get(), GreenValue.get(), BlueValue.get(), 1).rgb
        }
//        RenderUtils.drawJelloShadow(10f,15f, 720f,400f
//        )
//        RenderUtils.drawImage(classProvider.createResourceLocation("liquidbounce/shadow/shadow.png"),
//            ((xPos - if (rectMode.equals("right", true)) 3 else 0) - 20).toInt(),( (module.higt + textY) - 10).toInt(),fontRenderer.getStringWidth(displayString) + 45, fontRenderer.fontHeight + 10
//        )
        Fonts.bold72.drawStringWithShadow(Clientname.get(),10,15, TextColor)
//        Fonts.Light35.drawStringWithShadow(LiquidBounce.CLIENT_VERSION.toString(),Fonts.Light35.getStringWidth(LiquidBounce.CLIENT_NAME) + 13, 14,TextColor)
//        Fonts.Light35.drawStringWithShadow(info.get(),Fonts.Light35.getStringWidth(LiquidBounce.CLIENT_NAME) + 13, 24,TextColor)
    }

}
