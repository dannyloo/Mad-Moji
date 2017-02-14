package com.gamestridestudios.ahhh_round.events;

import com.badlogic.gdx.graphics.Pixmap;

public class ShowShareDialogEvent {
    public final int score;
    public final Pixmap screenshot;

    public ShowShareDialogEvent(int score, Pixmap screenshot) {
        this.score = score;
        this.screenshot = screenshot;
    }
}
