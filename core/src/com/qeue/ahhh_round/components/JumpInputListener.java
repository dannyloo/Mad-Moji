package com.qeue.ahhh_round.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class JumpInputListener extends InputListener {
    private Runnable jumpRunnable;

    public JumpInputListener(Runnable jumpRunnable) {
        this.jumpRunnable = jumpRunnable;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (!event.isHandled()) {
            jumpRunnable.run();
        }
        return false;
    }
}
