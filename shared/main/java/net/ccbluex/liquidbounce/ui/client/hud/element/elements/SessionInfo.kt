package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import blur.BlurBuffer
import blur.StencilUtil
import me.font.FontLoaders
import me.utils.DrRenderUtils
import me.utils.RoundedUtil
import net.ccbluex.liquidbounce.LiquidBounce
//import net.ccbluex.liquidbounce.features.module.modules.gui.SessionStats
import net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
//import net.ccbluex.liquidbounce.font.FontLoaders
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder.killCounts
import net.ccbluex.liquidbounce.ui.font.Fonts
//import net.ccbluex.liquidbounce.utils.blur.BlurBuffer
import net.ccbluex.liquidbounce.utils.render.RenderUtils
//import net.ccbluex.liquidbounce.utils.tenacity.render.DrRenderUtils
//import net.ccbluex.liquidbounce.utils.tenacity.render.RoundedUtil
//import net.ccbluex.liquidbounce.utils.tenacity.render.StencilUtil
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import java.awt.Color


@ElementInfo(name = "Session Info")
class SessionInfo(
    x: Double = 15.0, y: Double = 10.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)
) : Element(x, y, scale, side) {

    var startTime = System.currentTimeMillis()
    var endTime:kotlin.Long = -1
//额额 
    private val modeValue = ListValue("Mode", arrayOf("Normal", "Tenacity"), "Tenacity")
    private val blur = BoolValue("Blur", false)
    private val Shadow = BoolValue("Normal-Shadow", true)
    private val radiusValue = FloatValue("Normal-Radius", 0f, 0f, 10f)
    private val bgredValue = IntegerValue("Normal-Bg-R", 0, 0, 255)
    private val bggreenValue = IntegerValue("Normal-Bg-G", 0, 0, 255)
    private val bgblueValue = IntegerValue("Normal-Bg-B", 0, 0, 255)
    private val bgalphaValue = IntegerValue("Normal-Bg-Alpha", 0, 0, 255)
    private val outline = BoolValue("Normal-Outline", false)


    // Tenacity
    private val linesLeft = listOf("Play time", "Games played", "Kills")

    private val animatedPlaytime = BoolValue("Tenacity-Animated counter", true)
    val colorMode =
        ListValue("Tenacity-Color", arrayOf("Sync", "Analogous", "Tenacity", "Gradient", "Modern"), "Tenacity")
    private val degree = ListValue("Tenacity-Degree", arrayOf("30", "-30"), "-30")


    private var playtimeWidth = 20.5f

    private var gradientColor1 = Color.WHITE
    private var gradientColor2 = Color.WHITE
    private var gradientColor3 = Color.WHITE
    private var gradientColor4 = Color.WHITE


    val fontValue = FontValue("Font", Fonts.font35)


    // Tenacity methods
    var hudMod: HUD? = LiquidBounce.moduleManager.getModule(
        HUD::class.java
    ) as HUD

    fun getClientColor(): Color {
        return Color(hudMod!!.r.get(), hudMod!!.g.get(), hudMod!!.b.get())
    }

    fun getAlternateClientColor(): Color {
        return Color(hudMod!!.r2.get(), hudMod!!.g2.get(), hudMod!!.b2.get())
    }

    //Animation values for going up and down with the time
    private var hourYAnimation = 0f
    private var minuteYAnimation1 = 0f
    private var minuteYAnimation2 = 0f
    private var secondYAnimation2 = 0f
    private var secondYAnimation1 = 0f

    //Animation values for going left or right based on the width of the other charchter
    private var secondsSeparateWidthAnim1 = 0f
    private var secondsSeparateWidthAnim2 = 0f
    private var minuteSeparateWidthAnim1 = 0f
    private var minuteSeparateWidthAnim2 = 0f

    private fun drawAnimatedPlaytime(playtimeX: Float, y: Float) {
        val playTime = getPlayTime()
        RoundedUtil.drawRoundOutline(
            playtimeX,
            y + 21,
            playtimeWidth,
            13f,
            6f,
            .5f,
            DrRenderUtils.applyOpacity(Color.WHITE, 0f),
            Color.WHITE
        )
        StencilUtil.initStencilToWrite()
        RoundedUtil.drawRound(playtimeX, y + 22, playtimeWidth, 11f, 6f, Color(30, 30, 30))
        StencilUtil.readStencilBuffer(1)
        var secondX = playtimeX + playtimeWidth - 7
        FontLoaders.F18.drawString("s", secondX, y + 24, -1)
        val secondsFirstPlace = playTime[2] % 10
        secondYAnimation2 =
            DrRenderUtils.animate((20 * secondsFirstPlace).toDouble(), secondYAnimation2.toDouble(), .02).toFloat()
        secondsSeparateWidthAnim1 = DrRenderUtils.animate(
            FontLoaders.F18.getStringWidth(secondsFirstPlace.toString()).toDouble(),
            secondsSeparateWidthAnim1.toDouble(),
            .05
        ).toFloat()
        secondX -= (secondsSeparateWidthAnim1 + .5).toFloat()
        for (i in 0..9) {
            FontLoaders.F18.drawString(i.toString(), secondX, y + 24 + i * 20 - secondYAnimation2, -1)
        }
        val secondsSecondPlace = Math.floorDiv(playTime[2], 10)
        secondYAnimation1 =
            DrRenderUtils.animate((20 * secondsSecondPlace).toDouble(), secondYAnimation1.toDouble(), .02).toFloat()
        secondsSeparateWidthAnim2 = DrRenderUtils.animate(
            FontLoaders.F18.getStringWidth(secondsSecondPlace.toString()).toDouble(),
            secondsSeparateWidthAnim2.toDouble(),
            .05
        ).toFloat()
        secondX -= (secondsSeparateWidthAnim2 + .5).toFloat()
        for (i in 0..9) {
            FontLoaders.F18.drawString(i.toString(), secondX, y + 24 + i * 20 - secondYAnimation1, -1)
        }
        if (playTime[1] > 0) {
            var minuteX = playtimeX + playtimeWidth - 27
            FontLoaders.F18.drawString("m", minuteX, y + 24, -1)
            val minuteFirstPlace = playTime[1] % 10
            minuteYAnimation1 =
                DrRenderUtils.animate((20 * minuteFirstPlace).toDouble(), minuteYAnimation1.toDouble(), .02).toFloat()
            minuteSeparateWidthAnim1 = DrRenderUtils.animate(
                FontLoaders.F18.getStringWidth(minuteFirstPlace.toString()).toDouble(),
                minuteSeparateWidthAnim1.toDouble(),
                .05
            ).toFloat()
            minuteX -= (minuteSeparateWidthAnim1 + .5).toFloat()
            for (i in 0..9) {
                FontLoaders.F18.drawString(i.toString(), minuteX, y + 24 + i * 20 - minuteYAnimation1, -1)
            }
            val minuteSecondPlace = Math.floorDiv(playTime[1], 10)
            minuteYAnimation2 =
                DrRenderUtils.animate((20 * minuteSecondPlace).toDouble(), minuteYAnimation2.toDouble(), .02).toFloat()
            minuteSeparateWidthAnim2 = DrRenderUtils.animate(
                FontLoaders.F18.getStringWidth(minuteSecondPlace.toString()).toDouble(),
                minuteSeparateWidthAnim2.toDouble(),
                .05
            ).toFloat()
            minuteX -= (minuteSeparateWidthAnim2 + .5).toFloat()
            for (i in 0..9) {
                FontLoaders.F18.drawString(i.toString(), minuteX, y + 24 + i * 20 - minuteYAnimation2, -1)
            }
            if (playTime[0] > 0) {
                hourYAnimation =
                    DrRenderUtils.animate((20 * (playTime[0] % 10)).toDouble(), hourYAnimation.toDouble(), .02)
                        .toFloat()
                FontLoaders.F18.drawString("h", playtimeX + playtimeWidth - 44, y + 24, -1)
                for (i in 0..9) {
                    FontLoaders.F18.drawString(
                        i.toString(),
                        playtimeX + playtimeWidth - 49,
                        y + 24 + i * 20 - hourYAnimation,
                        -1
                    )
                }
            }
        }
        StencilUtil.uninitStencilBuffer()
    }

    private fun getPlayTime(): IntArray {
        val diff = getTimeDiff()
        var diffSeconds: Long = 0
        var diffMinutes: Long = 0
        var diffHours: Long = 0
        if (diff > 0) {
            diffSeconds = diff / 1000 % 60
            diffMinutes = diff / (60 * 1000) % 60
            diffHours = diff / (60 * 60 * 1000) % 24
        }
        /* String str = (int) diffSeconds + "s";
        if (diffMinutes > 0) str = (int) diffMinutes + "m " + str;
        if (diffHours > 0) str = (int) diffHours + "h " + str;*/return intArrayOf(
            diffHours.toInt(),
            diffMinutes.toInt(),
            diffSeconds.toInt()
        )
    }

    private fun getTimeDiff(): Long {
        return (if (endTime == -1L) System.currentTimeMillis() else endTime) - startTime
    }

    override fun drawElement(): Border {

        when (modeValue.get().toLowerCase()) {
            "normal" -> {
                val fontRenderer = fontValue.get()

                val y2 = fontRenderer.fontHeight * 5 + 11f + 3f
                val x2 = 120f
                val floatX = renderX.toFloat()
                val floatY = renderY.toFloat()


                if (blur.get()) {
                    GL11.glTranslated(-renderX, -renderY, 0.0)
                    GL11.glPushMatrix()
                    BlurBuffer.blurRoundArea(floatX, floatY, x2, y2, radiusValue.get().toInt())
                    GL11.glPopMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                }
                if (Shadow.get()) {
                    RenderUtils.drawShadowWithCustomAlpha(0f, 0f, x2, y2, 255f)
                }

                val time: String
                time = if (Minecraft.getMinecraft().isSingleplayer) {
                    "SinglePlayer"
                } else {
                    val durationInMillis: Long = System.currentTimeMillis() - Recorder.startTime
                    val second = durationInMillis / 1000 % 60
                    val minute = durationInMillis / (1000 * 60) % 60
                    val hour = durationInMillis / (1000 * 60 * 60) % 24
                    String.format("%02d:%02d:%02d", hour, minute, second)
                }
                val hud = LiquidBounce.moduleManager.getModule(HUD::class.java) as HUD
                RoundedUtil.drawRound(
                    0f,
                    0f,
                    x2,
                    y2,
                    radiusValue.get(),
                    Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get())
                )
                RenderUtils.drawGradientSideways(
                    2.22,
                    fontRenderer.fontHeight + 2.5 + 0.0 - 1,
                    x2.toDouble() - 2.22,
                    fontRenderer.fontHeight + 2.5 + 1.16f - 1,
                    Color(hud.r.get(), hud.g.get(), hud.b.get()).rgb,
                    Color(hud.r2.get(), hud.g2.get(), hud.b2.get()).rgb
                )
                //RenderUtils.drawShadow(2f, -2f, x2, y2,)
                Fonts.font35.drawCenteredString("Session Info", x2 / 2f, 2f, Color.WHITE.rgb, false)
                if (outline.get()) {
                    RenderUtils.rectangleBordered(
                        0.0,
                        0.0,
                        x2.toDouble(),
                        y2.toDouble(),
                        0.55,
                        Color(0, 0, 0, 30).rgb,
                        RenderUtils.getGradientOffset(
                            Color(hud.r.get(), hud.g.get(), hud.b.get()),
                            Color(hud.r2.get(), hud.g2.get(), hud.b2.get()),
                            100.0
                        ).rgb
                    )
                }
                fontRenderer.drawString("Play Time:", 2, fontRenderer.fontHeight + 6, Color.WHITE.rgb)
                fontRenderer.drawString(
                    "$time",
                    (120f - fontRenderer.getStringWidth("$time")),
                    fontRenderer.fontHeight + 6f,
                    Color.WHITE.rgb
                )
                fontRenderer.drawString("Games Won", 2, fontRenderer.fontHeight * 2 + 8, Color.WHITE.rgb)
                fontRenderer.drawString(
                    "${Recorder.win}",
                    (120f - fontRenderer.getStringWidth("${Recorder.win}")),
                    fontRenderer.fontHeight * 2 + 8f,
                    Color.WHITE.rgb
                )
                fontRenderer.drawString("Players Killed", 2, fontRenderer.fontHeight * 3 + 10, Color.WHITE.rgb)
                fontRenderer.drawString(
                    Recorder.killCounts.toString(),
                    (120f - fontRenderer.getStringWidth("${Recorder.killCounts}")),
                    fontRenderer.fontHeight * 3 + 10f,
                    Color.WHITE.rgb
                )
                fontRenderer.drawString("Banned", 2, fontRenderer.fontHeight * 4 + 12, Color.WHITE.rgb)
                fontRenderer.drawString(
                    "0",
                    (120f - fontRenderer.getStringWidth("0")),
                    fontRenderer.fontHeight * 4 + 12f,
                    Color.WHITE.rgb
                )
            }

            "tenacity" -> {

                val x: Float = this.x.toFloat()
                val y: Float = this.y.toFloat()
                val height: Float = (linesLeft.size * (FontLoaders.F18.height + 6) + 24).toFloat()
                val width = 140f

                val hudMod = LiquidBounce.moduleManager.getModule(
                    HUD::class.java
                ) as HUD
                when (colorMode.get()) {
                    "Sync" -> {
                        val colors = arrayOf(ClickGUI.generateColor(), ClickGUI.generateColor())
                        gradientColor1 = RenderUtils.interpolateColorsBackAndForth(
                            15, 0,
                            colors[0], colors[1], hudMod.hueInterpolation.get()
                        )
                        gradientColor2 = RenderUtils.interpolateColorsBackAndForth(
                            15, 90,
                            colors[0], colors[1], hudMod.hueInterpolation.get()
                        )
                        gradientColor3 = RenderUtils.interpolateColorsBackAndForth(
                            15, 180,
                            colors[0], colors[1], hudMod.hueInterpolation.get()
                        )
                        gradientColor4 = RenderUtils.interpolateColorsBackAndForth(
                            15, 270,
                            colors[0], colors[1], hudMod.hueInterpolation.get()
                        )
                    }

                    "Tenacity" -> {
                        gradientColor1 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            0,
                            getClientColor(),
                            getAlternateClientColor(),
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor2 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            90,
                            getClientColor(),
                            getAlternateClientColor(),
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor3 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            180,
                            getClientColor(),
                            getAlternateClientColor(),
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor4 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            270,
                            getClientColor(),
                            getAlternateClientColor(),
                            hudMod.hueInterpolation.get()
                        )
                    }

                    "Gradient" -> {
                        gradientColor1 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            0,
                            ClickGUI.generateColor(),
                            ClickGUI.generateColor(),
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor2 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            90,
                            ClickGUI.generateColor(),
                            ClickGUI.generateColor(),
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor3 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            180,
                            ClickGUI.generateColor(),
                            ClickGUI.generateColor(),
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor4 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            270,
                            ClickGUI.generateColor(),
                            ClickGUI.generateColor(),
                            hudMod.hueInterpolation.get()
                        )
                    }

                    "Analogous" -> {
                        val `val` = if (degree.get() == "30") 0 else 1
                        val analogous = RenderUtils.getAnalogousColor(ClickGUI.generateColor())[`val`]
                        gradientColor1 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            0,
                            ClickGUI.generateColor(),
                            analogous,
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor2 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            90,
                            ClickGUI.generateColor(),
                            analogous,
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor3 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            180,
                            ClickGUI.generateColor(),
                            analogous,
                            hudMod.hueInterpolation.get()
                        )
                        gradientColor4 = RenderUtils.interpolateColorsBackAndForth(
                            15,
                            270,
                            ClickGUI.generateColor(),
                            analogous,
                            hudMod.hueInterpolation.get()
                        )
                    }

                    "Modern" -> RoundedUtil.drawRoundOutline(
                        x,
                        y,
                        width,
                        height,
                        6f,
                        .5f,
                        Color(10, 10, 10, 80),
                        Color(-2)
                    )
                }
                val outlinedRadar = colorMode.get() != "Modern"
                DrRenderUtils.setAlphaLimit(0f)
                if (outlinedRadar) {
                    if (blur.value) {
                        BlurBuffer.blurRoundArea(x, y, width, height, 6)
                    }
                    RoundedUtil.drawGradientRound(
                        x,
                        y,
                        width,
                        height,
                        6f,
                        DrRenderUtils.applyOpacity(gradientColor4, .85f),
                        gradientColor1,
                        gradientColor3,
                        gradientColor2
                    )
                    DrRenderUtils.drawGradientRect2(
                        (x - 1).toDouble(),
                        (y + 15).toDouble(),
                        (width + 2).toDouble(),
                        5.0,
                        DrRenderUtils.applyOpacity(
                            Color.BLACK, .2f
                        ).rgb,
                        DrRenderUtils.applyOpacity(Color.BLACK, 0f).rgb
                    )
                } else {
                    DrRenderUtils.drawGradientRect2(
                        (x + 1).toDouble(),
                        (y + 15).toDouble(),
                        (width - 2).toDouble(),
                        5.0,
                        DrRenderUtils.applyOpacity(
                            Color.BLACK, .2f
                        ).rgb,
                        DrRenderUtils.applyOpacity(Color.BLACK, 0f).rgb
                    )
                }


                FontLoaders.F22.drawCenteredString(
                    "Statistics",
                    (x + width / 2).toDouble(),
                    (y + if (colorMode.get() == "Modern") 3 else 2).toDouble(),
                    -1
                )
                

                for (i in linesLeft.indices) {
                    val offset: Int = i * (FontLoaders.F18.height + 6)
                    FontLoaders.F18.drawString(
                        linesLeft[i],
                        x + 5,
                        (y + offset.toFloat() + (if (i == 0) 23.5f else 25f)),
                        -1
                    )
                }
                val playTime = getPlayTime()
                playtimeWidth = DrRenderUtils.animate(
                    20.5 + (if (playTime[1] > 0) 20 else 0) + if (playTime[0] > 0) 14 else 0,
                    playtimeWidth.toDouble(),
                    0.008
                ).toFloat()

                val playtimeX: Float = x + width - (playtimeWidth + 5)
                if (animatedPlaytime.get()) {
                    drawAnimatedPlaytime(playtimeX, y)
                } else {
                    val playtimeString = playTime[0].toString() + "h " + playTime[1] + "m " + playTime[2] + "s"
                    FontLoaders.F18.drawString(
                        playtimeString,
                        playtimeX + playtimeWidth - FontLoaders.F18.getStringWidth(playtimeString),
                        y + 24,
                        -1
                    )
                }

                val linesRight = listOf(Recorder.totalPlayed.toString(), killCounts.toString())

                for (i in linesRight.indices) {
                    val offset = (i + 1) * (FontLoaders.F18.height + 6)
                    FontLoaders.F18.drawString(
                        linesRight[i], x + width - (FontLoaders.F18.getStringWidth(
                            linesRight[i]
                        ) + 5), y + offset + 25, -1
                    )
                }
            }
        }

        return Border(-2f, -2f, 150f, 150f)
    }
}