package com.qeue.madmoji.utils;

public class AngleUtil {
    public static boolean isAngleBetween(double target, double angle1, double angle2) {
        target = radiansToDegrees(target);
        angle1 = radiansToDegrees(angle1);
        angle2 = radiansToDegrees(angle2);

        target = (360 + (target % 360)) % 360;
        angle1 = (3600000 + angle1) % 360;
        angle2 = (3600000 + angle2) % 360;

        if (angle1 < angle2) {
            return angle1 <= target && target <= angle2;
        }
        return angle1 <= target || target <= angle2;
    }

    public static double radiansToDegrees(double radians) {
        return radians * (180.0 / Math.PI);
    }
}
