package net.ccbluex.liquidbounce.ui.client.hud.element.elements

//import net.ccbluex.liquidbounce.font.FontLoaders
//import net.ccbluex.liquidbounce.utils.blur.BlurBuffer
import blur.BlurBuffer
import me.font.FontLoaders
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "CpuRender", disableScale = true, priority = 1)
class CPURender(x: Double = 5.0, y: Double = 130.0) : Element(x, y) {


    override fun drawElement(): Border? {
//            val nmsl = ManagementFactory.getOperatingSystemMXBean();
            var maxCpu = 100L
            var totalCpu = ClientUtils.getCPUUse()
            var freeCpu = maxCpu- totalCpu
            var usedCpu = totalCpu - freeCpu
            RenderUtils.drawRect(0,0,60,70,Color(0,0,0,30).rgb)
            RenderUtils.drawShadowWithCustomAlpha(0f,0f,60f,70f,255f)
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            BlurBuffer.blurArea(renderX.toFloat(),renderY.toFloat(),60f,70f)
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
            RenderUtils.drawArc(30F, 35F, 20.0, Color(0,0,0,50).rgb, 0, 360.0, 8)
            RenderUtils.drawArc(30F, 35F, 20.0, Color(255,255,255).rgb, 0, 360.0 * usedCpu / maxCpu  , 8)
            Fonts.font35.drawString("RAM",30F - Fonts.font35.getStringWidth("RAM") / 2,35F - Fonts.font35.fontHeight / 2,-1)
            FontLoaders.F16.drawString((usedCpu * 100L / maxCpu).toString() + "%",30F - FontLoaders.C16.getStringWidth((usedCpu * 100L / totalCpu).toString() + "%" ) / 2 ,60F,-1)


        return Border(0f,0F,60F,70F)
    }

}