/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.render;

import me.utils.ImageUtils;
import net.ccbluex.liquidbounce.api.enums.WDefaultVertexFormats;
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer;
import net.ccbluex.liquidbounce.api.minecraft.client.render.ITessellator;
import net.ccbluex.liquidbounce.api.minecraft.client.render.IWorldRenderer;
import net.ccbluex.liquidbounce.api.minecraft.renderer.entity.IRenderManager;
import net.ccbluex.liquidbounce.api.minecraft.util.*;
import net.ccbluex.liquidbounce.injection.backend.Backend;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.particles.Particle;
import net.ccbluex.liquidbounce.utils.particles.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static me.utils.DrRenderUtils.interpolateColorC;
import static me.utils.DrRenderUtils.interpolateInt;
import static net.ccbluex.liquidbounce.utils.MathUtils.interpolateFloat;
import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static org.lwjgl.opengl.GL11.*;

public final class RenderUtils extends MinecraftInstance {
    private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
    public static int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }
    private static final int[] DISPLAY_LISTS_2D = new int[4];
    public static int deltaTime;

    static {
        for (int i = 0; i < DISPLAY_LISTS_2D.length; i++) {
            DISPLAY_LISTS_2D[i] = glGenLists(1);
        }

        glNewList(DISPLAY_LISTS_2D[0], GL_COMPILE);

        quickDrawRect(-7F, 2F, -4F, 3F);
        quickDrawRect(4F, 2F, 7F, 3F);
        quickDrawRect(-7F, 0.5F, -6F, 3F);
        quickDrawRect(6F, 0.5F, 7F, 3F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[1], GL_COMPILE);

        quickDrawRect(-7F, 3F, -4F, 3.3F);
        quickDrawRect(4F, 3F, 7F, 3.3F);
        quickDrawRect(-7.3F, 0.5F, -7F, 3.3F);
        quickDrawRect(7F, 0.5F, 7.3F, 3.3F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[2], GL_COMPILE);

        quickDrawRect(4F, -20F, 7F, -19F);
        quickDrawRect(-7F, -20F, -4F, -19F);
        quickDrawRect(6F, -20F, 7F, -17.5F);
        quickDrawRect(-7F, -20F, -6F, -17.5F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[3], GL_COMPILE);

        quickDrawRect(7F, -20F, 7.3F, -17.5F);
        quickDrawRect(-7.3F, -20F, -7F, -17.5F);
        quickDrawRect(4F, -20.3F, 7.3F, -20F);
        quickDrawRect(-7.3F, -20.3F, -4F, -20F);

        glEndList();
    }
    public static boolean isHovered(float x, float y, float w, float h, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h));
    }
    public static double animate(double endPoint, double current, double speed) {
        boolean shouldContinueAnimation = endPoint > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }

    public static void drawRectBasedBorder(float x, float y, float x2, float y2, float width, int color1) {
        drawRect(x - width / 2F, y - width / 2F, x2 + width / 2F, y + width / 2F, color1);
        drawRect(x - width / 2F, y + width / 2F, x + width / 2F, y2 + width / 2F, color1);
        drawRect(x2 - width / 2F, y + width / 2F, x2 + width / 2F, y2 + width / 2F, color1);
        drawRect(x + width / 2F, y2 - width / 2F, x2 - width / 2F, y2 + width / 2F, color1);
    }

    public static void drawTriAngle(float cx, float cy, float r, float n, Color color, boolean polygon) {
        cx *= 2.0;
        cy *= 2.0;
        double b = 6.2831852 / n;
        double p = Math.cos(b);
        double s = Math.sin(b);
        r *= 2.0;
        double x = r;
        double y = 0.0;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        glLineWidth(1F);
        enableGlCap(GL_LINE_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        glColor(color);
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        worldrenderer.begin(polygon ? GL_POLYGON : 2, DefaultVertexFormats.POSITION);
        int ii = 0;
        while (ii < n) {
            worldrenderer.pos(x + cx, (double)y + cy, 0.0D).endVertex();
            double t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        tessellator.draw();
        enableTexture2D();
        disableBlend();
        GlStateManager.scale(2f, 2f, 2f);
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void fastRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius) {
        float z = 0;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double x1 = (double)(paramXStart + radius);
        double y1 = (double)(paramYStart + radius);
        double x2 = (double)(paramXEnd - radius);
        double y2 = (double)(paramYEnd - radius);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 0.25)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 0.25)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 0.25)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 0.25)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }
    public static java.awt.Color[] getAnalogousColor(java.awt.Color color) {
        java.awt.Color[] colors = new java.awt.Color[2];
        float[] hsb = java.awt.Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        float degree = 30 / 360f;

        float newHueAdded = hsb[0] + degree;
        colors[0] = new java.awt.Color(java.awt.Color.HSBtoRGB(newHueAdded, hsb[1], hsb[2]));

        float newHueSubtracted = hsb[0] - degree;

        colors[1] = new java.awt.Color(java.awt.Color.HSBtoRGB(newHueSubtracted, hsb[1], hsb[2]));

        return colors;
    }
    public static java.awt.Color interpolateColorHue(java.awt.Color color1, java.awt.Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        float[] color1HSB = java.awt.Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = java.awt.Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        java.awt.Color resultColor = java.awt.Color.getHSBColor(interpolateFloat(color1HSB[0], color2HSB[0], amount),
                interpolateFloat(color1HSB[1], color2HSB[1], amount), interpolateFloat(color1HSB[2], color2HSB[2], amount));

        return new java.awt.Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }
    public static java.awt.Color interpolateColorsBackAndForth(int speed, int index, java.awt.Color start, java.awt.Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, angle / 360f) : interpolateColorC(start, end, angle / 360f);
    }
    public static void drawArc(float n, float n2, double n3, final int n4, final int n5, final double n6, final int n7) {
        n3 *= 2.0;
        n *= 2.0f;
        n2 *= 2.0f;
        final float n8 = (n4 >> 24 & 0xFF) / 255.0f;
        final float n9 = (n4 >> 16 & 0xFF) / 255.0f;
        final float n10 = (n4 >> 8 & 0xFF) / 255.0f;
        final float n11 = (n4 & 0xFF) / 255.0f;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GL11.glLineWidth((float)n7);
        GL11.glEnable(2848);
        GL11.glColor4f(n9, n10, n11, n8);
        GL11.glBegin(3);
        int n12 = n5;
        while (n12 <= n6) {
            GL11.glVertex2d(n + Math.sin(n12 * 3.141592653589793 / 180.0) * n3, n2 + Math.cos(n12 * 3.141592653589793 / 180.0) * n3);
            ++n12;
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void renderParticles(final List<Particle> particles) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int i = 0;
        try {
            for (final Particle particle : particles) {
                i++;
                final Vec3 v = particle.position;
                boolean draw = true;

                final double x = v.xCoord - (mc.getRenderManager()).getRenderPosX();
                final double y = v.yCoord - (mc.getRenderManager()).getRenderPosY();
                final double z = v.zCoord - (mc.getRenderManager()).getRenderPosZ();

                final double distanceFromPlayer = mc.getThePlayer().getDistance(v.xCoord, v.yCoord - 1, v.zCoord);
                int quality = (int) (distanceFromPlayer * 4 + 10);

                if (quality > 350)
                    quality = 350;

                if (!isInViewFrustrum(new EntityEgg(mc2.world, v.xCoord, v.yCoord, v.zCoord)))
                    draw = false;

                if (i % 10 != 0 && distanceFromPlayer > 25)
                    draw = false;

                if (i % 3 == 0 && distanceFromPlayer > 15)
                    draw = false;

                if (draw) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);

                    final float scale = 0.04F;
                    GL11.glScalef(-scale, -scale, -scale);

                    GL11.glRotated(-mc.getRenderManager().getPlayerViewY(), 0.0D, 1.0D, 0.0D);
                    GL11.glRotated(mc.getRenderManager().getPlayerViewX(),1.0D, 0.0D, 0.0D);
                    final Color c = new Color(ColorUtils.INSTANCE.getColor(-(1 + 5 * 1.7f), 0.7f, 1));
                    drawFilledCircleNoGL(0, 0, 0.7, c.hashCode(), quality);

                    if (distanceFromPlayer < 4)
                        drawFilledCircleNoGL(0, 0, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                    if (distanceFromPlayer < 20)
                        drawFilledCircleNoGL(0, 0, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                    GL11.glScalef(0.8F, 0.8F, 0.8F);
                    GL11.glPopMatrix();
                }
            }
        } catch (final ConcurrentModificationException ignored) {
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glColor3d(255, 255, 255);
    }
    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox());
    }
    private static final Frustum frustrum = new Frustum();
    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        IEntity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.getPosX(), current.getPosY(), current.getPosZ());
        return frustrum.isBoundingBoxInFrustum(bb);
    }
    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
    }
    public static float smoothAnimation(float ani, float finalState, float speed, float scale) {
        return (float) RenderUtils.getAnimationState2(ani, finalState, Math.max(10, (Math.abs(ani - finalState)) * speed) * scale);
    }
    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        IScaledResolution scale = classProvider.createScaledResolution(mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * (float) factor),
                (int) (((float) scale.getScaledHeight() - y2) * (float) factor),
                (int) ((x2 - x) * (float) factor), (int) ((y2 - y) * (float) factor));
    }
    public static double getAnimationStateSmooth(double target, double current, double speed) {
        boolean larger;
        boolean bl = larger = target > current;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }
        if (target == current){
            return target;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = Math.max((dif * speed),1);
        if (factor < 0.1) {
            factor = 0.1;
        }
        if (larger){
            if (current + factor>target){
                current = target;
            }else {
                current += factor;
            }
        }else {
            if (current - factor<target) {
                current = target;
            }else {
                current -= factor;
            }
        }
        return current;
    }

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }
    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }
    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.getDisplayWidth() || framebuffer.framebufferHeight != mc.getDisplayHeight()) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.getDisplayWidth(), mc.getDisplayHeight(), true);
        }
        return framebuffer;
    }
    public static double getAnimationState2(double animation, double finalState, double speed) {
        float add = (float) (0.01 * speed);
        if (animation < finalState) {
            if (animation + add < finalState)
                animation += add;
            else
                animation = finalState;
        } else {
            if (animation - add > finalState)
                animation -= add;
            else
                animation = finalState;
        }
        return animation;
    }
    public static void drawExhiRect(float x, float y, float x2, float y2) {
        drawRect(x - 3.5F, y - 3.5F, x2 + 3.5F, y2 + 3.5F, Color.black.getRGB());
        drawRect(x - 3F, y - 3F, x2 + 3F, y2 + 3F, new Color(50, 50, 50).getRGB());
        //drawBorder(x - 1.5F, y - 1.5F, x2 + 1.5F, y2 + 1.5F, 2.5F, new Color(26, 26, 26).getRGB());
        drawRect(x - 2.5F, y - 2.5F, x2 + 2.5F, y2 + 2.5F, new Color(26, 26, 26).getRGB());
        drawRect(x - 0.5F, y - 0.5F, x2 + 0.5F, y2 + 0.5F, new Color(50, 50, 50).getRGB());
        drawRect(x, y, x2, y2, new Color(18, 18, 18).getRGB());
    }
    public static void drawJelloShadow(float x,float y,float w,float h){
        RenderUtils.drawImage(classProvider.createResourceLocation("liquidbounce/shadow/shadow.png"), (int) x - 3, (int) y - 3, (int) w + 4, (int) h + 4);
    }
    private static void quickPolygonCircle(float x, float y, float xRadius, float yRadius, int start, int end, int split) {
        for(int i = end; i >= start; i -= split) {
            glVertex2d(x + Math.sin(i * Math.PI / 180.0D) * xRadius, y + Math.cos(i * Math.PI / 180.0D) * yRadius);
        }
    }
    public static void drawOutlinedRect(float x, float y, float width, float height, float lineSize, int lineColor) {
        RenderUtils.drawRect(x, y, width, y + lineSize, lineColor);
        RenderUtils.drawRect(x, height - lineSize, width, height, lineColor);
        RenderUtils.drawRect(x, y + lineSize, x + lineSize, height - lineSize, lineColor);
        RenderUtils.drawRect(width - lineSize, y + lineSize, width, height - lineSize, lineColor);
    }
    public static void drawEntityOnScreen(final int posX, final int posY, final int scale, final IEntityLivingBase entity) {
        GlStateManager.pushMatrix();
        GlStateManager.enableColorMaterial();

        GlStateManager.translate(posX, posY, 50.0);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate(135F, 0F, 1F, 0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135F, 0F, 1F, 0F);
        GlStateManager.translate(0.0, 0.0, 0.0);

        float renderYawOffset = entity.getRenderYawOffset();
        float rotationYaw = entity.getRotationYaw();
        float rotationPitch = entity.getRotationPitch();
        float prevRotationYawHead = entity.getPrevRotationYawHead();
        float rotationYawHead = entity.getRotationYawHead();


        entity.setRenderYawOffset(0);
        entity.setRotationYaw(0);
        entity.setRotationPitch(90);
        entity.setRotationYawHead(entity.getRotationYaw());
        entity.setPrevRotationYawHead(entity.getRotationYaw());

        IRenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0F, 1F);
        rendermanager.setRenderShadow(true);

        entity.setRenderYawOffset(renderYawOffset);
        entity.setRotationYaw(rotationYaw);
        entity.setRotationPitch(rotationPitch);
        entity.setPrevRotationYawHead(prevRotationYawHead);
        entity.setRotationYawHead(rotationYawHead);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    public static void drawRoundedCornerRect(float x, float y, float x1, float y1, float radius) {
        glBegin(GL_POLYGON);

        float xRadius = (float) Math.min((x1 - x) * 0.5, radius);
        float yRadius = (float) Math.min((y1 - y) * 0.5, radius);
        quickPolygonCircle(x + xRadius,y + yRadius, xRadius, yRadius,180,270,4);
        quickPolygonCircle(x1 - xRadius,y + yRadius, xRadius, yRadius,90,180,4);
        quickPolygonCircle(x1 - xRadius,y1 - yRadius, xRadius, yRadius,0,90,4);
        quickPolygonCircle(x + xRadius,y1 - yRadius, xRadius, yRadius,270,360,4);

        glEnd();
    }
    // rTL = radius top left, rTR = radius top right, rBR = radius bottom right, rBL = radius bottom left
    public static void customRounded(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float rTL, float rTR, float rBR, float rBL, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        float z = 0;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double xTL = paramXStart + rTL;
        double yTL = paramYStart + rTL;

        double xTR = paramXEnd - rTR;
        double yTR = paramYStart + rTR;

        double xBR = paramXEnd - rBR;
        double yBR = paramYEnd - rBR;

        double xBL = paramXStart + rBL;
        double yBL = paramYEnd - rBL;

        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        glColor4f(red, green, blue, alpha);
        glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        if (rBR <= 0)
            glVertex2d(xBR, yBR);
        else for (double i = 0; i <= 90; i += 1)
            glVertex2d(xBR + Math.sin(i * degree) * rBR, yBR + Math.cos(i * degree) * rBR);

        if (rTR <= 0)
            glVertex2d(xTR, yTR);
        else for (double i = 90; i <= 180; i += 1)
            glVertex2d(xTR + Math.sin(i * degree) * rTR, yTR + Math.cos(i * degree) * rTR);

        if (rTL <= 0)
            glVertex2d(xTL, yTL);
        else for (double i = 180; i <= 270; i += 1)
            glVertex2d(xTL + Math.sin(i * degree) * rTL, yTL + Math.cos(i * degree) * rTL);

        if (rBL <= 0)
            glVertex2d(xBL, yBL);
        else for (double i = 270; i <= 360; i += 1)
            glVertex2d(xBL + Math.sin(i * degree) * rBL, yBL + Math.cos(i * degree) * rBL);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            double var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION));
        worldRenderer.pos((double) left, (double) bottom, 0.0D).endVertex();
        worldRenderer.pos((double) right, (double) bottom, 0.0D).endVertex();
        worldRenderer.pos((double) right, (double) top, 0.0D).endVertex();
        worldRenderer.pos((double) left, (double) top, 0.0D).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor,
                                         int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        rectangle(x, y, x + width, y1, borderColor);
        rectangle(x1 - width, y, x1, y1, borderColor);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void drawRoundedCornerRect(float x, float y, float x1, float y1, float radius, int color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        final boolean hasCull = glIsEnabled(GL_CULL_FACE);
        glDisable(GL_CULL_FACE);

        glColor(color);
        drawRoundedCornerRect(x, y, x1, y1, radius);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        setGlState(GL_CULL_FACE, hasCull);
    }
    public static int loadGlTexture(BufferedImage bufferedImage){
        int textureId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bufferedImage.getWidth(), bufferedImage.getHeight(),
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, ImageUtils.readImageToBuffer(bufferedImage));

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return textureId;
    }
    public static void drawShadowWithCustomAlpha(float x, float y, float width, float height, float alpha) {
        drawTexturedRectWithCustomAlpha(x - 9, y - 9, 9, 9, "paneltopleft", alpha);
        drawTexturedRectWithCustomAlpha(x - 9, y + height, 9, 9, "panelbottomleft", alpha);
        drawTexturedRectWithCustomAlpha(x + width, y + height, 9, 9, "panelbottomright", alpha);
        drawTexturedRectWithCustomAlpha(x + width, y - 9, 9, 9, "paneltopright", alpha);
        drawTexturedRectWithCustomAlpha(x - 9, y, 9, height, "panelleft", alpha);
        drawTexturedRectWithCustomAlpha(x + width, y, 9, height, "panelright", alpha);
        drawTexturedRectWithCustomAlpha(x, y - 9, width, 9, "paneltop", alpha);
        drawTexturedRectWithCustomAlpha(x, y + height, width, 9, "panelbottom", alpha);
    }
    public static void drawRoundRect(float x0, float y0, float x1, float y1,float radius,int color) {
        if(x0 == x1 || y0 == y1) return;
        final int Semicircle = 18;
        final float f = 90.0f / Semicircle;
        final float f2 = (color >> 24 & 0xFF) / 255.0f;
        final float f3 = (color >> 16 & 0xFF) / 255.0f;
        final float f4 = (color >> 8 & 0xFF) / 255.0f;
        final float f5 = (color & 0xFF) / 255.0f;
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(f3, f4, f5, f2);
        GL11.glBegin(5);
        GL11.glVertex2f(x0 + radius, y0);
        GL11.glVertex2f(x0 + radius, y1);
        GL11.glVertex2f(x1 - radius, y0);
        GL11.glVertex2f(x1 - radius, y1);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(x0, y0 + radius);
        GL11.glVertex2f(x0 + radius, y0 + radius);
        GL11.glVertex2f(x0, y1 - radius);
        GL11.glVertex2f(x0 + radius, y1 - radius);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(x1, y0 + radius);
        GL11.glVertex2f(x1 - radius, y0 + radius);
        GL11.glVertex2f(x1, y1 - radius);
        GL11.glVertex2f(x1 - radius, y1 - radius);
        GL11.glEnd();
        GL11.glBegin(6);
        float f6 = x1 - radius;
        float f7 = y0 + radius;
        GL11.glVertex2f(f6, f7);
        int j = 0;
        for (j = 0; j <= Semicircle; ++j) {
            final float f8 = j * f;
            GL11.glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(f8))),
                    (float) (f7 - radius * Math.sin(Math.toRadians(f8))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x0 + radius;
        f7 = y0 + radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= Semicircle; ++j) {
            final float f9 = j * f;
            GL11.glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(f9))),
                    (float) (f7 - radius * Math.sin(Math.toRadians(f9))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x0 + radius;
        f7 = y1 - radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= Semicircle; ++j) {
            final float f10 = j * f;
            GL11.glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(f10))),
                    (float) (f7 + radius * Math.sin(Math.toRadians(f10))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = x1 - radius;
        f7 = y1 - radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= Semicircle; ++j) {
            final float f11 = j * f;
            GL11.glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(f11))),
                    (float) (f7 + radius * Math.sin(Math.toRadians(f11))));
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawRoundRect(float x, float y, float x1, float y1, int color) {
        drawRoundedRect(x, y, x1, y1, color, color);
        GlStateManager.color(1,1,1);
    }
    public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
        enableGL2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
        drawVLine((x1 *= 2.0f) - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
        drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
        disableGL2D();
        Gui.drawRect(0, 0, 0, 0, 0);
    }
    public static void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
    }

    public static double getAnimationState(double n, final double n2, final double n3) {
        final float n4 = (float)(RenderUtils.deltaTime * n3);
        if (n < n2) {
            if (n + n4 < n2) {
                n += n4;
            }else {
                n = n2;
            }
        }else if (n - n4 > n2) {
            n -= n4;
        }else {
            n = n2;
        }
        return n;
    }
    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    public static void drawHLine(float par1, float par2, float par3, int par4) {
        if (par2 < par1) {
            float var5 = par1;
            par1 = par2;
            par2 = var5;
        }
        drawRect(par1, par3, par2 + 1.0f, par3 + 1.0f, par4);
    }
    public static void drawVLine(float x, float y, float x1, int y1) {
        if (x1 < y) {
            float var5 = y;
            y = x1;
            x1 = var5;
        }
        drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
    }
    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }
    public static int height() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }
    public static void drawFilledCircle1(int xx, int yy, float radius, int col) {
        float f = (float) (col >> 24 & 255) / 255.0f;
        float f1 = (float) (col >> 16 & 255) / 255.0f;
        float f2 = (float) (col >> 8 & 255) / 255.0f;
        float f3 = (float) (col & 255) / 255.0f;
        int sections = 50;
        double dAngle = 6.283185307179586 / (double) sections;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(6);
        int i = 0;
        while (i < sections) {
            float x = (float) ((double) radius * Math.sin((double) i * dAngle));
            float y = (float) ((double) radius * Math.cos((double) i * dAngle));
            GL11.glColor4f(f1, f2, f3, f);
            GL11.glVertex2f((float) xx + x, (float) yy + y);
            ++i;
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawFilledCircle1(float xx, float yy, float radius, int col) {
        float f = (float) (col >> 24 & 255) / 255.0f;
        float f1 = (float) (col >> 16 & 255) / 255.0f;
        float f2 = (float) (col >> 8 & 255) / 255.0f;
        float f3 = (float) (col & 255) / 255.0f;
        int sections = 50;
        double dAngle = 6.283185307179586 / (double) sections;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glBegin(6);
        int i = 0;
        while (i < sections) {
            float x = (float) ((double) radius * Math.sin((double) i * dAngle));
            float y = (float) ((double) radius * Math.cos((double) i * dAngle));
            GL11.glColor4f(f1, f2, f3, f);
            GL11.glVertex2f(xx + x, yy + y);
            ++i;
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawFilledCircle1(final int xx, final int yy, final float radius, final Color color) {
        int sections = 50;
        double dAngle = 2 * Math.PI / sections;
        float x, y;

        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < sections; i++) {
            x = (float) (radius * Math.sin((i * dAngle)));
            y = (float) (radius * Math.cos((i * dAngle)));

            glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
            glVertex2f(xx + x, yy + y);
        }
        GlStateManager.color(0, 0, 0);

        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }
    public static void drawCustomImage(final int x, final int y, final int width, final int height,
                                       final ResourceLocation image) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    public static void drawTexturedRectWithCustomAlpha(float x, float y, float width, float height, String image, float alpha) {
        glPushMatrix();
        final boolean enableBlend = glIsEnabled(GL_BLEND);
        final boolean disableAlpha = !glIsEnabled(GL_ALPHA_TEST);
        if(!enableBlend) glEnable(GL_BLEND);
        if(!disableAlpha) glDisable(GL_ALPHA_TEST);
        GlStateManager.color(1F, 1F, 1F, alpha);
        mc.getTextureManager().bindTexture2(new ResourceLocation("liquidbounce/shadow/" + image + ".png"));
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        if(!enableBlend) glDisable(GL_BLEND);
        if(!disableAlpha) glEnable(GL_ALPHA_TEST);
        GlStateManager.resetColor();
        glPopMatrix();
    }
    public static void drawImage3(ResourceLocation image, float x, float y, int width, int height, float r, float g, float b, float al) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(r, g, b, al);
        glTranslatef(x, y, x);
        mc.getTextureManager().bindTexture2((image));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
        glTranslatef(-x, -y, -x);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }
    public static void drawShadow(int x, int y, int width, int height) {
        ScaledResolution sr = new ScaledResolution((Minecraft) mc);
        drawTexturedRect(x - 9, y - 9, 9, 9, "paneltopleft", sr);
        drawTexturedRect(x - 9, y + height, 9, 9, "panelbottomleft", sr);
        drawTexturedRect(x + width, y + height, 9, 9, "panelbottomright", sr);
        drawTexturedRect(x + width, y - 9, 9, 9, "paneltopright", sr);
        drawTexturedRect(x - 9, y, 9, height, "panelleft", sr);
        drawTexturedRect(x + width, y, 9, height, "panelright", sr);
        drawTexturedRect(x, y - 9, width, 9, "paneltop", sr);
        drawTexturedRect(x, y + height, width, 9, "panelbottom", sr);
    }
    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    public static void drawShadow2(float x, float y, float width, float height) {
        drawTexturedRect(x - 9, y - 9, 9, 9, "paneltopleft");
        drawTexturedRect(x - 9, y + height, 9, 9, "panelbottomleft");
        drawTexturedRect(x + width, y + height, 9, 9, "panelbottomright");
        drawTexturedRect(x + width, y - 9, 9, 9, "paneltopright");
        drawTexturedRect(x - 9, y, 9, height, "panelleft");
        drawTexturedRect(x + width, y, 9, height, "panelright");
        drawTexturedRect(x, y - 9, width, 9, "paneltop");
        drawTexturedRect(x, y + height, width, 9, "panelbottom");
    }
    public static void drawTexturedRect(float x, float y, float width, float height, String image) {
        glPushMatrix();
        final boolean enableBlend = glIsEnabled(GL_BLEND);
        final boolean disableAlpha = !glIsEnabled(GL_ALPHA_TEST);
        if (!enableBlend) glEnable(GL_BLEND);
        if (!disableAlpha) glDisable(GL_ALPHA_TEST);
        mc.getTextureManager().bindTexture2(new ResourceLocation("liquidbounce/shadow/" + image + ".png"));
        GlStateManager.color(1F, 1F, 1F, 1F);
        RenderUtils.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        if (!enableBlend) glDisable(GL_BLEND);
        if (!disableAlpha) glEnable(GL_ALPHA_TEST);
        glPopMatrix();
    }
    public static void drawEntityKillAuraESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glLineWidth((float)lineWdith);
        GL11.glColor4f((float)lineRed, (float)lineGreen, (float)lineBlue, (float)lineAlpha);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static int getRainbowOpaque(int seconds, float saturation, float brightness, int index) {
        float hue = ((System.currentTimeMillis() + index) % (int) (seconds * 1000)) / (float) (seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }
    public static int SkyRainbow(int var2, float st, float bright) {
        double v1 = Math.ceil(System.currentTimeMillis() + (long) (var2 * 109)) / 5;
        return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), st, bright).getRGB();
    }
    public static void drawTexturedRect(int x, int y, int width, int height, String image, ScaledResolution sr) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        mc.getTextureManager().bindTexture2(new ResourceLocation("liquidbounce/shadow/" + image + ".png"));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GL11.glPopMatrix();
    }
    public static Color getGradientOffset(Color color1, Color color2, double offset) {
        double inverse_percent;
        int redPart;
        if(offset > 1.0D) {
            inverse_percent = offset % 1.0D;
            redPart = (int)offset;
            offset = redPart % 2 == 0?inverse_percent:1.0D - inverse_percent;
        }
        inverse_percent = 1.0D - offset;
        redPart = (int)((double)color1.getRed() * inverse_percent + (double)color2.getRed() * offset);
        int greenPart = (int)((double)color1.getGreen() * inverse_percent + (double)color2.getGreen() * offset);
        int bluePart = (int)((double)color1.getBlue() * inverse_percent + (double)color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }
    public static int Astolfo(int var2) {
        double v1 = Math.ceil(System.currentTimeMillis() + (long) (var2 * 109)) / 5;
        return Color.getHSBColor((double) ((float) ((v1 %= 360.0) / 360.0)) < 0.5 ? -((float) (v1 / 360.0)) : (float) (v1 / 360.0), 0.5F, 1.0F).getRGB();
    }
    public static int Astolfo(int var2, float bright, float st, int index, int offset, float client) {
        double rainbowDelay = Math.ceil(System.currentTimeMillis() + (long) (var2 * index)) / offset;
        return Color.getHSBColor((double) ((float) ((rainbowDelay %= client) / client)) < 0.5 ? -((float) (rainbowDelay / client)) : (float) (rainbowDelay / client), st, bright).getRGB();
    }
    public static int getRainbow(int index, int offset, float bright, float st) {
        float hue = (System.currentTimeMillis() + ((long) offset * index)) % 2000L;
        return Color.getHSBColor((float) (hue /= 2000L), st, bright).getRGB();
    }
    public static void drawGradientSidewaysH(double left, double top, double right, double bottom, int col1, int col2) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);

        quickDrawGradientSidewaysH(left, top, right, bottom, col1, col2);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    public static void quickDrawGradientSidewaysH(double left, double top, double right, double bottom, int col1, int col2) {
        glBegin(GL_QUADS);

        glColor(col1);
        glVertex2d(left, top);
        glVertex2d(left, bottom);
        glColor(col2);
        glVertex2d(right, bottom);
        glVertex2d(right, top);

        glEnd();
    }
    public static Color reAlpha(Color cIn, float alpha){
        return new Color(cIn.getRed()/255f,cIn.getGreen()/255f,cIn.getBlue()/255f,cIn.getAlpha() / 255f * alpha);
    }

    public static int reAlpha(final int n, final float n2) {
        final Color color = new Color(n);
        return new Color(0.003921569f * color.getRed(), 0.003921569f * color.getGreen(), 0.003921569f * color.getBlue(), n2).getRGB();
    }
    public static void drawGradientSidewaysV(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f2 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        float f4 = (float) (col2 >> 24 & 255) / 255.0f;
        float f5 = (float) (col2 >> 16 & 255) / 255.0f;
        float f6 = (float) (col2 >> 8 & 255) / 255.0f;
        float f7 = (float) (col2 & 255) / 255.0f;
        GL11.glEnable((int) 3042);
        GL11.glDisable((int) 3553);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glEnable((int) 2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
        GL11.glVertex2d((double) left, (double) bottom);
        GL11.glVertex2d((double) right, (double) bottom);
        GL11.glColor4f((float) f5, (float) f6, (float) f7, (float) f4);
        GL11.glVertex2d((double) right, (double) top);
        GL11.glVertex2d((double) left, (double) top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int) 3553);
        GL11.glDisable((int) 3042);
        GL11.glDisable((int) 2848);
        GL11.glShadeModel((int) 7424);
        Gui.drawRect(0, 0, 0, 0, 0);
    }
    public static void quickDrawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        glPushMatrix();
        glBegin(7);
        glColor4f(f1, f2, f3, f);
        glVertex2d(left, top);
        glVertex2d(left, bottom);

        glColor4f(f5, f6, f7, f4);
        glVertex2d(right, bottom);
        glVertex2d(right, top);
        glEnd();
        glPopMatrix();
    }
    public static void drawGradientSideways2(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float)(col1 >> 24 & 255) / 255.0F;
        float f1 = (float)(col1 >> 16 & 255) / 255.0F;
        float f2 = (float)(col1 >> 8 & 255) / 255.0F;
        float f3 = (float)(col1 & 255) / 255.0F;
        float f4 = (float)(col2 >> 24 & 255) / 255.0F;
        float f5 = (float)(col2 >> 16 & 255) / 255.0F;
        float f6 = (float)(col2 >> 8 & 255) / 255.0F;
        float f7 = (float)(col2 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    public static void drawBlockBox(final WBlockPos blockPos, final Color color, final boolean outline) {
        final IRenderManager renderManager = mc.getRenderManager();
        final ITimer timer = mc.getTimer();

        final double x = blockPos.getX() - renderManager.getRenderPosX();
        final double y = blockPos.getY() - renderManager.getRenderPosY();
        final double z = blockPos.getZ() - renderManager.getRenderPosZ();

        IAxisAlignedBB axisAlignedBB = classProvider.createAxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        final IBlock block = BlockUtils.getBlock(blockPos);

        if (block != null) {
            final IEntityPlayer player = mc.getThePlayer();

            final double posX = player.getLastTickPosX() + (player.getPosX() - player.getLastTickPosX()) * (double) timer.getRenderPartialTicks();
            final double posY = player.getLastTickPosY() + (player.getPosY() - player.getLastTickPosY()) * (double) timer.getRenderPartialTicks();
            final double posZ = player.getLastTickPosZ() + (player.getPosZ() - player.getLastTickPosZ()) * (double) timer.getRenderPartialTicks();

            if (Backend.MINECRAFT_VERSION_MINOR < 12) {
                block.setBlockBoundsBasedOnState(mc.getTheWorld(), blockPos);
            }

            axisAlignedBB = block.getSelectedBoundingBox(mc.getTheWorld(), mc.getTheWorld().getBlockState(blockPos), blockPos)
                    .expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
                    .offset(-posX, -posY, -posZ);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() != 255 ? color.getAlpha() : outline ? 26 : 35);
        drawFilledBox(axisAlignedBB);

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color);

            drawSelectionBoundingBox(axisAlignedBB);
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDepthMask(true);
        resetCaps();
    }
    public static void drawImage2(ResourceLocation image, int x, int y, int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture2((ResourceLocation) image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }
    public static void glColor1(int color) {

    }
    public static void enableSmoothLine(float width) {
        GL11.glDisable((int) 3008);
        GL11.glEnable((int) 3042);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glDisable((int) 3553);
        GL11.glDisable((int) 2929);
        GL11.glDepthMask((boolean) false);
        GL11.glEnable((int) 2884);
        GL11.glEnable((int) 2848);
        GL11.glHint((int) 3154, (int) 4354);
        GL11.glHint((int) 3155, (int) 4354);
        GL11.glLineWidth((float) width);
    }
    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    public static void drawSelectionBoundingBox(IAxisAlignedBB boundingBox) {
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_LINE_STRIP, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION));

        // Lower Rectangle
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();

        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMaxZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()).endVertex();

        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMinZ()).endVertex();
        worldrenderer.pos(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMinZ()).endVertex();

        tessellator.draw();
    }

    public static void drawEntityBox(final IEntity entity, final Color color, final boolean outline) {
        final IRenderManager renderManager = mc.getRenderManager();
        final ITimer timer = mc.getTimer();

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        final double x = entity.getLastTickPosX() + (entity.getPosX() - entity.getLastTickPosX()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosX();
        final double y = entity.getLastTickPosY() + (entity.getPosY() - entity.getLastTickPosY()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosY();
        final double z = entity.getLastTickPosZ() + (entity.getPosZ() - entity.getLastTickPosZ()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosZ();

        final IAxisAlignedBB entityBox = entity.getEntityBoundingBox();
        final IAxisAlignedBB axisAlignedBB = classProvider.createAxisAlignedBB(
                entityBox.getMinX() - entity.getPosX() + x - 0.05D,
                entityBox.getMinY() - entity.getPosY() + y,
                entityBox.getMinZ() - entity.getPosZ() + z - 0.05D,
                entityBox.getMaxX() - entity.getPosX() + x + 0.05D,
                entityBox.getMaxY() - entity.getPosY() + y + 0.15D,
                entityBox.getMaxZ() - entity.getPosZ() + z + 0.05D
        );

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color.getRed(), color.getGreen(), color.getBlue(), 95);
            drawSelectionBoundingBox(axisAlignedBB);
        }

        glColor(color.getRed(), color.getGreen(), color.getBlue(), outline ? 26 : 35);
        drawFilledBox(axisAlignedBB);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glDepthMask(true);
        resetCaps();
    }

    public static void drawAxisAlignedBB(final IAxisAlignedBB axisAlignedBB, final Color color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glLineWidth(2F);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glColor(color);
        drawFilledBox(axisAlignedBB);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    public static void drawPlatform(final double y, final Color color, final double size) {
        final IRenderManager renderManager = mc.getRenderManager();
        final double renderY = y - renderManager.getRenderPosY();

        drawAxisAlignedBB(classProvider.createAxisAlignedBB(size, renderY + 0.02D, size, -size, renderY, -size), color);
    }

    public static void drawPlatform(final IEntity entity, final Color color) {
        final IRenderManager renderManager = mc.getRenderManager();
        final ITimer timer = mc.getTimer();

        final double x = entity.getLastTickPosX() + (entity.getPosX() - entity.getLastTickPosX()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosX();
        final double y = entity.getLastTickPosY() + (entity.getPosY() - entity.getLastTickPosY()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosY();
        final double z = entity.getLastTickPosZ() + (entity.getPosZ() - entity.getLastTickPosZ()) * timer.getRenderPartialTicks()
                - renderManager.getRenderPosZ();

        final IAxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox()
                .offset(-entity.getPosX(), -entity.getPosY(), -entity.getPosZ())
                .offset(x, y, z);

        drawAxisAlignedBB(
                classProvider.createAxisAlignedBB(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY() + 0.2, axisAlignedBB.getMinZ(), axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY() + 0.26, axisAlignedBB.getMaxZ()),
                color
        );
    }
    public static void drawFilledCircle2(final IEntity entity, final Color color) {
        int sections = 50;
        double dAngle = 2 * Math.PI / sections;
        float x, y;

        glPushAttrib(GL_ENABLE_BIT);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < sections; i++) {
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        glEnd();

        glPopAttrib();
    }
    public static void drawFilledBox(final IAxisAlignedBB axisAlignedBB) {
        final ITessellator tessellator = classProvider.getTessellatorInstance();
        final IWorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION));

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();

        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMinX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMinZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMaxY(), axisAlignedBB.getMaxZ()).endVertex();
        worldRenderer.pos(axisAlignedBB.getMaxX(), axisAlignedBB.getMinY(), axisAlignedBB.getMaxZ()).endVertex();
        tessellator.draw();
    }

    public static void quickDrawRect(final float x, final float y, final float x2, final float y2) {
        glBegin(GL_QUADS);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void drawRect(final float x, final float y, final float x2, final float y2, final int color) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glColor(color);
        glBegin(GL_QUADS);

        glVertex2f(x2, y);
        glVertex2f(x, y);
        glVertex2f(x, y2);
        glVertex2f(x2, y2);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    public static void drawRect(final int x, final int y, final int x2, final int y2, final int color) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glColor(color);
        glBegin(GL_QUADS);

        glVertex2i(x2, y);
        glVertex2i(x, y);
        glVertex2i(x, y2);
        glVertex2i(x2, y2);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    /**
     * Like {@link #drawRect(float, float, float, float, int)}, but without setup
     */
    public static void quickDrawRect(final float x, final float y, final float x2, final float y2, final int color) {
        glColor(color);
        glBegin(GL_QUADS);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void drawRect(final float x, final float y, final float x2, final float y2, final Color color) {
        drawRect(x, y, x2, y2, color.getRGB());
    }

    public static void drawBorderedRect(final float x, final float y, final float x2, final float y2, final float width,
                                        final int color1, final int color2) {
        drawRect(x, y, x2, y2, color2);
        drawBorder(x, y, x2, y2, width, color1);
    }

    public static void drawBorder(float x, float y, float x2, float y2, float width, int color1) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glColor(color1);
        glLineWidth(width);

        glBegin(GL_LINE_LOOP);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    public static void quickDrawBorderedRect(final float x, final float y, final float x2, final float y2, final float width, final int color1, final int color2) {
        quickDrawRect(x, y, x2, y2, color2);

        glColor(color1);
        glLineWidth(width);

        glBegin(GL_LINE_LOOP);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void drawLoadingCircle(float x, float y) {
        for (int i = 0; i < 4; i++) {
            int rot = (int) ((System.nanoTime() / 5000000 * i) % 360);
            drawCircle(x, y, i * 10, rot - 180, rot);
        }
    }

    public static void drawCircle(float x, float y, float radius, int start, int end) {
        classProvider.getGlStateManager().enableBlend();
        classProvider.getGlStateManager().disableTexture2D();
        classProvider.getGlStateManager().tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor(Color.WHITE);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2F);
        glBegin(GL_LINE_STRIP);
        for (float i = end; i >= start; i -= (360 / 90.0f)) {
            glVertex2f((float) (x + (cos(i * PI / 180) * (radius * 1.001F))), (float) (y + (sin(i * PI / 180) * (radius * 1.001F))));
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);

        classProvider.getGlStateManager().enableTexture2D();
        classProvider.getGlStateManager().disableBlend();
    }

    public static void drawFilledCircle(final int xx, final int yy, final float radius, final Color color) {
        int sections = 50;
        double dAngle = 2 * Math.PI / sections;
        float x, y;

        glPushAttrib(GL_ENABLE_BIT);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < sections; i++) {
            x = (float) (radius * Math.sin((i * dAngle)));
            y = (float) (radius * Math.cos((i * dAngle)));

            glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
            glVertex2f(xx + x, yy + y);
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        glEnd();

        glPopAttrib();
    }

    public static void drawImage(IResourceLocation image, int x, int y, int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }
    public static void drawImage4(ResourceLocation image, int x, int y, int width, int height) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture2(image);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }
    public static void glColor(final Color color, final float alpha) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }
    /**
     * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height, textureWidth, textureHeight
     */

    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION_TEX));
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) width) * f, (v + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GL11.glColor4f(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void glColor(final Color color) {
        glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static void glColor(final int hex) {
        glColor(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, hex >> 24 & 0xFF);
    }


    public static void glColo2(final Color color, final float alpha) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static void glColor2(final int hex, final int alpha) {
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha / 255F);
    }

    public static void glColor2(final int hex, final float alpha) {
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }
    public static void draw2D(final IEntityLivingBase entity, final double posX, final double posY, final double posZ, final int color, final int backgroundColor) {
        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotated(-mc.getRenderManager().getPlayerViewY(), 0F, 1F, 0F);
        GL11.glScaled(-0.1D, -0.1D, 0.1D);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDepthMask(true);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[0]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[1]);

        GL11.glTranslated(0, 21 + -(entity.getEntityBoundingBox().getMaxY() - entity.getEntityBoundingBox().getMinY()) * 12, 0);

        glColor(color);
        glCallList(DISPLAY_LISTS_2D[2]);

        glColor(backgroundColor);
        glCallList(DISPLAY_LISTS_2D[3]);

        // Stop render
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        GL11.glPopMatrix();
    }
    public static void glColor3(final int red, final int green, final int blue, final int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    public static void glColo3(final Color color) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;
        final float alpha = color.getAlpha() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static void glColor3(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255F;
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }
    public static void startDrawing() {
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, (int) 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        Minecraft.getMinecraft().entityRenderer.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks,
                0);
    }
    public static void draw2D(final WBlockPos blockPos, final int color, final int backgroundColor) {
        final IRenderManager renderManager = mc.getRenderManager();

        final double posX = (blockPos.getX() + 0.5) - renderManager.getRenderPosX();
        final double posY = blockPos.getY() - renderManager.getRenderPosY();
        final double posZ = (blockPos.getZ() + 0.5) - renderManager.getRenderPosZ();

        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotated(-mc.getRenderManager().getPlayerViewY(), 0F, 1F, 0F);
        GL11.glScaled(-0.1D, -0.1D, 0.1D);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDepthMask(true);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[0]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[1]);

        GL11.glTranslated(0, 9, 0);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[2]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[3]);

        // Stop render
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        GL11.glPopMatrix();
    }

    public static void renderNameTag(final String string, final double x, final double y, final double z) {
        final IRenderManager renderManager = mc.getRenderManager();

        glPushMatrix();
        glTranslated(x - renderManager.getRenderPosX(), y - renderManager.getRenderPosY(), z - renderManager.getRenderPosZ());
        glNormal3f(0F, 1F, 0F);
        glRotatef(-mc.getRenderManager().getPlayerViewY(), 0F, 1F, 0F);
        glRotatef(mc.getRenderManager().getPlayerViewX(), 1F, 0F, 0F);
        glScalef(-0.05F, -0.05F, 0.05F);
        setGlCap(GL_LIGHTING, false);
        setGlCap(GL_DEPTH_TEST, false);
        setGlCap(GL_BLEND, true);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        final int width = Fonts.font35.getStringWidth(string) / 2;

        drawRect(-width - 1, -1, width + 1, Fonts.font35.getFontHeight(), Integer.MIN_VALUE);
        Fonts.font35.drawString(string, -width, 1.5F, Color.WHITE.getRGB(), true);

        resetCaps();
        glColor4f(1F, 1F, 1F, 1F);
        glPopMatrix();
    }

    public static void drawLine(final double x, final double y, final double x1, final double y1, final float width) {
        glDisable(GL_TEXTURE_2D);
        glLineWidth(width);
        glBegin(GL_LINES);
        glVertex2d(x, y);
        glVertex2d(x1, y1);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void makeScissorBox(final float x, final float y, final float x2, final float y2) {
        final IScaledResolution scaledResolution = classProvider.createScaledResolution(mc);
        final int factor = scaledResolution.getScaleFactor();
        glScissor((int) (x * factor), (int) ((scaledResolution.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }

    /**
     * GL CAP MANAGER
     * <p>
     * TODO: Remove gl cap manager and replace by something better
     */

    public static void resetCaps() {
        glCapMap.forEach(RenderUtils::setGlState);
    }

    public static void enableGlCap(final int cap) {
        setGlCap(cap, true);
    }

    public static void enableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, true);
    }

    public static void disableGlCap(final int cap) {
        setGlCap(cap, true);
    }

    public static void disableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, false);
    }

    public static void setGlCap(final int cap, final boolean state) {
        glCapMap.put(cap, glGetBoolean(cap));
        setGlState(cap, state);
    }

    public static void setGlState(final int cap, final boolean state) {
        if (state)
            glEnable(cap);
        else
            glDisable(cap);
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        ITessellator tessellator = classProvider.getTessellatorInstance();
        IWorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, classProvider.getVertexFormatEnum(WDefaultVertexFormats.POSITION_TEX));
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }
}