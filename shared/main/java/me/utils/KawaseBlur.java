/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.shader.Framebuffer
 */
package me.utils;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.shader.Framebuffer;

import java.util.ArrayList;
import java.util.List;

import static me.utils.StencilUtil.mc;

public class KawaseBlur {
    public static ShaderUtil kawaseDown = new ShaderUtil("loserline/shaders/kawasedown.frag");
    public static ShaderUtil kawaseUp = new ShaderUtil("loserline/shaders/kawaseup.frag");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static int currentIterations;
    private static final List<Framebuffer> framebufferList;

    public static void setupUniforms(float offset) {
        kawaseDown.setUniformf("offset", offset, offset);
        kawaseUp.setUniformf("offset", offset, offset);
    }

    private static void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();
        framebufferList.add(RenderUtils.createFrameBuffer(framebuffer));
        int i = 1;
        while ((float)i <= iterations) {
            Framebuffer framebuffer;
            framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
            framebufferList.add(RenderUtils.createFrameBuffer(framebuffer));
            ++i;
        }
    }

    public static void renderBlur(int iterations, int offset) {
        int i;
        if (currentIterations != iterations) {
            KawaseBlur.initFramebuffers(iterations);
            currentIterations = iterations;
        }
        renderFBO(framebufferList.get(1), mc.getFramebuffer().framebufferTexture, kawaseDown, offset);
        for (i = 1; i < iterations; ++i) {
            renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, offset);
        }
        for (i = iterations; i > 1; --i) {
            renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, offset);
        }

        mc.getFramebuffer().bindFramebuffer(true);
        RenderUtils.bindTexture(KawaseBlur.framebufferList.get((int)1).framebufferTexture);
        kawaseUp.init();
        kawaseUp.setUniformf("offset", offset, offset);
        kawaseUp.setUniformf("halfpixel", 0.5f / mc.displayWidth, 0.5f / mc.displayHeight);
        kawaseUp.setUniformi("inTexture", 0);
        ShaderUtil.drawQuads();
        kawaseUp.unload();
    }

    private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtil shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shader.init();
        RenderUtils.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformf("halfpixel", 0.5f / mc.displayWidth, 0.5f / mc.displayHeight);
        ShaderUtil.drawQuads();
        ShaderUtil.drawQuads();
        shader.unload();
        framebuffer.unbindFramebuffer();
    }

    static {
        framebufferList = new ArrayList<Framebuffer>();
    }
}

