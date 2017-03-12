package com.qeue.ahhh_round.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.qeue.ahhh_round.AhhhRound;
import com.qeue.ahhh_round.MainApplication;
import com.qeue.ahhh_round.activities.helpers.AdHelper;
import com.qeue.ahhh_round.activities.helpers.GooglePlayGamesHelper;
import com.qeue.ahhh_round.activities.helpers.RemoveAdsHandler;
import com.qeue.ahhh_round.activities.helpers.ShareHelper;
import com.qeue.ahhh_round.events.RateAppEvent;
import com.qeue.ahhh_round.events.ShowSkinsActivityEvent;
import com.qeue.ahhh_round.events.ShowStatsActivityEvent;
import com.qeue.ahhh_round.stores.CharacterSkinStore;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.fabric.sdk.android.Fabric;

public class GameActivity extends AndroidApplication {
    private Bus bus;
    private RemoveAdsHandler removeAdsHandler;
    private AdHelper adHelper;
    private GooglePlayGamesHelper googlePlayGamesHelper;
    private com.qeue.ahhh_round.stores.GameActivityStore gameActivityStore;
    private CharacterSkinStore characterSkinStore;
    private ShareHelper shareHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());

        bus = ((MainApplication) getApplication()).getBus();
        bus.register(this);
        gameActivityStore = ((MainApplication) getApplication()).getGameActivityStore();
        characterSkinStore = ((MainApplication) getApplication()).getCharacterSkinStore();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.numSamples = 2;
        initialize(new AhhhRound(bus, gameActivityStore, characterSkinStore), config);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        removeAdsHandler = new RemoveAdsHandler(this, bus, gameActivityStore);
        adHelper = new AdHelper(this, bus);
        googlePlayGamesHelper = new GooglePlayGamesHelper(this, bus, gameActivityStore, characterSkinStore);
        shareHelper = new ShareHelper(this, bus);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAdsHandler.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googlePlayGamesHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googlePlayGamesHelper.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shareHelper.onResume();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        if (!removeAdsHandler.handleActivityResult(request, response, data)) {
            super.onActivityResult(request, response, data);
            googlePlayGamesHelper.onActivityResult(request, response, data);
        }
    }

    @Subscribe
    public void showSkinActivity(final ShowSkinsActivityEvent event) {
        Intent skinActivityIntent = new Intent(this, SelectSkinActivity.class);
        startActivity(skinActivityIntent);
    }

    @Subscribe
    public void showStatsActivity(final ShowStatsActivityEvent event) {
        Intent statsActivityIntent = new Intent(this, StatsActivity.class);
        startActivity(statsActivityIntent);
    }

    @Subscribe
    public void rateApp(RateAppEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                System.out.println("Package name is "+getPackageName());
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
}
