package com.gamestridestudios.ahhh_round;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.gamestridestudios.ahhh_round.events.EnableLeaderboardEvent;
import com.gamestridestudios.ahhh_round.events.RateAppEvent;
import com.gamestridestudios.ahhh_round.events.ShowInterstitialAdEvent;
import com.gamestridestudios.ahhh_round.events.ShowLeaderboardEvent;
import com.gamestridestudios.ahhh_round.events.ShowShareDialogEvent;
import com.gamestridestudios.ahhh_round.events.SuccessfullyShowedAdEvent;
import com.gamestridestudios.ahhh_round.events.UpdateGooglePlayGamesEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import io.fabric.sdk.android.Fabric;

public class AndroidLauncher extends AndroidApplication {
    private InterstitialAd interstitialAd;
    private Bus bus;
    private GameHelper gameHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());

        bus = new Bus(ThreadEnforcer.ANY);
        bus.register(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.numSamples = 2;
        initialize(new AhhhRound(bus), config);

        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.setup(new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
            }

            @Override
            public void onSignInSucceeded() {
                bus.post(new EnableLeaderboardEvent());
            }
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
	}

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHelper.onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        gameHelper.onActivityResult(request, response, data);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("B719ECEE7859FB315CFFFBAE97592ADE")
                    .build();
        interstitialAd.loadAd(adRequest);
    }

    @Subscribe
    public void showLeaderboard(ShowLeaderboardEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameHelper.isSignedIn()) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getResources().getString(R.string.leaderboard_id)), 0);
                }
            }
        });
    }

    @Subscribe
    public void showShareDialog(final ShowShareDialogEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I just hopped over " + event.score + (event.score == 1 ? " enemy" : " enemies") + " in Ahhh-round. Bet you can’t beat me! FIXME__SHARE_URL");
                startActivity(shareIntent);
            }
        });
    }

    @Subscribe
    public void rateApp(RateAppEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
    }

    @Subscribe
    public void updateGooglePlayGames(final UpdateGooglePlayGamesEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameHelper.isSignedIn()) {
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(), getResources().getString(R.string.leaderboard_id), event.score);
                    if (event.score >= 1) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), getResources().getString(R.string.achievement_1));
                    }
                    if (event.score >= 5) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), getResources().getString(R.string.achievement_5));
                    }
                    if (event.score >= 10) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), getResources().getString(R.string.achievement_10));
                    }
                    if (event.score >= 25) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), getResources().getString(R.string.achievement_25));
                    }
                    if (event.score >= 100) {
                        Games.Achievements.unlock(gameHelper.getApiClient(), getResources().getString(R.string.achievement_100));
                    }
                }
            }
        });
    }

    @Subscribe
    public void showInterstitialAd(ShowInterstitialAdEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                    bus.post(new SuccessfullyShowedAdEvent());
                }
            }
        });
    }
}
