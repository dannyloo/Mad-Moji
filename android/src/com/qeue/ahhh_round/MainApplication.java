package com.qeue.ahhh_round;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * This class is the application accessible from within Activities.
 * It holds singletons that store game data/settings.
 */
public class MainApplication extends Application {
    private com.qeue.ahhh_round.stores.GameActivityStore gameActivityStore;
    private com.qeue.ahhh_round.stores.CharacterSkinStore characterSkinStore;
    private Bus bus;

    @Override
    public String getPackageName() {
        return super.getPackageName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gameActivityStore = new com.qeue.ahhh_round.stores.GameActivityStore();
        characterSkinStore = new com.qeue.ahhh_round.stores.CharacterSkinStore(gameActivityStore);
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public com.qeue.ahhh_round.stores.GameActivityStore getGameActivityStore() {
        return gameActivityStore;
    }

    public com.qeue.ahhh_round.stores.CharacterSkinStore getCharacterSkinStore() {
        return characterSkinStore;
    }

    public Bus getBus() {
        return bus;
    }
}
