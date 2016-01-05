package com.gamestridestudios.ahhh_round.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class RoundButton extends Group {
    private float radius;
    private Image buttonCircle;
    private Image buttonIcon;
    private InputListener clickListener;

    public RoundButton(float radius, Texture icon, float iconSize, float rightOffset, Color buttonColor, Color iconColor) {
        this.radius = radius;
        setBounds(0, 0, (float) Math.ceil(radius * 2), (float) Math.ceil(radius * 2));

        Pixmap buttonPixmap = new Pixmap((int) Math.ceil(radius * 2) + 1, (int) Math.ceil(radius * 2) + 1, Pixmap.Format.RGBA8888);
        buttonPixmap.setColor(Color.WHITE);
        buttonPixmap.fillCircle((int) radius + 1, (int) radius + 1, (int) radius);
        Texture buttonTexture = new Texture(buttonPixmap);
        buttonTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonCircle = new Image(buttonTexture);
        buttonCircle.setColor(buttonColor);
        buttonCircle.setPosition(-1, -1);

        buttonIcon = new Image(icon);
        buttonIcon.setBounds(radius - iconSize / 2 + rightOffset, radius - iconSize / 2, iconSize, iconSize);

        addActor(buttonCircle);
        addActor(buttonIcon);
        buttonIcon.setZIndex(buttonCircle.getZIndex() + 1);
        buttonIcon.setColor(iconColor);
    }

    public void setIconSizeAndRightOffset(float iconSize, float rightOffset) {
        buttonIcon.setBounds(radius - iconSize / 2 + rightOffset, radius - iconSize / 2, iconSize, iconSize);
    }

    public void setIconColor(Color color) {
        buttonIcon.setColor(color);
    }

    public void setIcon(Texture icon) {
        buttonIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(icon)));
    }

    public void setPositionCenter(float x, float y) {
        setPosition(x - radius, y - radius);
    }

    public void setClickListener(Runnable action) {
        if (clickListener != null) {
            removeListener(clickListener);
        }
        clickListener = new ButtonClickListener(action);
        addListener(clickListener);
    }

    private class ButtonClickListener extends InputListener {
        private Runnable action;

        public ButtonClickListener(Runnable action) {
            this.action = action;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (Math.sqrt(Math.pow(radius - x, 2) + Math.pow(radius - y, 2)) <= radius) {
                action.run();
            }
            return super.touchDown(event, x, y, pointer, button);
        }
    }
}
