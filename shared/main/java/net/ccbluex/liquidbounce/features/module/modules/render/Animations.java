
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;


@ModuleInfo(name = "Animations", description = "格挡动画",category = ModuleCategory.RENDER)
public class Animations extends Module{
    public static final FloatValue xValue = new FloatValue("Blocking-X", 0.0F, -2.0F, 2.0F);
    public static final FloatValue yValue = new FloatValue("Blocking-Y", 0.0F, -2.0F, 2.0F);
    public static final FloatValue zValue = new FloatValue("Blocking-Z", 0.0F, -2.0F, 2.0F);
    public static final FloatValue scaleValue = new FloatValue("Blocking-scale", 0.8F, 0.1F, 1.0F);
    public static final FloatValue xhValue = new FloatValue("Held-X", 0.0F, -2.0F, 2.0F);
    public static final FloatValue yhValue = new FloatValue("Held-Y", 0.0F, -2.0F, 2.0F);
    public static final FloatValue zhValue = new FloatValue("Held-Z", 0.0F, -2.0F, 2.0F);
    public static final FloatValue scalehValue = new FloatValue("Held-scale", 0.8F, 0.1F, 1.0F);
    public static final BoolValue heldValue = new BoolValue("Held", true);
    public static final BoolValue SPValue = new BoolValue("Progress", true);
    public static final BoolValue oldSPValue = new BoolValue("Progress1.8", true);
    public static final FloatValue swingSpeed = new FloatValue("swingSpeed", 1.0F, 0.1F, 5.0F);
    public static final ListValue Sword = new ListValue("Sword", new String[]{"Old", "1.7", "WindMill", "Push", "Smooth"}, "1.7");



}
