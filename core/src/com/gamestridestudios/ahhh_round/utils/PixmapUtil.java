package com.gamestridestudios.ahhh_round.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class PixmapUtil {
    public static Pixmap getPixmapRoundedRectangle(double width, double height, double radius) {
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillRectangle(0, (int) radius, pixmap.getWidth(), pixmap.getHeight() - 2 * (int) radius);
        pixmap.fillRectangle((int) radius, 0, pixmap.getWidth() - 2 * (int) radius, pixmap.getHeight());
        pixmap.fillCircle((int) radius, (int) radius, (int) radius);
        pixmap.fillCircle((int) radius, pixmap.getHeight() - (int) radius, (int) radius);
        pixmap.fillCircle(pixmap.getWidth() - (int) radius, (int) radius, (int) radius);
        pixmap.fillCircle(pixmap.getWidth() - (int) radius, pixmap.getHeight() - (int) radius, (int) radius);
        return pixmap;
    }
}
