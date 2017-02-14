package com.gamestridestudios.ahhh_round.activities.helpers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.gamestridestudios.ahhh_round.R;
import com.gamestridestudios.ahhh_round.events.UpdateGooglePlayGamesEvent;
import com.gamestridestudios.ahhh_round.stores.CharacterSkinStore;
import com.gamestridestudios.ahhh_round.stores.GameActivityStore;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * This class allows easy integration with Google Play Games.
 * It takes care of connecting to Google Play Games and sending scores.
 * If the user has previously installed the app and integrated Google Play Games and they reinstall
 * the app, it will load their old scores.
 */
public class GooglePlayGamesHelper {
    private Activity activity;
    private Bus bus;
    private GameActivityStore gameActivityStore;
    private CharacterSkinStore characterSkinStore;
    private GameHelper gameHelper;

    public GooglePlayGamesHelper(final Activity activity, Bus bus, final GameActivityStore gameActivityStore, CharacterSkinStore characterSkinStore) {
        this.activity = activity;
        this.bus = bus;
        this.gameActivityStore = gameActivityStore;
        this.characterSkinStore = characterSkinStore;

        bus.register(this);

        gameHelper = new GameHelper(activity, GameHelper.CLIENT_GAMES);
        gameHelper.setConnectOnStart(!gameActivityStore.hasFailedGamesSignInOnce());
        gameHelper.setup(new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
                gameActivityStore.setHasFailedGamesSignInOnce(true);
            }

            @Override
            public void onSignInSucceeded() {
                gameActivityStore.setHasFailedGamesSignInOnce(false);
                updateHighscoreBasedOnLeaderboard();
                updateTotalJumpsBasedOnLeaderboard();
                updateTotalPlaysBasedOnLeaderboard();
            }
        });
    }

    private void updateHighscoreBasedOnLeaderboard() {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.highscore_leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                int score = (int) loadPlayerScoreResult.getScore().getRawScore();
                if (score > gameActivityStore.getHighScore()) {
                    gameActivityStore.setHighScore(score);
                    characterSkinStore.checkForAnyNewUnlockedSkins(true);
                }
            }
        });
    }

    private void updateTotalJumpsBasedOnLeaderboard() {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_jumps_leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                int totalJumps = (int) loadPlayerScoreResult.getScore().getRawScore();
                if (totalJumps > gameActivityStore.getTotalJumps()) {
                    gameActivityStore.setTotalJumps(totalJumps);
                    characterSkinStore.checkForAnyNewUnlockedSkins(true);
                }
            }
        });
    }

    private void updateTotalPlaysBasedOnLeaderboard() {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_plays_leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                int totalPlays = (int) loadPlayerScoreResult.getScore().getRawScore();
                if (totalPlays > gameActivityStore.getTotalPlays()) {
                    gameActivityStore.setTotalPlays(totalPlays);
                    characterSkinStore.checkForAnyNewUnlockedSkins(true);
                }
            }
        });
    }

    @Subscribe
    public void updateGooglePlayGames(final UpdateGooglePlayGamesEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameHelper.isSignedIn()) {
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.highscore_leaderboard_id), event.score);
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_jumps_leaderboard_id), event.totalJumps);
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), activity.getResources().getString(R.string.total_plays_leaderboard_id), event.totalPlays);
                    if (event.score >= 1) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_1_id));
                    }
                    if (event.score >= 5) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_5_id));
                    }
                    if (event.score >= 10) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_10_id));
                    }
                    if (event.score >= 25) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_25_id));
                    }
                    if (event.score >= 100) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), activity.getResources().getString(R.string.achievement_100_id));
                    }
                }
            }
        });
    }

    public void onStart() {
        gameHelper.onStart(activity);
    }

    public void onStop() {
        gameHelper.onStop();
    }

    public void onActivityResult(int request, int response, Intent data) {
        gameHelper.onActivityResult(request, response, data);
    }
}
