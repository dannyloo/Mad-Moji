package com.gamestridestudios.ahhh_round.activities.helpers;

import android.app.Activity;

import com.gamestridestudios.ahhh_round.R;
import com.gamestridestudios.ahhh_round.events.ShowAdEvent;
import com.gamestridestudios.ahhh_round.events.SuccessfullyShowedAdEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * This class allows for easy integration of AdMob interstitial ads.
 */
public class AdHelper {
    private Activity activity;
    private Bus bus;
    private InterstitialAd ad;

    public AdHelper(Activity activity, Bus bus) {
        this.activity = activity;
        this.bus = bus;

        bus.register(this);

        ad = new InterstitialAd(activity);
        ad.setAdUnitId(activity.getResources().getString(R.string.interstitial_ad_unit_id));
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewAd();
            }
        });

        requestNewAd();
    }

    @Subscribe
    public void showAd(ShowAdEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ad.isLoaded()) {
                    ad.show();
                    bus.post(new SuccessfullyShowedAdEvent());
                }
            }
        });
    }

    private void requestNewAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("B719ECEE7859FB315CFFFBAE97592ADE")
                .build();
        ad.loadAd(adRequest);
    }
}
