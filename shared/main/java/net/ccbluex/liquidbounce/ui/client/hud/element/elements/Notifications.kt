/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 * 
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

//import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer
//import me.utils.BlurUtils

import net.ccbluex.liquidbounce.LiquidBounce.hud
import net.ccbluex.liquidbounce.features.module.modules.render.ColorMixer
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils2
import net.ccbluex.liquidbounce.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    val styleValue = ListValue("Style", arrayOf("Full", "Compact", "Lite","Other"), "Lite")
    val barValue = BoolValue("Bar", true)
    val otherbarValue = BoolValue("OtherBar",true)
    val bgAlphaValue = IntegerValue("Background-Alpha", 120, 0, 255)

    val blurValue = BoolValue("Blur", false)
    val blurStrength = FloatValue("Strength", 0F, 0F, 30F)

    val hAnimModeValue = ListValue("H-Animation", arrayOf("LiquidBounce", "Smooth","None"), "LiquidBounce")
    val vAnimModeValue = ListValue("V-Animation", arrayOf("None", "Smooth"), "Smooth")
    val animationSpeed = FloatValue("Speed", 0.5F, 0.01F, 1F)

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Example Notification", Notification.Type.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        var animationY = 30F
        val notifications = mutableListOf<Notification>()

        for (i in hud.notifications)
            notifications.add(i)

        if (!classProvider.isGuiHudDesigner(mc.currentScreen) || !notifications.isEmpty()) {
            var indexz = 0
            for (i in notifications) {
                if (indexz == 0 && styleValue.get().equals("material", true) && side.vertical != Side.Vertical.DOWN) animationY -= i.notifHeight - (if (barValue.get()) 2F else 0F)
                i.drawNotification(animationY, this)
                if (indexz < notifications.size - 1) indexz++
                animationY += (when (styleValue.get().toLowerCase()) {
                    "compact" -> 20F
                    "full" -> 30F
                    else -> (if (side.vertical == Side.Vertical.DOWN) i.notifHeight else notifications[indexz].notifHeight) + 5F + (if (barValue.get()) 2F else 0F)
                }) * (if (side.vertical == Side.Vertical.DOWN) 1F else -1F)
            }
        } else {
            var indexz = 0
            for (i in notifications) {
                if (indexz == 0 && styleValue.get().equals("material", true) && side.vertical != Side.Vertical.DOWN) animationY -= i.notifHeight - (if (barValue.get()) 2F else 0F)
//                i.drawNotification(animationY, this)
                exampleNotification.drawNotification(animationY - if (styleValue.get().equals("material", true) && side.vertical != Side.Vertical.DOWN) (exampleNotification.notifHeight - 5F - (if (barValue.get()) 2F else 0F)) else 0F, this)

                if (indexz < notifications.size - 1) indexz++
                animationY += (when (styleValue.get().toLowerCase()) {
                    "compact" -> 20F
                    "full" -> 30F
                    else -> (if (side.vertical == Side.Vertical.DOWN) i.notifHeight else notifications[indexz].notifHeight) + 5F + (if (barValue.get()) 2F else 0F)
                }) * (if (side.vertical == Side.Vertical.DOWN) 1F else -1F)
            }
        }

        if (!classProvider.isGuiHudDesigner(mc.currentScreen) || !notifications.isEmpty()) {
            exampleNotification.fadeState = Notification.FadeState.STAY
            exampleNotification.x = if (styleValue.get().equals("material", true)) 160F else exampleNotification.textLength + 8F

            if (exampleNotification.stayTimer.hasTimePassed(exampleNotification.displayTime))
                exampleNotification.stayTimer.reset()

            return getNotifBorder()
        }

        return getNotifBorder()
    }

    private fun getNotifBorder() = when (styleValue.get().toLowerCase()) {
        "full" -> Border(-130F, -58F, 0F, -30F)
        "compact" -> Border(-102F, -48F, 0F, -30F)
        else -> if (side.vertical == Side.Vertical.DOWN) Border(-160F, -50F, 0F, -30F) else Border(-160F, -20F, 0F, 0F)
    }
}
class Notification(message : String, type : Type, displayLength: Long) {
    private val notifyDir = "liquidbounce/notification/"

    private val imgSuccess = ResourceLocation("${notifyDir}checkmark.png")
    private val imgError = ResourceLocation("${notifyDir}error.png")
    private val imgWarning = ResourceLocation("${notifyDir}warning.png")
    private val imgInfo = ResourceLocation("${notifyDir}info.png")

    private val newSuccess = ResourceLocation("${notifyDir}new/checkmark.png")
    private val newError = ResourceLocation("${notifyDir}new/error.png")
    private val newWarning = ResourceLocation("${notifyDir}new/warning.png")
    private val newInfo = ResourceLocation("${notifyDir}new/info.png")

    var x = 0F
    var textLength = 0
    var fadeState = FadeState.IN
    var displayTime = 0L
    var stayTimer = MSTimer()
    var notifHeight = 0F
    private var message = ""
    var messageList : List<String>
    private var stay = 0F
    private var fadeStep = 0F
    private var firstY = 0f
    private var type: Type

    init {
        this.message = message
        this.messageList = Fonts.font40.listFormattedStringToWidth(message, 105)
        this.notifHeight = messageList.size.toFloat() * (Fonts.font40.fontHeight.toFloat() + 2F) + 8F
        this.type = type
        this.displayTime = displayLength
        this.firstY = 19190F
        this.stayTimer.reset()
        this.textLength = Fonts.font40.getStringWidth(message)
    }

    constructor(message: String, type: Type) : this(message, type, 2000L)

    constructor(message: String) : this(message, Type.INFO, 500L)

    constructor(message: String, displayLength: Long) : this(message, Type.INFO, displayLength)

    enum class Type {
        SUCCESS, INFO, WARNING, ERROR
    }

    enum class FadeState {
        IN, STAY, OUT, END
    }

    fun drawNotification(animationY: Float, parent: Notifications) {
        val delta = RenderUtils.deltaTime

        val style = parent.styleValue.get()
        val barMaterial = parent.barValue.get()
        val otherbar = parent.otherbarValue.get()

        val blur = parent.blurValue.get()
        val strength = parent.blurStrength.get()

        val hAnimMode = parent.hAnimModeValue.get()
        val vAnimMode = parent.vAnimModeValue.get()
        val animSpeed = parent.animationSpeed.get()

        val side = parent.side

        val originalX = parent.renderX.toFloat()
        val originalY = parent.renderY.toFloat()
        val width = if (style.equals("material", true)) 160F else textLength.toFloat() + 8.0f

        val backgroundColor = Color(0, 0, 0, parent.bgAlphaValue.get())
        val enumColor = when (type) {
            Type.SUCCESS -> Color(80, 255, 80).rgb
            Type.ERROR -> Color(255, 80, 80).rgb
            Type.INFO -> Color(255, 255, 255).rgb
            Type.WARNING -> Color(255, 255, 0).rgb
        }

        if (vAnimMode.equals("smooth", true)) {
            if (firstY == 19190.0F)
                firstY = animationY
            else
                firstY = AnimationUtils2.animate(animationY, firstY, 0.02F * delta)
        } else {
            firstY = animationY
        }

        var y = firstY

        when (style.toLowerCase()) {
            "compact" -> {
                GlStateManager.resetColor()

                if (blur) {
                    GL11.glTranslatef(-originalX, -originalY, 0F)
                    GL11.glPushMatrix()
//                    BlurUtils.blurAreaRounded(originalX + -x - 5F, originalY + -18F - y, originalX + -x + 8F + textLength, originalY + -y, 3F, strength)
                    GL11.glPopMatrix()
                    GL11.glTranslatef(originalX, originalY, 0F)
                }

                RenderUtils.customRounded(-x + 8F + textLength, -y, -x - 2F, -18F - y, 0F, 3F, 3F, 0F, backgroundColor.rgb)
                RenderUtils.customRounded(-x - 2F, -y, -x - 5F, -18F - y, 3F, 0F, 0F, 3F, when(type) {
                    Type.SUCCESS -> Color(80, 255, 80).rgb
                    Type.ERROR -> Color(255, 80, 80).rgb
                    Type.INFO -> Color(255, 255, 255).rgb
                    Type.WARNING -> Color(255, 255, 0).rgb
                })

                GlStateManager.resetColor()
                Fonts.font40.drawString(message, -x + 3, -13F - y, -1)
            }
            "full" -> {
                val dist = (x + 1 + 26F) - (x - 8 - textLength)
                val kek = -x - 1 - 26F

                GlStateManager.resetColor()

                if (blur) {
                    GL11.glTranslatef(-originalX, -originalY, 0F)
                    GL11.glPushMatrix()
//                    BlurUtils.blurArea(originalX + kek, originalY + -28F - y, originalX + -x + 8 + textLength, originalY + -y, strength)
                    GL11.glPopMatrix()
                    GL11.glTranslatef(originalX, originalY, 0F)
                }

                RenderUtils.drawRect(-x + 8 + textLength, -y, kek, -28F - y, backgroundColor.rgb)

                GL11.glPushMatrix()
                GlStateManager.disableAlpha()
                RenderUtils.drawImage4(when (type) {
                    Type.SUCCESS -> imgSuccess
                    Type.ERROR -> imgError
                    Type.WARNING -> imgWarning
                    Type.INFO -> imgInfo
                }, kek.toInt(), (-27F - y).toInt(), 26, 26)
                GlStateManager.enableAlpha()
                GL11.glPopMatrix()

                GlStateManager.resetColor()
                if (fadeState == FadeState.STAY && !stayTimer.hasTimePassed(displayTime))
                    RenderUtils.drawRect(kek, -y, kek + (dist * if (stayTimer.hasTimePassed(displayTime)) 0F else ((displayTime - (System.currentTimeMillis() - stayTimer.time)).toFloat() / displayTime.toFloat())), -1F - y, enumColor)
                else if (fadeState == FadeState.IN)
                    RenderUtils.drawRect(kek, -y, kek + dist, -1F - y, enumColor)

                GlStateManager.resetColor()
                Fonts.font40.drawString(message, -x + 2, -18F - y, -1)
            }
            "lite" -> {
                GlStateManager.resetColor()

                GL11.glPushMatrix()
                GL11.glTranslatef(-x, -y - notifHeight - (if (barMaterial) 2F else 0F), 0F)

                if (barMaterial) {
                    Stencil.write(true)
                    RenderUtils.drawRoundRect(0F, 0F, textLength + 50f, notifHeight + 2F, 2F, when (type) {
                        Type.SUCCESS -> Color(122,255,211,100).rgb
                        Type.ERROR -> Color(122,255,211,100).rgb
                        Type.WARNING -> Color(122,255,211,100).rgb
                        Type.INFO -> Color(122,255,211,100).rgb
                    })
                    Stencil.erase(true)
                    if (fadeState == FadeState.STAY) RenderUtils.drawRect(0F, notifHeight, (textLength + 50f) * if (stayTimer.hasTimePassed(displayTime)) 1F else ((System.currentTimeMillis() - stayTimer.time).toFloat() / displayTime.toFloat()), notifHeight + 2F, when (type) {
                        Type.SUCCESS -> ColorMixer.getMixedColor(2, 2).rgb
                        Type.ERROR -> ColorMixer.getMixedColor(2, 2).rgb
                        Type.WARNING -> ColorMixer.getMixedColor(2, 2).rgb
                        Type.INFO -> ColorMixer.getMixedColor(2, 2).rgb
                    })
                    Stencil.dispose()
                } else RenderUtils.drawRoundRect(0F, 0F, textLength + 50f, notifHeight, 2F, when (type) {
                    Type.SUCCESS -> Color(122,255,211,100).rgb
                    Type.ERROR -> Color(122,255,211,100).rgb
                    Type.WARNING -> Color(122,255,211,100).rgb
                    Type.INFO -> Color(122,255,211,100).rgb
                })

                var yHeight = 7F
                for (s in messageList) {
                    Fonts.font40.drawString(s, 30F, yHeight, if (type == Type.ERROR) -1 else -1)
                    yHeight += Fonts.font40.fontHeight.toFloat() + 2F
                }

                GL11.glPushMatrix()
                GlStateManager.disableAlpha()
                RenderUtils.drawRoundedRect(9f, notifHeight / 2F - 6f, 23f,notifHeight / 2f + 8f,
                    2.5f.toInt(),Color(112,255,221,110).rgb)
                RenderUtils.drawImage3(when (type) {
                    Type.SUCCESS -> newSuccess
                    Type.ERROR -> newError
                    Type.WARNING -> newWarning
                    Type.INFO -> newInfo},9F, notifHeight / 2F - 6F, 14,14,255f,255f,255f,255f)
                GlStateManager.enableAlpha()
                GL11.glPopMatrix()

                GL11.glPopMatrix()

                GlStateManager.resetColor()
            }
            "other" ->{
                GlStateManager.resetColor()

                GL11.glPushMatrix()
                GL11.glTranslatef(-x, -y - notifHeight - (if (barMaterial) 2F else 0F), 0F)

                if (otherbar) {
                    Stencil.write(true)
                    RenderUtils.drawRect(0F, 0F, textLength + 50f, notifHeight + 2F, when (type) {
                        Type.SUCCESS -> Color(0,0,0,100).rgb
                        Type.ERROR -> Color(0,0,0,100).rgb
                        Type.WARNING -> Color(0,0,0,100).rgb
                        Type.INFO -> Color(0,0,0,100).rgb
                    })
                    Stencil.erase(true)
                    RenderUtils.drawGradientSideways(
                        0.0,
                        0.0,
                        ((textLength + 50f) * (if (stayTimer.hasTimePassed(displayTime)) 1f else ((System.currentTimeMillis() - stayTimer.time).toFloat() / displayTime.toFloat())).toFloat()).toDouble(),
                        (notifHeight.toInt() +2f).toDouble(), when (type) {
                        Type.SUCCESS -> Color(112,255,221,255).rgb
                        Type.ERROR -> Color(112,255,221,255).rgb
                        Type.WARNING -> Color(112,255,221,255).rgb
                        Type.INFO -> Color(112,255,221,255).rgb
                    },Color(0,0,0,0).rgb)
                    Stencil.dispose()
                } else RenderUtils.drawRect(0F, 0F, textLength + 50f, notifHeight, when (type) {
                    Type.SUCCESS -> Color(0,0,0,100).rgb
                    Type.ERROR -> Color(0,0,0,100).rgb
                    Type.WARNING -> Color(0,0,0,100).rgb
                    Type.INFO -> Color(0,0,0,100).rgb
                })

                var yHeight = 7F
                for (s in messageList) {
                    Fonts.font40.drawString(s, 30F, yHeight, if (type == Type.ERROR) -1 else -1)
                    yHeight += Fonts.font40.fontHeight.toFloat() + 2F
                }

                GL11.glPushMatrix()
                GlStateManager.disableAlpha()
                RenderUtils.drawImage3(when (type) {
                    Type.SUCCESS -> newSuccess
                    Type.ERROR -> newError
                    Type.WARNING -> newWarning
                    Type.INFO -> newInfo},9F, notifHeight / 2F - 6F, 14,14,255f,255f,255f,255f)
                GlStateManager.enableAlpha()
                GL11.glPopMatrix()

                GL11.glPopMatrix()

                GlStateManager.resetColor()
            }
        }

        when (fadeState) {
            FadeState.IN -> {
                if (x < width) {
                    if (hAnimMode.equals("smooth", true))
                        x = AnimationUtils2.animate(width, x, animSpeed * 0.025F * delta)
                    else
                        x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 4F
                }
                if (x >= width) {
                    fadeState = FadeState.STAY
                    x = width
                    fadeStep = width
                }

                stay = 60F
                stayTimer.reset()
            }

            FadeState.STAY -> {
                if (stay > 0) {
                    stay = 0F
                    stayTimer.reset()
                }
                if (stayTimer.hasTimePassed(displayTime))
                    fadeState = FadeState.OUT
            }

            FadeState.OUT -> if (x > 0) {
                if (hAnimMode.equals("smooth", true))
                    x = net.ccbluex.liquidbounce.utils.AnimationUtils2.animate(-width / 2F, x, animSpeed * 0.025F * delta)
                else
                    x = AnimationUtils.easeOut(fadeStep, width) * width

                fadeStep -= delta / 4F
            } else
                fadeState = FadeState.END

            FadeState.END -> hud.removeNotification(this)
        }
    }
}

enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(0x60E066)),
    ERROR(Color(0xFF2F3A)),
    WARNING(Color(0xF5FD00)),
    INFO( Color(106, 106, 220));
}


enum class FadeState { IN, STAY, OUT, END }