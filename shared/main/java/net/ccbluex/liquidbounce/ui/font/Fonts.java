/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.font;

import com.google.gson.*;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Fonts extends MinecraftInstance {

    @FontDetails(fontName = "Minecraft Font")
    public static final IFontRenderer minecraftFont = mc.getFontRendererObj();
    private static final HashMap<FontInfo, IFontRenderer> CUSTOM_FONT_RENDERERS = new HashMap<>();

//    @FontDetails(fontName = "Roboto-Italic ", fontSize = 35)
//    public static IFontRenderer Italic35;
//
//    @FontDetails(fontName = "Roboto-Italic ", fontSize = 45)
//    public static IFontRenderer Italic45;
//
//    @FontDetails(fontName = "Roboto-Italic ", fontSize = 60)
//    public static IFontRenderer Italic60;
//
//    @FontDetails(fontName = "Roboto-Italic ", fontSize = 120)
//    public static IFontRenderer Italic120;
//
//    @FontDetails(fontName = "Roboto-Light.ttf ", fontSize = 35)
//    public static IFontRenderer Light35;
//
//    @FontDetails(fontName = "Roboto-Light.ttf ", fontSize = 45)
//    public static IFontRenderer Light45;
//
//    @FontDetails(fontName = "Roboto-Light.ttf ", fontSize = 60)
//    public static IFontRenderer Light60;
    @FontDetails(fontName = "flux", fontSize = 35)
    public static IFontRenderer flux;

    @FontDetails(fontName = "flux", fontSize = 50)
    public static IFontRenderer flux50;
    @FontDetails(fontName = "flux", fontSize = 14)
    public static IFontRenderer FluxIcon14;
    @FontDetails(fontName = "flux", fontSize = 16)
    public static IFontRenderer flux16;

    @FontDetails(fontName = "icomoon", fontSize = 35)
    public static IFontRenderer icon35;

    @FontDetails(fontName = "icomoon", fontSize = 50)
    public static IFontRenderer icon50;
    @FontDetails(fontName = "icomoon", fontSize = 14)
    public static IFontRenderer icon14;
    @FontDetails(fontName = "icomoon", fontSize = 16)
    public static IFontRenderer icon16;
    @FontDetails(fontName = "Bold", fontSize = 30)
        public static IFontRenderer bold30;
    @FontDetails(fontName = "Bold", fontSize = 40)
    public static IFontRenderer bold40;
    @FontDetails(fontName = "Bold", fontSize = 35)
    public static IFontRenderer bold35;
    @FontDetails(fontName = "Bold", fontSize = 45)
    public static IFontRenderer bold45;
    @FontDetails(fontName = "Bold", fontSize = 72)
    public static IFontRenderer bold72;
    @FontDetails(fontName = "Bold", fontSize = 180)
    public static IFontRenderer bold180;

    @FontDetails(fontName = "Roboto-Light.ttf ", fontSize = 120)
    public static IFontRenderer Light120;

    @FontDetails(fontName = "Roboto Medium", fontSize = 35)
    public static IFontRenderer font35;
    @FontDetails(fontName = "Roboto Medium", fontSize = 35)
    public static IFontRenderer font30;
    @FontDetails(fontName = "Roboto Medium", fontSize = 25)
    public static IFontRenderer font25;
    @FontDetails(fontName = "Roboto Medium", fontSize = 40)
    public static IFontRenderer font40;

    @FontDetails(fontName = "Roboto Bold", fontSize =72)
    public static IFontRenderer fontBold72;

    @FontDetails(fontName = "Roboto Bold", fontSize = 120)
    public static IFontRenderer fontBold120;

    @FontDetails(fontName = "Roboto Bold", fontSize = 180)
    public static IFontRenderer fontBold180;

    @FontDetails(fontName = "jello120 two ", fontSize = 120)
    public static IFontRenderer jello120;

    @FontDetails(fontName = "jello35", fontSize = 35)
    public static IFontRenderer jello35;

    @FontDetails(fontName = "jello35", fontSize = 37)
    public static IFontRenderer jello37;

    @FontDetails(fontName = "jello35", fontSize = 45)
    public static IFontRenderer jello45;

    @FontDetails(fontName = "jello60", fontSize = 60)
    public static IFontRenderer jello60;

    @FontDetails(fontName = "jello30", fontSize = 30)
    public static IFontRenderer jello30;

    @FontDetails(fontName = "jello72", fontSize = 72)
    public static IFontRenderer jello72;

    @FontDetails(fontName = "SFUI Regular", fontSize = 35)
    public static IFontRenderer fontSFUI35;

    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.getLogger().info("Loading Fonts.");

        downloadFonts();
//
//        Light35 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Light.ttf", 35)));
//        Light45= classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Light.ttf", 45)));
//        Light60 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Light.ttf", 60)));
        Light120 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Light.ttf", 120)));
//        Italic35 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Italic.ttf", 35)));
//        Italic45= classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Italic.ttf", 45)));
//        Italic60= classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Italic.ttf", 60)));
//        Italic120= classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Italic.ttf", 120)));
        font35 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 35)));
        font25 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 25)));
        font40 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 40)));

        font30 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 30)));
        fontBold72 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 72)));
        fontBold120 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 120)));
        fontBold180 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Bold.ttf", 180)));
        fontSFUI35 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("sfui.ttf", 35)));
        jello120 =classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/jello.ttf", 120)));
        jello35 =classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/jello.ttf", 35)));
        jello60=classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/jello.ttf", 60)));
        jello37=classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/jello.ttf", 37)));
        jello30=classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/jello.ttf", 30)));
        jello45=classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/jello.ttf", 45)));
        jello72 =classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/jello.ttf", 72)));

        FluxIcon14 = classProvider.wrapFontRenderer(new GameFontRenderer(getFlux(14)));
        flux16 = classProvider.wrapFontRenderer(new GameFontRenderer(getFlux(16)));
        flux = classProvider.wrapFontRenderer(new GameFontRenderer(getFlux(30)));
        flux50 = classProvider.wrapFontRenderer(new GameFontRenderer(getFlux(50)));;
        
        icon14 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/icon.ttf", 14)));
        icon16 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/icon.ttf", 16)));
        icon35 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/icon.ttf", 30)));
        icon50 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("liquidbounce/font/icon.ttf", 50)));;
        
        bold35 = classProvider.wrapFontRenderer(new GameFontRenderer(getBold(35)));
        bold40 = classProvider.wrapFontRenderer(new GameFontRenderer(getBold(40)));
        bold45 = classProvider.wrapFontRenderer(new GameFontRenderer(getBold(45)));
        bold30 = classProvider.wrapFontRenderer(new GameFontRenderer(getBold(30)));
        bold72 = classProvider.wrapFontRenderer(new GameFontRenderer(getBold(72)));
        bold180 = classProvider.wrapFontRenderer(new GameFontRenderer(getBold(180)));

        try {
            CUSTOM_FONT_RENDERERS.clear();

            final File fontsFile = new File(LiquidBounce.fileManager.fontsDir, "fonts.json");

            if (fontsFile.exists()) {
                final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(fontsFile)));

                if (jsonElement instanceof JsonNull)
                    return;

                final JsonArray jsonArray = (JsonArray) jsonElement;

                for (final JsonElement element : jsonArray) {
                    if (element instanceof JsonNull)
                        return;

                    final JsonObject fontObject = (JsonObject) element;

                    Font font = getFont(fontObject.get("fontFile").getAsString(), fontObject.get("fontSize").getAsInt());

                    CUSTOM_FONT_RENDERERS.put(new FontInfo(font), classProvider.wrapFontRenderer(new GameFontRenderer(font)));
                }
            } else {
                fontsFile.createNewFile();

                final PrintWriter printWriter = new PrintWriter(new FileWriter(fontsFile));
                printWriter.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonArray()));
                printWriter.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }
    private static Font getFlux(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/fluxicon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static void downloadFonts() {
        try {
            final File outputFile = new File(LiquidBounce.fileManager.fontsDir, "roboto.zip");

            if (!outputFile.exists()) {
                ClientUtils.getLogger().info("Downloading fonts...");
                HttpUtils.download("https://wysi-foundation.github.io/LiquidCloud/LiquidBounce/fonts/fonts.zip", outputFile);
                ClientUtils.getLogger().info("Extract fonts...");
                extractZip(outputFile.getPath(), LiquidBounce.fileManager.fontsDir.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Font getJello(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/jello.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    public static IFontRenderer getFontRenderer(final String name, final int size) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                Object o = field.get(null);

                if (o instanceof IFontRenderer) {
                    FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if (fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (IFontRenderer) o;
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return CUSTOM_FONT_RENDERERS.getOrDefault(new FontInfo(name, size), minecraftFont);
    }

    public static FontInfo getFontDetails(final IFontRenderer fontRenderer) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if (o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new FontInfo(fontDetails.fontName(), fontDetails.fontSize());
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<FontInfo, IFontRenderer> entry : CUSTOM_FONT_RENDERERS.entrySet()) {
            if (entry.getValue() == fontRenderer)
                return entry.getKey();
        }

        return null;
    }
    private static Font getBold(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/bold.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    public static List<IFontRenderer> getFonts() {
        final List<IFontRenderer> fonts = new ArrayList<>();

        for (final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if (fontObj instanceof IFontRenderer) fonts.add((IFontRenderer) fontObj);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS.values());

        return fonts;
    }

    private static Font getFont(final String fontName, final int size) {
        try {
            final InputStream inputStream = new FileInputStream(new File(LiquidBounce.fileManager.fontsDir, fontName));
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }

    private static void extractZip(final String zipFile, final String outputFolder) {
        final byte[] buffer = new byte[1024];

        try {
            final File folder = new File(outputFolder);

            if (!folder.exists()) folder.mkdir();

            final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(outputFolder + File.separator + zipEntry.getName());
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int i;
                while ((i = zipInputStream.read(buffer)) > 0)
                    fileOutputStream.write(buffer, 0, i);

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static class FontInfo {
        private final String name;
        private final int fontSize;

        public FontInfo(String name, int fontSize) {
            this.name = name;
            this.fontSize = fontSize;
        }

        public FontInfo(Font font) {
            this(font.getName(), font.getSize());
        }

        public String getName() {
            return name;
        }

        public int getFontSize() {
            return fontSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontInfo fontInfo = (FontInfo) o;

            if (fontSize != fontInfo.fontSize) return false;
            return Objects.equals(name, fontInfo.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + fontSize;
            return result;
        }
    }

}