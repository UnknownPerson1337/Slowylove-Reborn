/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package Linkow.utils;

import net.ccbluex.liquidbounce.utils.misc.RandomUtils;

public final class TimeUtils {

    private int tick;

    public void update() {
        tick++;
    }

    private long currentMS = System.currentTimeMillis();

    public boolean hasElapsed(long milliseconds) {
        return elapsed() > milliseconds;
    }

    public long elapsed() {
        return System.currentTimeMillis() - currentMS;
    }

    public void reset() {
        currentMS = System.currentTimeMillis();
    }

    public static long randomDelay(final int minDelay, final int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public static long randomClickDelay(final int minCPS, final int maxCPS) {
        return (long) ((Math.random() * (1000 / minCPS - 1000 / maxCPS + 1)) + 1000 / maxCPS);
    }
}