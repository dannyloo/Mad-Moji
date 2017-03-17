package com.qeue.madmoji.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class CenteredLabel extends Label implements AhhhroundGameElement {
    private double screenHeight;
    private double fontSize;
    private double centerAroundX;
    private double centerAroundY;
    private GlyphLayout layout;

    public CenteredLabel(Font font, Color color, double parentHeight, double centerAroundX, double centerAroundY) {
        this("", font, color, parentHeight, centerAroundX, centerAroundY);
    }

    public CenteredLabel(CharSequence text, Font font, Color color, double parentHeight, double centerAroundX, double centerAroundY) {
        super(text, new LabelStyle(font.bitmapFont, color));
        this.screenHeight = parentHeight;
        this.fontSize = font.size;
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
        double verticalOffset = -fontSize / 4.6875f;
        if (centerAroundY > screenHeight / 2) {
            verticalOffset *= -1;
        }
        setPosition((float) (centerAroundX - (layout.width / 2) + fontSize / 22.5), (float) (centerAroundY - (layout.height / 2) + verticalOffset));
    }

    @Override
    public void setVisibility(boolean visible) {
        getColor().a = visible ? 1 : 0;
    }

    @Override
    public void fadeIn(double time) {
        addAction(Actions.fadeIn((float) time));
    }

    @Override
    public void fadeOut(double time) {
        addAction(Actions.fadeOut((float) time));
    }

    @Override
    public void setPosition(double x, double y) {
        setPosition((float) x, (float) y);
    }

    @Override
    public void setBounds(double x, double y, double w, double h) {
        setBounds((float) x, (float) y, (float) w, (float) h);
    }
}
