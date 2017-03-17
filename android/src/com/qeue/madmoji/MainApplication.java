package com.qeue.madmoji;

import android.app.Application;

import com.qeue.madmoji.stores.CharacterSkinStore;
import com.qeue.madmoji.stores.GameActivityStore;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * This class is the application accessible from within Activities.
 * It holds singletons that store game data/settings.
 */
public class MainApplication extends Application {
    private GameActivityStore gameActivityStore;
    private CharacterSkinStore characterSkinStore;
    private Bus bus;

    @Override
    public String getPackageName() {
        return super.getPackageName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gameActivityStore = new GameActivityStore();
        characterSkinStore = new CharacterSkinStore(gameActivityStore);
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public GameActivityStore getGameActivityStore() {
        return gameActivityStore;
    }

    public CharacterSkinStore getCharacterSkinStore() {
        return characterSkinStore;
    }

    public Bus getBus() {
        return bus;
    }
}
