package com.gamestridestudios.ahhh_round.components;

import com.badlogic.gdx.graphics.Texture;
import com.gamestridestudios.ahhh_round.utils.AngleUtil;

public class Character extends Image implements AhhhroundGameElement {
    protected double currentAngleFromCenter; //angle is in radians
    private double width;
    private double height;
    private double characterRadius;
    private double midpointPercent;

    public Character(double width, double characterRadius, double initialAngleFromCenter, double midpointPercent, Texture texture) {
        super(texture);
        this.width = width;
        this.height = width * texture.getHeight() / texture.getWidth();
        this.characterRadius = characterRadius;
        this.midpointPercent = midpointPercent;
        currentAngleFromCenter = initialAngleFromCenter;
        setBounds(0, 0, width, height);
        setOrigin((float) (width / 2), (float) (height * midpointPercent));
        setRotation((float) AngleUtil.radiansToDegrees(initialAngleFromCenter - Math.PI / 2) % 360);
    }

    public void setPositionAroundCircle(double angleFromCenter, double distanceFromCenter, Point center) {
        currentAngleFromCenter = angleFromCenter;
        setRotation((float) AngleUtil.radiansToDegrees(angleFromCenter - Math.PI / 2) % 360);

        Point position = getPosition(distanceFromCenter, center);
        setPosition(position.x, position.y);
    }

    public void setPositionAroundCircle(double distanceFromCenter, Point center) {
        setPositionAroundCircle(currentAngleFromCenter, distanceFromCenter, center);
    }

    public Point getPosition(double distanceFromCenter, Point center) {
        return new Point(Math.cos(currentAngleFromCenter) * (distanceFromCenter + characterRadius) + center.x - width / 2.0,
                         Math.sin(currentAngleFromCenter) * (distanceFromCenter + characterRadius) + center.y - height * midpointPercent);
    }

    public double getCurrentAngleFromCenter() {
        return currentAngleFromCenter;
    }
}
