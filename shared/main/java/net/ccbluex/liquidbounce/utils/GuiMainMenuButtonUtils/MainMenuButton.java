/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.GuiMainMenuButtonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class MainMenuButton extends GuiButton {

    public MainMenuButton(final int buttonId, final int x, final int y, final int width, final int height, final String buttonText) {
        super(buttonId, x, y, width, height, buttonText);
    }


    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {}
}
