package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;

@ModuleInfo(name = "ItemShow", category = ModuleCategory.RENDER, description = "ItemShow")
public class ItemShow extends Module {
    public static final ListValue transformFirstPersonRotate = new ListValue("RotateMode", new String[]{"Custom","Rotate1", "Rotate2","Rotata3", "None"}, "Custom");
    public static final FloatValue SpeedRotate = new FloatValue("Rotate-Speed", 1f, 0f, 10f);
    public static final FloatValue customRotate1 = new FloatValue("CustomRotate1", 0, -360, 360);
    public static final FloatValue customRotate2 = new FloatValue("CustomRotate2", 0, -360, 360);
    public static final FloatValue customRotate3 = new FloatValue("CustomRotate3", 0, -360, 360);
}
