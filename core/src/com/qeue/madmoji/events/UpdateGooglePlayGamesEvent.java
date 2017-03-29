package com.qeue.madmoji.events;

public class UpdateGooglePlayGamesEvent {
    public final int score;
    public final int totalJumps;
    public final int totalPlays;
    public final int daysPlayedInARow;

    public UpdateGooglePlayGamesEvent(int score, int totalJumps, int totalPlays, int daysPlayedInARow) {
        this.score = score;
        this.totalJumps = totalJumps;
        this.totalPlays = totalPlays;
        this.daysPlayedInARow = daysPlayedInARow;
    }
}
