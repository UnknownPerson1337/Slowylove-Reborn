/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.render.shader.shaders;

import me.shader.Shader;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;

public final class BackgroundShader extends Shader {

    public final static me.shader.shaders.BackgroundShader BACKGROUND_SHADER = new me.shader.shaders.BackgroundShader();

    private float time;

    public BackgroundShader() {
        super("background.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("iResolution");
        setupUniform("iTime");
    }

    @Override
    public void updateUniforms() {
        final int resolutionID = getUniform("iResolution");
        if(resolutionID > -1)
            GL20.glUniform2f(resolutionID, (float) Display.getWidth(), (float) Display.getHeight());
        final int timeID = getUniform("iTime");
        if(timeID > -1) GL20.glUniform1f(timeID, time);

        time += 0.003F * RenderUtils.deltaTime;
    }

}
