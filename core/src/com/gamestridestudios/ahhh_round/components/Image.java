package com.gamestridestudios.ahhh_round.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Image extends com.badlogic.gdx.scenes.scene2d.ui.Image implements AhhhroundGameElement {
    public Image() {
        super();
    }

    public Image(Texture texture) {
        super(texture);
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
