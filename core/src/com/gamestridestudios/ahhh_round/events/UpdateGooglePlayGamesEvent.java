package com.gamestridestudios.ahhh_round.events;

public class UpdateGooglePlayGamesEvent {
    public final int score;
    public final int totalJumps;
    public final int totalPlays;

    public UpdateGooglePlayGamesEvent(int score, int totalJumps, int totalPlays) {
        this.score = score;
        this.totalJumps = totalJumps;
        this.totalPlays = totalPlays;
    }
}
