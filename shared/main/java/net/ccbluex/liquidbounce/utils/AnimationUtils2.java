/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils;

public class AnimationUtils2 {

    /**
     * In-out-easing function
     * https://github.com/jesusgollonet/processing-penner-easing
     * @param t Current iteration
     * @param d Total iterations
     * @return Eased value
     */
    public static float easeOut(float t, float d) {
        return (t = t / d - 1) * t * t + 1;
    }
    public static double animate(double target, double current, double speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += factor;
            if (current >= target) current = target;
        } else if (target < current) {
            current -= factor;
            if (current <= target) current = target;
        }

        return current;
    }

    public static float animate(float target, float current, float speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0F) {
            speed = 0.0F;
        } else if (speed > 1.0F) {
            speed = 1.0F;
        }

        double dif = Math.max(target, (double)current) - Math.min(target, (double)current);
        double factor = dif * (double)speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += (float)factor;
            if (current >= target) current = target;
        } else if (target < current) {
            current -= (float)factor;
            if (current <= target) current = target;
        }

        return current;
    }
}
