package com.qeue.ahhh_round.utils;

public class AssetSizeUtil {
    private static Double width;
    private static Double height;
    private static Double heightIndifferentConstant;
    private static Double heightSensitiveConstant;

    /**
     * This must be called before any other method to initialize the utility.
     */
    public static void setup(double width, double height) {
        AssetSizeUtil.width = width;
        AssetSizeUtil.height = height;
    }

    /**
     * Used mainly for in-game objects like the player, center circle, and enemies.
     * This doesn't take into consideration the height of the screen, so it shouldn't be used for
     * sizing buttons or text.
     */
    public static double getHeightIndifferentConstant() {
        if (heightIndifferentConstant == null) {
            if (width != null) {
                heightIndifferentConstant = width / 25.227;
            } else {
                throw new RuntimeException("You must setup the AssetSizeUtil before using it.");
            }
        }
        return heightIndifferentConstant;
    }

    /**
     * Use this for all buttons and text shown on the game screen.
     */
    public static float inGameFontSize(double size) {
        return (float) (size * getHeightSensitiveConstant() / 17.0);
    }

    /**
     * Use this for all buttons and text shown on other Activities.
     */
    public static float outOfGameFontSize(double size) {
        double constant = Math.min(getHeightIndifferentConstant(), 12);
        return (float) (size * constant * 1.5 / 17.0);
    }

    /**
     * Used internally to generate the font size for in-game buttons and text.
     */
    private static double getHeightSensitiveConstant() {
        if (heightSensitiveConstant == null) {
            if (width != null && height != null) {
                heightSensitiveConstant = width / 11.773;
                if (height / width < 1.775) {
                    heightSensitiveConstant = height / 23.548;
                }
                heightSensitiveConstant = Math.min(heightSensitiveConstant, 80);
            } else {
                throw new RuntimeException("You must setup the AssetSizeUtil before using it.");
            }
        }
        return heightSensitiveConstant;
    }

}
