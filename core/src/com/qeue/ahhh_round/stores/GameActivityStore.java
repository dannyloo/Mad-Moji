package com.qeue.ahhh_round.stores;

import com.qeue.ahhh_round.AhhhRound;

import java.util.Date;

public class GameActivityStore {
    private static final String HIGH_SCORE_KEY = "HIGHSCORE";
    private static final String TOTAL_JUMPS_KEY = "TOTAL_JUMPS";
    private static final String TOTAL_PLAYS_KEY = "TOTAL_PLAYS";
    private static final String IS_MUTED_KEY = "IS_MUTED";
    private static final String HAS_PAID_TO_REMOVE_ADS_KEY = "HAS_PAID_TO_REMOVE_ADS";
    private static final String HAS_RATED_APP_KEY = "HAS_RATED_APP";
    private static final String HAS_UNSEEN_LOCKED_CHARACTER_SKINS_KEY = "HAS_UNSEEN_LOCKED_CHARACTER_SKINS";
    private static final String HAS_FAILED_GAMES_SIGN_IN_ONCE_KEY = "HAS_FAILED_GAMES_SIGN_IN_ONCE";
    private static final String DAYS_PLAYED_IN_A_ROW_KEY = "DAYS_PLAYED_IN_A_ROW";
    private static final String LAST_TIME_PLAYED_KEY = "LAST_TIME_PLAYED";

    private static boolean instanceExists;

    private Integer highScore;
    private Integer totalPlays;
    private Integer totalJumps;
    private Boolean isMuted;
    private Boolean hasPaidToRemoveAds;
    private Boolean hasRatedApp;
    private Boolean hasUnseenLockedCharacterSkins;
    private Boolean hasFailedGamesSignInOnce;
    private Integer daysPlayedInARow;
    private Date lastTimePlayed;

    public GameActivityStore() {
        if (instanceExists) {
            throw new RuntimeException("An instance of CharacterSkinStore already exists. You may only create one instance.");
        }
        instanceExists = true;
    }

    public int getHighScore() {
        if (highScore == null) {
            highScore = AhhhRound.getPrefs().getInteger(HIGH_SCORE_KEY);
        }
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
        AhhhRound.getPrefs().putInteger(HIGH_SCORE_KEY, highScore);
        AhhhRound.getPrefs().flush();
    }

    public int getTotalPlays() {
        if (totalPlays == null) {
            totalPlays = AhhhRound.getPrefs().getInteger(TOTAL_PLAYS_KEY);
        }
        return totalPlays;
    }

    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
        AhhhRound.getPrefs().putInteger(TOTAL_PLAYS_KEY, totalPlays);
        AhhhRound.getPrefs().flush();
    }

    public int getTotalJumps() {
        if (totalJumps == null) {
            totalJumps = AhhhRound.getPrefs().getInteger(TOTAL_JUMPS_KEY);
        }
        return totalJumps;
    }

    public void setTotalJumps(int totalJumps) {
        this.totalJumps = totalJumps;
        AhhhRound.getPrefs().putInteger(TOTAL_JUMPS_KEY, totalJumps);
        AhhhRound.getPrefs().flush();
    }

    public boolean isMuted() {
        if (isMuted == null) {
            isMuted = AhhhRound.getPrefs().getBoolean(IS_MUTED_KEY);
        }
        return isMuted;
    }

    public void setIsMuted(boolean isMuted) {
        this.isMuted = isMuted;
        AhhhRound.getPrefs().putBoolean(IS_MUTED_KEY, isMuted);
        AhhhRound.getPrefs().flush();
    }

    public boolean hasPaidToRemoveAds() {
        if (hasPaidToRemoveAds == null) {
            hasPaidToRemoveAds = AhhhRound.getPrefs().getBoolean(HAS_PAID_TO_REMOVE_ADS_KEY);
        }
        return hasPaidToRemoveAds;
    }

    public void setHasPaidToRemoveAds(boolean hasPaidToRemoveAds) {
        this.hasPaidToRemoveAds = hasPaidToRemoveAds;
        AhhhRound.getPrefs().putBoolean(HAS_PAID_TO_REMOVE_ADS_KEY, hasPaidToRemoveAds);
        AhhhRound.getPrefs().flush();
    }

    public boolean hasRatedApp() {
        if (hasRatedApp == null) {
            hasRatedApp = AhhhRound.getPrefs().getBoolean(HAS_RATED_APP_KEY);
        }
        return hasRatedApp;
    }

    public void setHasRatedApp(boolean hasRatedApp) {
        this.hasRatedApp = hasRatedApp;
        AhhhRound.getPrefs().putBoolean(HAS_RATED_APP_KEY, hasRatedApp);
        AhhhRound.getPrefs().flush();
    }

    public boolean hasUnseenLockedCharacterSkins() {
        if (hasUnseenLockedCharacterSkins == null) {
            hasUnseenLockedCharacterSkins = AhhhRound.getPrefs().getBoolean(HAS_UNSEEN_LOCKED_CHARACTER_SKINS_KEY);
        }
        return hasUnseenLockedCharacterSkins;
    }

    public void setHasUnseenLockedCharacterSkins(boolean hasUnseenLockedCharacterSkins) {
        this.hasUnseenLockedCharacterSkins = hasUnseenLockedCharacterSkins;
        AhhhRound.getPrefs().putBoolean(HAS_UNSEEN_LOCKED_CHARACTER_SKINS_KEY, hasUnseenLockedCharacterSkins);
        AhhhRound.getPrefs().flush();
    }

    public boolean hasFailedGamesSignInOnce() {
        if (hasFailedGamesSignInOnce == null) {
            hasFailedGamesSignInOnce = AhhhRound.getPrefs().getBoolean(HAS_FAILED_GAMES_SIGN_IN_ONCE_KEY);
        }
        return hasFailedGamesSignInOnce;
    }

    public void setHasFailedGamesSignInOnce(boolean hasFailedGamesSignInOnce) {
        this.hasFailedGamesSignInOnce = hasFailedGamesSignInOnce;
        AhhhRound.getPrefs().putBoolean(HAS_FAILED_GAMES_SIGN_IN_ONCE_KEY, hasFailedGamesSignInOnce);
        AhhhRound.getPrefs().flush();
    }

    public int getDaysPlayedInARow() {
        if (daysPlayedInARow == null) {
            daysPlayedInARow = AhhhRound.getPrefs().getInteger(DAYS_PLAYED_IN_A_ROW_KEY);
        }
        return daysPlayedInARow;
    }

    public void setDaysPlayedInARow(int daysPlayedInARow) {
        this.daysPlayedInARow = daysPlayedInARow;
        AhhhRound.getPrefs().putInteger(DAYS_PLAYED_IN_A_ROW_KEY, daysPlayedInARow);
        AhhhRound.getPrefs().flush();
    }

    public Date getLastTimePlayed() {
        if (lastTimePlayed == null) {
            lastTimePlayed = new Date(AhhhRound.getPrefs().getLong(LAST_TIME_PLAYED_KEY));
        }
        return lastTimePlayed;
    }

    public void setLastTimePlayed(Date lastTimePlayed) {
        this.lastTimePlayed = lastTimePlayed;
        AhhhRound.getPrefs().putLong(LAST_TIME_PLAYED_KEY, lastTimePlayed.getTime());
        AhhhRound.getPrefs().flush();
    }
}
