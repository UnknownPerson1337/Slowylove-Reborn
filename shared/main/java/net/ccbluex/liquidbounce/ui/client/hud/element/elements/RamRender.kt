package net.ccbluex.liquidbounce.ui.client.hud.element.elements

//import net.ccbluex.liquidbounce.font.FontLoaders
//import net.ccbluex.liquidbounce.utils.blur.BlurBuffer
import blur.BlurBuffer
import me.font.FontLoaders
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "RamRender", disableScale = true, priority = 1)
class RamRender(x: Double = 5.0, y: Double = 130.0) : Element(x, y) {


    override fun drawElement(): Border? {
            var maxMem = Runtime.getRuntime().maxMemory()
            var totalMem = Runtime.getRuntime().totalMemory()
            var freeMem = Runtime.getRuntime().freeMemory()
            var usedMem = totalMem - freeMem
            RenderUtils.drawRect(0,0,60,70,Color(0,0,0,30).rgb)
            RenderUtils.drawShadowWithCustomAlpha(0f,0f,60f,70f,255f)
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            BlurBuffer.blurArea(renderX.toFloat(),renderY.toFloat(),60f,70f)
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
            RenderUtils.drawArc(30F, 35F, 20.0, Color(0,0,0,50).rgb, 0, 360.0, 8)
            RenderUtils.drawArc(30F, 35F, 20.0, Color(255,255,255).rgb, 0, 360.0 * usedMem / maxMem  , 8)
            Fonts.font35.drawString("RAM",30F - Fonts.font35.getStringWidth("RAM") / 2,35F - Fonts.font35.fontHeight / 2,-1)
            FontLoaders.F16.drawString((usedMem * 100L / maxMem).toString() + "%",30F - FontLoaders.C16.getStringWidth((usedMem * 100L / totalMem).toString() + "%" ) / 2 ,60F,-1)


        return Border(0f,0F,60F,70F)
    }

}