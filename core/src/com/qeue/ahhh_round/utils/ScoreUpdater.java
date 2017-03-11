package com.qeue.ahhh_round.utils;

import com.squareup.otto.Bus;

import java.util.Calendar;
import java.util.Date;

public class ScoreUpdater {
    private Bus bus;
    private com.qeue.ahhh_round.stores.GameActivityStore gameActivityStore;
    private com.qeue.ahhh_round.stores.CharacterSkinStore characterSkinStore;

    public ScoreUpdater(Bus bus, com.qeue.ahhh_round.stores.GameActivityStore gameActivityStore, com.qeue.ahhh_round.stores.CharacterSkinStore characterSkinStore) {
        this.bus = bus;
        this.gameActivityStore = gameActivityStore;
        this.characterSkinStore = characterSkinStore;
    }

    public void updateWithScore(int score) {
        if (score > gameActivityStore.getHighScore()) {
            gameActivityStore.setHighScore(score);
        }
        gameActivityStore.setTotalJumps(gameActivityStore.getTotalJumps() + score);
        gameActivityStore.setTotalPlays(gameActivityStore.getTotalPlays() + 1);
        updateDaysPlayedInARow();
        characterSkinStore.checkForAnyNewUnlockedSkins(false);

        bus.post(new com.qeue.ahhh_round.events.UpdateGooglePlayGamesEvent(score, gameActivityStore.getTotalJumps(), gameActivityStore.getTotalPlays()));
    }

    private void updateDaysPlayedInARow() {
        Date dayAfterLastTimePlayed = new Date(gameActivityStore.getLastTimePlayed().getTime() + 60 * 60 * 24 * 1000);
        Date now = new Date();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(now);
        cal2.setTime(dayAfterLastTimePlayed);
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
            gameActivityStore.setDaysPlayedInARow(gameActivityStore.getDaysPlayedInARow() + 1);
            gameActivityStore.setLastTimePlayed(now);
        } else if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)
                || (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR))) {
            gameActivityStore.setDaysPlayedInARow(1);
            gameActivityStore.setLastTimePlayed(now);
        }
    }
}
