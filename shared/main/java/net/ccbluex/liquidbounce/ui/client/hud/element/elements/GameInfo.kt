package net.ccbluex.liquidbounce.ui.client.hud.element.elements
//Coarse_KK
import blur.BlurBuffer
import me.utils.ColorUtils2
import me.utils.VisualUtils
import net.ccbluex.liquidbounce.features.module.modules.render.ColorMixer
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder.killCounts
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.Palette
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*


@ElementInfo(name = "GameInfo")
class GameInfo(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private val modeValue = ListValue("Mode", arrayOf("Default", "Shadow", "Old", "LoseLine"), "Default")
    private val blur = BoolValue("Blur", true)
    private val RectA = IntegerValue("RectA", 120, 0, 255)
    private val textredValue = IntegerValue("TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("TextRed", 244, 0, 255)
    private val textblueValue = IntegerValue("TextBlue", 255, 0, 255)
    private val textblueae = IntegerValue("Textalpha", 255, 0, 255)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val redValue = IntegerValue("Text-R", 255, 0, 255)
    private val greenValue = IntegerValue("Text-G", 255, 0, 255)
    private val blueValue = IntegerValue("Text-B", 255, 0, 255)
    private val colorRedValue2 = IntegerValue("Text-R2", 0, 0, 255)
    private val colorGreenValue2 = IntegerValue("Text-G2", 111, 0, 255)
    private val colorBlueValue2 = IntegerValue("Text-B2", 255, 0, 255)
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val newRainbowIndex = IntegerValue("NewRainbowOffset", 1, 1, 50)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoclient = IntegerValue("AstolfoRange", 109, 1, 765)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Fade", "Astolfo", "NewRainbow","Gident"), "Custom")
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)
    private var fontValue = FontValue("Font", Fonts.font35)
    private var GameInfoRows = 0
    private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")



    override fun drawElement(): Border? {
        val icon = Fonts.flux
        val fontHeight = Fonts.font40.fontHeight
        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()
        val barLength1 = (163f).toDouble()
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), 192).rgb
        var Borderx1 = 0
        var Bordery1 = 0
        var Borderx2 = 0
        var Bordery2 = 0

        if (modeValue.get().equals("Default", true)) {
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows* 18 + 12
            Borderx2 +=176
            Bordery2 +=78
            if(blur.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
//            GL11.glPushMatrix()
                BlurBuffer.blurArea(floatX, floatY + 8  , 176F, 70F )
//            GL11.glPopMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 176F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), 0).rgb)
            RenderUtils.drawShadowWithCustomAlpha(0F, 12.5F, 176F, 64F, 255F)
            RenderUtils.drawRect(0F, 11.0F, 176F, 77F , Color(20, 19, 18, 170).rgb)
            fontValue.get().drawStringWithShadow("Session Info", 3, this.GameInfoRows * 18 + 14, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("FPS:" + Minecraft.getDebugFPS(), 7, this.GameInfoRows * 18 + 26, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Ping:" + EntityUtils.getPing(mc2.player).toString(), 7, this.GameInfoRows * 18 + 36, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("X:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posX) + " " + "Y:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posY) + " " + "Z:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posZ), 7, this.GameInfoRows * 18 + 47, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("ServerIP:" + ServerUtils.getRemoteIp(), 7, this.GameInfoRows * 18 + 56, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Kills:" + killCounts, 7, this.GameInfoRows * 18 + 68, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            for (i in 0 until gradientAmountValue.get()) {
                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * barLength1
                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * barLength1
                RenderUtils.drawGradientSideways(
                    8 + barStart  -8 , 10.0, 8 + barEnd + 5, 11.0,
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(redValue.get(), greenValue.get(), blueValue.get()),
                            Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )

                        else -> color
                    },
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(redValue.get(), greenValue.get(), blueValue.get()),
                            Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )
                        else -> color
                    }
                )

            }
        }
        if (modeValue.get().equals("Shadow", true)) {
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows* 18 + 12
            Borderx2 +=176
            Bordery2 +=80
            if(blur.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
//            GL11.glPushMatrix()
                BlurBuffer.blurArea(floatX, floatY + 11  , 176F, 70F )
//            GL11.glPopMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
            }

            fontValue.get().drawStringWithShadow("Session Info", 3, this.GameInfoRows * 18 + 14, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("FPS:" + Minecraft.getDebugFPS(), 7, this.GameInfoRows * 18 + 26, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Ping:" + EntityUtils.getPing(mc2.player).toString(), 7, this.GameInfoRows * 18 + 36, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("X:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posX) + " " + "Y:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posY) + " " + "Z:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posZ), 7, this.GameInfoRows * 18 + 47, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("ServerIP:" + ServerUtils.getRemoteIp(), 7, this.GameInfoRows * 18 + 56, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Kills:" + killCounts, 7, this.GameInfoRows * 18 + 68, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            RenderUtils.drawOutlinedRect(0F, this.GameInfoRows * 18F + 25F, 176F, 80F, 10F,ColorMixer.getMixedColor(3,10).rgb)
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 176F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), 0).rgb)

            RenderUtils.drawShadowWithCustomAlpha(0F, 11.0F, 176F, 70F , 255F)
            Border(0F, this.GameInfoRows * 18F + 12F, 176F, 80F)
        }
        if(modeValue.get().equals("Old", true)){
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows * 18 + 12
            Borderx2 +=150
            Bordery2 +=80
            if(blur.get()){
                GL11.glTranslated(-renderX, -renderY, 0.0)
                BlurBuffer.blurArea(renderX.toFloat(), renderY.toFloat()+8,156F,78F )
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            RenderUtils.drawShadowWithCustomAlpha(0F, 8F, 155F, 76F, 200F)
            RenderUtils.drawRect(0F, 8F, 156F,85F, Color(41, 41, 41, RectA.get()).rgb)
            icon.drawString("c", 47F, 2.5F + fontHeight + 6F, color)
            Fonts.font35.drawStringWithShadow("Session Info", (50F + icon.getStringWidth("u")).toInt(),
                (this.GameInfoRows * 18F + 16).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.font30.drawStringWithShadow("Speed: " + calculateBPS(),
                (5F + icon.getStringWidth("b")).toInt(),
                (this.GameInfoRows * 20F + 30).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.font30.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), (5F + icon.getStringWidth("e")).toInt(),
                (this.GameInfoRows * 20F + 43).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.font30.drawStringWithShadow("Kills: " + killCounts, (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 20F + 54).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.font30.drawStringWithShadow("Played Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}" ,
                (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 20F + 66).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        }
        if(modeValue.get().equals("LoseLine", true)){
            if (blur.get()){
                GL11.glTranslated(-renderX, -renderY, 0.0)
                BlurBuffer.blurArea(floatX, floatY + 10.5F  , 150F, 70F )
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows * 18 + 12
            Borderx2 +=150
            Bordery2 +=80
            RenderUtils.drawShadowWithCustomAlpha(0F, 10.5F, 150F, 70F, 200F)
            RenderUtils.drawRect(0F,11F,150F,12F, ColorUtils2.hslRainbow(10, indexOffset = 30).rgb)
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 12, 150F, this.GameInfoRows * 18F + 25F, Color(0, 0, 0, 100).rgb)
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 150F, 80F, Color(0, 0, 0, 100).rgb)
            icon.drawString("c", 3F, 2.5F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("m", 3F, 15.9F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("f", 3F, 28.5F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("a", 3F, 39.5F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("x", 3F, 52F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("Session Info", (5F + icon.getStringWidth("u")).toInt(),
                (this.GameInfoRows * 18F + 16).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("BPS: " + calculateBPS(),
                (5F + icon.getStringWidth("b")).toInt(),
                (this.GameInfoRows * 18F + 30).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), (5F + icon.getStringWidth("e")).toInt(),
                (this.GameInfoRows * 18F + 43).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("Kills: " +killCounts, (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 18F + 54).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("Played Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}" ,
                (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 18F + 66).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        }
        return   Border(Borderx1.toFloat(), Bordery1.toFloat(), Borderx2.toFloat(), Bordery2.toFloat())
    }
    fun calculateBPS(): Double {
        if(mc.thePlayer != null) {
            val bps = Math.hypot(
                mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX,
                mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ
            ) * mc.timer.timerSpeed * 20
            return Math.round(bps * 100.0) / 100.0
        }else{
            return 0.00;
        }

    }
}
