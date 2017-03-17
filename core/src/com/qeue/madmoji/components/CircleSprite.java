package com.qeue.madmoji.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CircleSprite extends Image {
    public CircleSprite(double radius, com.qeue.madmoji.components.Color color) {
        super();
        int intRadius = (int) Math.ceil(radius);
        int higherQualityScalingConstant = 4;
        Pixmap centerCirclePixmap = new Pixmap(intRadius * 2 * higherQualityScalingConstant, intRadius * 2 * higherQualityScalingConstant, Pixmap.Format.RGBA8888);
        centerCirclePixmap.setColor(Color.WHITE);
        centerCirclePixmap.fillCircle(intRadius * higherQualityScalingConstant, intRadius * higherQualityScalingConstant, intRadius * higherQualityScalingConstant);
        Texture centerCircleTexture = new Texture(centerCirclePixmap);
        centerCircleTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        setDrawable(new TextureRegionDrawable(new TextureRegion(centerCircleTexture)));
        setBounds(0, 0, radius * 2, radius * 2);
        setColor(Color.valueOf(color.hex));
    }
}
