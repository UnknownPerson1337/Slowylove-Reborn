
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue

import net.minecraft.util.ResourceLocation

@ModuleInfo(name = "Cape", description = "LiquidBounce+ capes.", category = ModuleCategory.RENDER)
class Cape : Module() {

    val styleValue = ListValue("Style", arrayOf("Dark", "Astolfo", "Sunny", "Target", "Wyy", "PowerX", "Azrael", "Flux", "LiquidBounce", "Light", "Novoline", "Special1", "Special2"), "Dark")

    fun getCapeLocation(value: String): ResourceLocation {
        return try {
            CapeStyle.valueOf(value.toUpperCase()).location
        } catch (e: IllegalArgumentException) {
            CapeStyle.DARK.location
        }
    }

    enum class CapeStyle(val location: ResourceLocation) {
        DARK(ResourceLocation("liquidbounce/capes/dark.png")),
        ASTOLFO(ResourceLocation("liquidbounce/capes/astolfo.png")),
        LIGHT(ResourceLocation("liquidbounce/capes/light.png")),
        SUNNY(ResourceLocation("liquidbounce/capes/Sunny.png")),
        WYY(ResourceLocation("liquidbounce/capes/Wyy.png")),
        POWERX(ResourceLocation("liquidbounce/capes/PowerX.png")),
        AZRAEL(ResourceLocation("liquidbounce/capes/azrael.png")),
        TARGET(ResourceLocation("liquidbounce/capes/Target.png")),
        FLUX(ResourceLocation("liquidbounce/capes/Flux.png")),
        LIQUIDBOUNCE(ResourceLocation("liquidbounce/capes/LiquidBounce.png")),
        NOVOLINE(ResourceLocation("liquidbounce/capes/Novoline.png")),
        SPECIAL1(ResourceLocation("liquidbounce/capes/special1.png")),
        SPECIAL2(ResourceLocation("liquidbounce/capes/special2.png"))
    }

    override val tag: String
        get() = styleValue.get()

}