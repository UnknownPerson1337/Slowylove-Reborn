package net.ccbluex.liquidbounce.features.module.modules.fun;

//import just.monika.主播你有反编译我代码的时间还不如自己写一个端子.DokiClient;
//import just.monika.主播你有反编译我代码的时间还不如自己写一个端子.module.Category;
//import just.monika.主播你有反编译我代码的时间还不如自己写一个端子.module.Module;
//import just.monika.主播你有反编译我代码的时间还不如自己写一个端子.utils.render.RenderUtil;
//import just.monika.主播你有反编译我代码的时间还不如自己写一个端子.utils.render.blur.BloomUtil;
//import just.monika.主播你有反编译我代码的时间还不如自己写一个端子.utils.render.blur.GaussianBlur;
//import just.monika.主播你有反编译我代码的时间还不如自己写一个端子.utils.render.blur.KawaseBlur;
//import just.monika.反编译我代码油饼食不食.event.impl.render.ShaderEvent;
//import just.monika.反编译我代码油饼食不食.settings.ParentAttribute;
//import just.monika.反编译我代码油饼食不食.settings.impl.BooleanSetting;
//import just.monika.反编译我代码油饼食不食.settings.impl.ModeSetting;
//import just.monika.反编译我代码油饼食不食.settings.impl.IntegerValue;
//import just.monika.反编译我代码油饼食不食.utils.render.StencilUtil;

import me.utils.GaussianBlur;
import me.utils.KawaseBlur;
import me.utils.StencilUtil;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

@ModuleInfo(name="BlurModule",description = "nmsl",category = ModuleCategory.FUN)
public class BlurModule extends Module {

    private final BoolValue blur = new BoolValue("Blur", true);
    private final ListValue blurMode = new ListValue("Blur Mode", new String[]{"Kawase", "Gaussian"} ,"Gaussian");
    private final IntegerValue radius = new IntegerValue("Blur Radius", 10, 1, 50);
    private final IntegerValue iterations = new IntegerValue("Blur Iterations", 4, 1, 15);
    private final IntegerValue offset = new IntegerValue("Blur Offset", 3, 1, 20);
    private final BoolValue shadow = new BoolValue("Shadow", true);
    private final IntegerValue shadowRadius = new IntegerValue("Shadow Radius", 6, 1, 20);
    private final IntegerValue shadowOffset = new IntegerValue("Shadow Offset", 2, 1, 15);

//    public BlurModule() {
//        super("Blur", Category.RENDER, "blurs shit");
//        blurMode.addParent(blur, ParentAttribute.BOOLEAN_CONDITION);
//        iterations.addParent(blurMode, modeSetting -> modeSetting.is("Kawase"));
//        offset.addParent(blurMode, modeSetting -> modeSetting.is("Kawase"));
//        radius.addParent(blurMode, modeSetting -> !modeSetting.is("Kawase"));
//        shadowRadius.addParent(shadow, ParentAttribute.BOOLEAN_CONDITION);
//        shadowOffset.addParent(shadow, ParentAttribute.BOOLEAN_CONDITION);
//        addSettings(blur, blurMode, radius, iterations, offset, shadow, shadowRadius, shadowOffset);
//    }

    private String currentMode;

    @Override
    public void onEnable() {
        currentMode = blurMode.get();
      //  RoundedUtil.roundedOutlineShader = new ShaderUtil("DokiClient/Shaders/roundRectOutline.frag");
        super.onEnable();
    }

    public void stuffToBlur(boolean bloom) {

       // Gui.drawRect2(40, 40, 400, 40, -1);

    }


    private Framebuffer bloomFramebuffer = new Framebuffer(1,1, false);

    public void blurScreen() {
//        if (!toggled) return;
        if (!currentMode.equals(blurMode.get())) {
            currentMode = blurMode.get();
        }
        if (blur.get()) {
            StencilUtil.initStencilToWrite();
//            DokiClient.INSTANCE.getEventProtocol().dispatch(new ShaderEvent(false));
            stuffToBlur(false);
            StencilUtil.readStencilBuffer(1);

            switch (currentMode) {
                case "Gaussian":
                    GaussianBlur.renderBlur(radius.getValue().floatValue());
                    break;
                case "Kawase":
                    KawaseBlur.renderBlur(iterations.getValue().intValue(), offset.getValue().intValue());
                    break;
            }
            StencilUtil.uninitStencilBuffer();
        }


        if (shadow.get()) {
            bloomFramebuffer = RenderUtils.createFrameBuffer(bloomFramebuffer);

            bloomFramebuffer.framebufferClear();
            bloomFramebuffer.bindFramebuffer(true);
//            DokiClient.INSTANCE.getEventProtocol().dispatch(new ShaderEvent(true));
            stuffToBlur(true);
            bloomFramebuffer.unbindFramebuffer();
            RenderUtils.drawRect(0, 0, 100, 100, new Color(-2).getRGB());
            StencilUtil.readStencilBuffer(1);

//            BloomUtil.renderBlur(bloomFramebuffer.framebufferTexture, shadowRadius.getValue().intValue(), shadowOffset.getValue().intValue());
        }


    }


}
