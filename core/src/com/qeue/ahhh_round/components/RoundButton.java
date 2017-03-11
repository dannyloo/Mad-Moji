package com.qeue.ahhh_round.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class RoundButton extends Group implements AhhhroundGameElement {
    private double radius;
    private Image buttonCircle;
    private Image buttonIcon;
    private InputListener clickListener;

    public RoundButton(double radius, Texture icon, double iconSize, double rightOffset, Color buttonColor, Color iconColor) {
        this.radius = radius;
        setBounds(0, 0, Math.ceil(radius * 2), Math.ceil(radius * 2));

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

    public void setIconSizeAndRightOffset(double iconSize, double rightOffset) {
        buttonIcon.setBounds(radius - iconSize / 2 + rightOffset, radius - iconSize / 2, iconSize, iconSize);
    }

    public void setIconColor(Color color) {
        buttonIcon.setColor(color);
    }

    public void setIcon(Texture icon) {
        buttonIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(icon)));
    }

    public void setPositionCenter(double x, double y) {
        setPosition(x - radius, y - radius);
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
            event.handle();
            return super.touchDown(event, x, y, pointer, button);
        }
    }
}
