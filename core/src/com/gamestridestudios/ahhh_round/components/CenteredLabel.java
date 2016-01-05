package com.gamestridestudios.ahhh_round.components;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class CenteredLabel extends Label {
    private float screenWidth;
    private float screenHeight;
    private float fontSize;
    private float centerAroundX;
    private float centerAroundY;
    private GlyphLayout layout;

    public CenteredLabel(CharSequence text, LabelStyle style, float fontSize, float screenWidth, float screenHeight, float centerAroundX, float centerAroundY) {
        super(text, style);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.fontSize = fontSize;
        this.centerAroundX = centerAroundX;
        this.centerAroundY = centerAroundY;
        setText(text);
    }

    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        if (layout == null) {
            layout = new GlyphLayout();
        }
        layout.setText(getStyle().font, newText);
        float verticalOffset = -fontSize * screenWidth / 3375;
        if (centerAroundY > screenHeight / 2) {
            verticalOffset *= -1;
        }
        setPosition(centerAroundX - (layout.width / 2) + fontSize * screenWidth / 16200, centerAroundY - (layout.height / 2) + verticalOffset);
    }
}
