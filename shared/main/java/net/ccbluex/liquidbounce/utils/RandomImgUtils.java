package net.ccbluex.liquidbounce.utils;

import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RandomImgUtils {
    private static long startTime = 0L;
    static Random random = new Random();
    static int count = random.nextInt(1);
    public static int count2 = random.nextInt(1);

    public static ResourceLocation getBackGround() {
        return new ResourceLocation("liquidbounce/bg.png");
    }
}
