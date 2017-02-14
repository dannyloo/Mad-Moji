package com.gamestridestudios.ahhh_round.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.gamestridestudios.ahhh_round.utils.PixmapUtil;

public class RectangleButton extends Group implements AhhhroundGameElement {
    private Image buttonBackground;
    private CenteredLabel buttonText;
    private InputListener clickListener;

    public RectangleButton(double width, double height, String text, final Style buttonStyle) {
        setBounds(0, 0, width, height);

        int higherQualityScalingConstant = 4;
        Pixmap buttonPixmap = PixmapUtil.getPixmapRoundedRectangle(width * higherQualityScalingConstant, height * higherQualityScalingConstant, buttonStyle.cornerRadius * 3);
        buttonPixmap.setColor(Color.WHITE);
        Texture buttonTexture = new Texture(buttonPixmap);
        buttonTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonBackground = new Image(buttonTexture);
        buttonBackground.setColor(buttonStyle.buttonColor);
        buttonBackground.setBounds(0, 0, width, height);
        buttonBackground.setPosition(0, 0);

        buttonText = new CenteredLabel(text, buttonStyle.font, buttonStyle.textColor, height, width / 2f, height / 2f);

        addActor(buttonBackground);
        addActor(buttonText);
        buttonText.setZIndex(buttonBackground.getZIndex() + 1);

        buttonStyle.styleUpdatedCallback = new Runnable() {
            @Override
            public void run() {
                buttonBackground.setColor(buttonStyle.buttonColor);
            }
        };
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

    public void setClickListener(Runnable action) {
        if (clickListener != null) {
            removeListener(clickListener);
        }
        clickListener = new ButtonClickListener(action);
        addListener(clickListener);
    }

    public static class Style {
        private Runnable styleUpdatedCallback;
        private Color buttonColor;
        private Color textColor;
        private Font font;
        private double cornerRadius;

        public Style(Color buttonColor, Color textColor, Font font) {
            this.buttonColor = buttonColor;
            this.font = font;
            this.textColor = textColor;
            this.cornerRadius = font.size * 0.375;
        }

        public void setButtonColor(Color color) {
            buttonColor = color;
            if (styleUpdatedCallback != null) {
                styleUpdatedCallback.run();
            }
        }
    }

    private class ButtonClickListener extends InputListener {
        private Runnable action;

        public ButtonClickListener(Runnable action) {
            this.action = action;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            action.run();
            return super.touchDown(event, x, y, pointer, button);
        }
    }
}
