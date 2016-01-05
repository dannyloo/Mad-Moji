package com.gamestridestudios.ahhh_round.events;

public class UpdateLeaderboardEvent {
    public final int score;

    public UpdateLeaderboardEvent(int score) {
        this.score = score;
    }
}
