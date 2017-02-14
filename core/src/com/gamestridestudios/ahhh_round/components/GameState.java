package com.gamestridestudios.ahhh_round.components;

public enum GameState {
    GAME_OVER,
    SLEEPING,
    PLAYING;

    public boolean isGameOver() {
        return this == GAME_OVER;
    }

    public boolean isSleeping() {
        return this == SLEEPING;
    }

    public boolean isPlaying() {
        return this == PLAYING;
    }
}
