package Linkow.utils;

import net.ccbluex.liquidbounce.api.minecraft.client.IMinecraft;
import net.ccbluex.liquidbounce.api.minecraft.util.IScaledResolution;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import static org.lwjgl.opengl.GL11.*;

public class MenuShader {
    protected static final IMinecraft mc = MinecraftInstance.mc;

    private ShaderProgram menuShader = new ShaderProgram("fragment/menu.frag");

    private int pass;

    public MenuShader(int pass) {
        this.pass = pass;
    }

    public void render(final IScaledResolution scaledResolution) {
        menuShader.init();
        setupUniforms();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        menuShader.renderCanvas(scaledResolution);
        menuShader.uninit();
        pass++;
    }

    public void setupUniforms() {
        menuShader.setUniformf("time", pass / 100f);
        menuShader.setUniformf("resolution", mc.getDisplayWidth(), mc.getDisplayHeight());
    }

}
