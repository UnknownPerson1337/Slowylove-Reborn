package me.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;

/* loaded from: LiquidBounce-b73.jar:net/ccbluex/liquidbounce/FontLoaders.class */
public abstract class FontLoaders {
    public static CFontRenderer F14 = new CFontRenderer(getFont(14), true, true);
    public static CFontRenderer F16 = new CFontRenderer(getFont(16), true, true);
    public static CFontRenderer F18 = new CFontRenderer(getFont(18), true, true);
    public static CFontRenderer F20 = new CFontRenderer(getFont(20), true, true);
    public static CFontRenderer F22 = new CFontRenderer(getFont(22), true, true);
    public static CFontRenderer F23 = new CFontRenderer(getFont(23), true, true);
    public static CFontRenderer F24 = new CFontRenderer(getFont(24), true, true);
    public static CFontRenderer F30 = new CFontRenderer(getFont(30), true, true);
    public static CFontRenderer F40 = new CFontRenderer(getFont(40), true, true);
    public static CFontRenderer F50 = new CFontRenderer(getFont(50), true, true);
    public static CFontRenderer C12 = new CFontRenderer(getComfortaa(12), true, true);
    public static CFontRenderer C14 = new CFontRenderer(getComfortaa(14), true, true);
    public static CFontRenderer C16 = new CFontRenderer(getComfortaa(16), true, true);
    public static CFontRenderer C18 = new CFontRenderer(getComfortaa(18), true, true);
    public static CFontRenderer C20 = new CFontRenderer(getComfortaa(20), true, true);
    public static CFontRenderer C35 = new CFontRenderer(getComfortaa(35), true, true);
    public static CFontRenderer C22 = new CFontRenderer(getComfortaa(22), true, true);
    public static CFontRenderer Logo = new CFontRenderer(getNovo(40), true, true);

    public static CFontRenderer SF18 = new CFontRenderer(getSF(18), true, true);
    public static CFontRenderer SF20= new CFontRenderer(getSF(20), true, true);

    public static CFontRenderer I10 = new CFontRenderer(getIcon(10), true, true);
    public static CFontRenderer I14 = new CFontRenderer(getIcon(14), true, true);
    public static CFontRenderer I15 = new CFontRenderer(getIcon(15), true, true);
    public static CFontRenderer I16 = new CFontRenderer(getIcon(16), true, true);
    public static CFontRenderer I18 = new CFontRenderer(getIcon(18), true, true);
    public static CFontRenderer I20 = new CFontRenderer(getIcon(20), true, true);
    public static CFontRenderer I25 = new CFontRenderer(getIcon(25), true, true);
    public static ArrayList<CFontRenderer> fonts = new ArrayList<>();

    public static CFontRenderer getFontRender(int size) {
        return fonts.get(size - 10);
    }

    public static void initFonts() {
        // 触发static加载font
    }

    public static Font getFont(int size) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/wqy_microhei.ttf")).getInputStream()).deriveFont(0, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    public static Font getComfortaa(int size) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/wqy_microhei.ttf")).getInputStream()).deriveFont(0, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    public static Font getIcon(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("icon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }
    public static Font getNovo(int size) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/nico.ttf")).getInputStream()).deriveFont(0, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    public static Font getSF(int size) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/fonts/sf.ttf")).getInputStream()).deriveFont(0, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
}
