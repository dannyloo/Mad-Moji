package com.qeue.madmoji.components;

/**
 * Created by owner on 2017-03-16.
 * Controlling Banner Ads
 */

public interface BannerAdInterface {
    void showBannerAd();
    float getAdHeight();
    void hideBannerAd();
    boolean isWifiConnected();
    boolean isDataConnected();
}
