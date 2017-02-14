package com.gamestridestudios.ahhh_round.components;

import com.gamestridestudios.ahhh_round.AhhhRound;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CharacterSkin {
    public final String imageName;
    public final List<String> unlockPredicateTextForm;
    private Boolean unlocked;
    private UnlockPredicate unlockPredicate;

    public CharacterSkin(String imageName, String[] unlockPredicateTextForm, UnlockPredicate unlockPredicate) {
        this.imageName = imageName;
        this.unlockPredicate = unlockPredicate;
        this.unlockPredicateTextForm = Collections.unmodifiableList(Arrays.asList(unlockPredicateTextForm));
    }

    public boolean isUnlocked() {
        if (unlocked == null) {
            unlocked = AhhhRound.getPrefs().getBoolean("SKIN_UNLOCKED_" + imageName);
        }
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
        AhhhRound.getPrefs().putBoolean("SKIN_UNLOCKED_" + imageName, unlocked);
        AhhhRound.getPrefs().flush();
    }

    public boolean unlockIfAble() {
        if (!isUnlocked() && unlockPredicate.run()) {
            setUnlocked(true);
            return true;
        }
        return false;
    }

    public interface UnlockPredicate {
        boolean run();
    }
}
