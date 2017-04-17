package com.qeue.madmoji.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.qeue.madmoji.AhhhRound;
import com.qeue.madmoji.MainApplication;
import com.qeue.madmoji.activities.helpers.AdHelper;
import com.qeue.madmoji.activities.helpers.GooglePlayGamesHelper;
import com.qeue.madmoji.activities.helpers.RemoveAdsHandler;
import com.qeue.madmoji.activities.helpers.ShareHelper;
import com.qeue.madmoji.components.BannerAdInterface;
import com.qeue.madmoji.events.RateAppEvent;
import com.qeue.madmoji.events.ShowSkinsActivityEvent;
import com.qeue.madmoji.events.ShowStatsActivityEvent;
import com.qeue.madmoji.events.ShowLeaderboardEvent;
import com.qeue.madmoji.stores.GameActivityStore;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import io.fabric.sdk.android.Fabric;

public class GameActivity extends AndroidApplication implements BannerAdInterface{
    private Bus bus;
    private RemoveAdsHandler removeAdsHandler;
    private AdHelper adHelper;
    private GooglePlayGamesHelper googlePlayGamesHelper;
    private GameActivityStore gameActivityStore;
    private com.qeue.madmoji.stores.CharacterSkinStore characterSkinStore;
    private ShareHelper shareHelper;
    //This is the key for out ad that we are using (we only have one right now so we only need one key here
    // This is bottle flips key, change it later
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-2406548650566064/2083961232"; //ca-app-pub-1620907453021344/7731284513 this is bf2k16's
    //The banner that will be getting displayed
    AdView bannerAd;

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
        initialize(new AhhhRound(bus, gameActivityStore, characterSkinStore, this), config);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        removeAdsHandler = new RemoveAdsHandler(this, bus, gameActivityStore);
        adHelper = new AdHelper(this, bus);
        googlePlayGamesHelper = new GooglePlayGamesHelper(this, bus, gameActivityStore, characterSkinStore);
        shareHelper = new ShareHelper(this, bus);

        setupAds();
        config = new AndroidApplicationConfiguration();
        View gameView = initializeForView(new AhhhRound(bus, gameActivityStore, characterSkinStore, this), config);
        //Setting up the layouts for the ad and the game
        RelativeLayout mLayout = new RelativeLayout(this);
        mLayout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mLayout.addView(bannerAd, params);

        setContentView(mLayout);
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
    public void showStatsActivity(final ShowLeaderboardEvent event) {
//        Intent statsActivityIntent = new Intent(this, StatsActivity.class);
//        startActivity(statsActivityIntent);
        googlePlayGamesHelper.attemptToShowLeaderboard();
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

    @Override
    public void showBannerAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerAd.setVisibility(View.VISIBLE);
                AdRequest.Builder builder = new AdRequest.Builder();
                AdRequest ad = builder.build();
                bannerAd.loadAd(ad);
            }
        });
    }

    @Override
    public float getAdHeight() {
        return AdSize.SMART_BANNER.getHeight();
    }

    @Override
    public void hideBannerAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bannerAd.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (ni != null && ni.isConnected());
    }

    @Override
    public boolean isDataConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (ni != null && ni.isConnected());
    }

    //This method simply initialized (sets up) the banner ad declared above
    public void setupAds(){
        bannerAd = new AdView(this);
        bannerAd.setVisibility(View.INVISIBLE);
        bannerAd.setBackgroundColor(0xff000000); // black
        bannerAd.setAdUnitId(BANNER_AD_UNIT_ID);
        bannerAd.setAdSize(AdSize.SMART_BANNER);

    }
}
