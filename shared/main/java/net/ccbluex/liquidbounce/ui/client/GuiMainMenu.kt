/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client



import Linkow.ui.Button
import Linkow.utils.MenuShader
import Linkow.utils.ZoomUtil
import blur.BlurBuffer
import me.font.FontLoaders
import me.utils.RoundedUtil
import net.ccbluex.liquidbounce.api.minecraft.util.IScaledResolution
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen
import net.ccbluex.liquidbounce.injection.backend.ClassProviderImpl
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

class GuiMainMenu(pass: Int) : WrappedGuiScreen() {
    var mc = MinecraftInstance.mc
    var sr: IScaledResolution? = null
    var arrayList = ArrayList<Button>()
    private val backgroundShader: MenuShader
    private val zoomValue = 0.298f
    private var LogoZoom: ZoomUtil? = null
    private var singleplayerZoom: ZoomUtil? = null
    private var multiplayerZoom: ZoomUtil? = null
    private var altsZoom: ZoomUtil? = null
    private var settingsZoom: ZoomUtil? = null
    private var quitZoom: ZoomUtil? = null

    init {
        backgroundShader = MenuShader(pass)
    }

    override fun initGui() {
        sr = classProvider.createScaledResolution(mc)
        arrayList.clear()
        LogoZoom = ZoomUtil(
            sr!!.scaledWidth / 2f + 5,
            sr!!.scaledHeight / 2f - 105,
            (24 + 90).toFloat(),
            24f,
            12,
            zoomValue,
            6f
        )
        singleplayerZoom = ZoomUtil(
            sr!!.scaledWidth / 2f - 12 - 40,
            sr!!.scaledHeight / 2f - 60,
            (24 + 90).toFloat(),
            24f,
            12,
            zoomValue,
            6f
        )
        multiplayerZoom = ZoomUtil(
            sr!!.scaledWidth / 2f - 12 - 40,
            sr!!.scaledHeight / 2f - 30,
            (24 + 90).toFloat(),
            24f,
            12,
            zoomValue,
            6f
        )
        altsZoom = ZoomUtil(
            sr!!.scaledWidth / 2f - 12 - 40,
            sr!!.scaledHeight / 2f,
            (24 + 90).toFloat(),
            24f,
            12,
            zoomValue,
            6f
        )
        settingsZoom = ZoomUtil(
            sr!!.scaledWidth / 2f - 12 - 40,
            sr!!.scaledHeight / 2f + 30,
            (24 + 90).toFloat(),
            24f,
            12,
            zoomValue,
            6f
        )
        quitZoom = ZoomUtil(
            sr!!.scaledWidth / 2f - 12 - 40,
            sr!!.scaledHeight / 2f + 60,
            (24 + 90).toFloat(),
            24f,
            12,
            zoomValue,
            6f
        )
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        backgroundShader.render(sr)
        singleplayerZoom!!.update(mouseX, mouseY)
        multiplayerZoom!!.update(mouseX, mouseY)
        altsZoom!!.update(mouseX, mouseY)
        settingsZoom!!.update(mouseX, mouseY)
        quitZoom!!.update(mouseX, mouseY)
        RoundedUtil.drawRound(
            singleplayerZoom!!.x,
            singleplayerZoom!!.y,
            singleplayerZoom!!.width,
            singleplayerZoom!!.height,
            8f,
            Color(23, 23, 23, 40)
        )
        BlurBuffer.blurRoundArea(singleplayerZoom!!.x, singleplayerZoom!!.y, singleplayerZoom!!.width, singleplayerZoom!!.height,8);
        FontLoaders.C18.drawCenteredStringWithShadow(
            "SinglePlayer",
            sr!!.scaledWidth / 2f + 5f,
            sr!!.scaledHeight / 2f - 55,
            -1
        )
        RoundedUtil.drawRound(
            multiplayerZoom!!.x,
            multiplayerZoom!!.y,
            multiplayerZoom!!.width,
            multiplayerZoom!!.height,
            8f,
            Color(23, 23, 23, 40)
        )
        BlurBuffer.blurRoundArea(multiplayerZoom!!.x, multiplayerZoom!!.y, multiplayerZoom!!.width, multiplayerZoom!!.height,
            8
        );
        FontLoaders.C18.drawCenteredStringWithShadow(
            "MultiPlayer",
            sr!!.scaledWidth / 2f + 5f,
            sr!!.scaledHeight / 2f - 25,
            -1
        )
        RoundedUtil.drawRound(
            altsZoom!!.x,
            altsZoom!!.y,
            altsZoom!!.width,
            altsZoom!!.height,
            8f,
            Color(23, 23, 23, 40)
        )
        BlurBuffer.blurRoundArea(altsZoom!!.x, altsZoom!!.y, altsZoom!!.width, altsZoom!!.height,8);
        FontLoaders.C18.drawCenteredStringWithShadow(
            "Alt Manager",
            sr!!.scaledWidth / 2f + 5f,
            sr!!.scaledHeight / 2f + 5,
            -1
        )
        RoundedUtil.drawRound(
            settingsZoom!!.x,
            settingsZoom!!.y,
            settingsZoom!!.width,
            settingsZoom!!.height,
            8f,
            Color(23, 23, 23, 40)
        )
        BlurBuffer.blurRoundArea(settingsZoom!!.x, settingsZoom!!.y, settingsZoom!!.width, settingsZoom!!.height,8);
        FontLoaders.C18.drawCenteredStringWithShadow(
            "Options",
            sr!!.scaledWidth / 2f + 5f,
            sr!!.scaledHeight / 2f + 35,
            -1
        )
        RoundedUtil.drawRound(
            quitZoom!!.x,
            quitZoom!!.y,
            quitZoom!!.width,
            quitZoom!!.height,
            8f,
            Color(23, 23, 23, 40)
        )
        BlurBuffer.blurRoundArea(quitZoom!!.x, quitZoom!!.y, quitZoom!!.width, quitZoom!!.height,8);
        FontLoaders.C18.drawCenteredStringWithShadow(
            "Quit Game",
            sr!!.scaledWidth / 2f + 5f,
            sr!!.scaledHeight / 2f + 65,
            -1
        )
        // MainMenu by Qianyeyou QQ:445851057
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (RenderUtils.isHovered(LogoZoom!!.x, LogoZoom!!.y, LogoZoom!!.width, LogoZoom!!.height, mouseX, mouseY)) {
            try {
                Desktop.getDesktop().browse(URI("web.badlife.icu"))
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
        if (RenderUtils.isHovered(
                singleplayerZoom!!.x,
                singleplayerZoom!!.y,
                singleplayerZoom!!.width,
                singleplayerZoom!!.height,
                mouseX,
                mouseY
            )
        ) {
            mc.displayGuiScreen(classProvider.createGuiSelectWorld(this.representedScreen))
        }
        if (RenderUtils.isHovered(
                multiplayerZoom!!.x,
                multiplayerZoom!!.y,
                multiplayerZoom!!.width,
                multiplayerZoom!!.height,
                mouseX,
                mouseY
            )
        ) {
            mc.displayGuiScreen(classProvider.createGuiMultiplayer(this.representedScreen))
        }
        if (RenderUtils.isHovered(altsZoom!!.x, altsZoom!!.y, altsZoom!!.width, altsZoom!!.height, mouseX, mouseY)) {
            MinecraftInstance.mc.displayGuiScreen(ClassProviderImpl.wrapGuiScreen(GuiAltManager(this.representedScreen)))
        }
        if (RenderUtils.isHovered(
                settingsZoom!!.x,
                settingsZoom!!.y,
                settingsZoom!!.width,
                settingsZoom!!.height,
                mouseX,
                mouseY
            )
        ) {
            mc.displayGuiScreen(classProvider.createGuiOptions(this.representedScreen,mc.gameSettings))
        }
        if (RenderUtils.isHovered(quitZoom!!.x, quitZoom!!.y, quitZoom!!.width, quitZoom!!.height, mouseX, mouseY)) {
            Minecraft.getMinecraft().shutdown()
        }
    }
}