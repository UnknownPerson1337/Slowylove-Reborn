/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.shader.Framebuffer
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL20
 */
package me.utils;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc;
import static org.lwjgl.opengl.GL11.*;

public class GaussianBlur {
    public static ShaderUtil blurShader = new ShaderUtil("loserline/shaders/gaussian.frag");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }
    public static void setupUniforms(float dir1, float dir2, float radius) {
        blurShader.setUniformi("textureIn", 0);
        blurShader.setUniformf("texelSize", 1.0f / (float)mc.getDisplayWidth(), 1.0f / (float)mc.getDisplayHeight());
        blurShader.setUniformf("direction", dir1, dir2);
        blurShader.setUniformf("radius", radius);
        FloatBuffer weightBuffer = BufferUtils.createFloatBuffer((int)256);
        int i = 0;
        while ((float)i <= radius) {
            weightBuffer.put(calculateGaussianValue(i, radius / 2.0f));
            ++i;
        }
        weightBuffer.rewind();
        GL20.glUniform1((int)blurShader.getUniform("weights"), (FloatBuffer)weightBuffer);
    }

    public static void renderBlur(float radius) {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        framebuffer = RenderUtils.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        blurShader.init();
        GaussianBlur.setupUniforms(1.0f, 0.0f, radius);
        RenderUtils.bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        blurShader.unload();
        mc.getFramebuffer().bindFramebuffer(true);
        blurShader.init();
        GaussianBlur.setupUniforms(0.0f, 1.0f, radius);
        RenderUtils.bindTexture(GaussianBlur.framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        blurShader.unload();
        RenderUtils.resetColor();
        GlStateManager.bindTexture(0);
    }
}

