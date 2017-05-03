package com.qeue.madmoji.stores;

import com.qeue.madmoji.AhhhRound;
import com.qeue.madmoji.components.CharacterSkin;
import com.qeue.madmoji.utils.ThousandsFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterSkinStore {
    private static final String SELECTED_CHARACTER_SKIN_KEY = "SELECTED_CHARACTER_SKIN";

    private static boolean instanceExists;

    private GameActivityStore gameActivityStore;
    private List<CharacterSkin> _allSkins;
    private Map<String, Runnable> selectedSkinChangeListeners = new HashMap<String, Runnable>();
    private CharacterSkin selectedCharacterSkin;

    public CharacterSkinStore(GameActivityStore gameActivityStore) {
        if (instanceExists) {
            throw new RuntimeException("An instance of CharacterSkinStore already exists. You may only create one instance.");
        }
        this.gameActivityStore = gameActivityStore;
        instanceExists = true;
    }

    public List<CharacterSkin> getAllSkins() {
        if (_allSkins == null) {
            _allSkins = Collections.unmodifiableList(populateCharacterSkins());
        }
        return _allSkins;
    }

    public void checkForAnyNewUnlockedSkins(boolean hasAlreadySeenAllUnlockedSkins) {
        for (CharacterSkin skin : getAllSkins()) {
            if (skin.unlockIfAble() && !hasAlreadySeenAllUnlockedSkins) {
                gameActivityStore.setHasUnseenLockedCharacterSkins(true);
            }
        }
    }

    public void addSelectedSkinChangeListener(String name, Runnable callback) {
        selectedSkinChangeListeners.put(name, callback);
    }

    public void removeSelectedSkinChangeListener(String name) {
        selectedSkinChangeListeners.remove(name);
    }

    public CharacterSkin getSelectedCharacterSkin() {
        if (selectedCharacterSkin == null) {
            selectedCharacterSkin = getCharacterSkin(AhhhRound.getPrefs().getString(SELECTED_CHARACTER_SKIN_KEY));
        }
        return selectedCharacterSkin;
    }

    public void setSelectedCharacterSkin(CharacterSkin characterSkin) {
        this.selectedCharacterSkin = characterSkin;
        AhhhRound.getPrefs().putString(SELECTED_CHARACTER_SKIN_KEY, characterSkin.imageName);
        AhhhRound.getPrefs().flush();
        alertListenersOfSelectedSkinChange();
    }

    private List<CharacterSkin> populateCharacterSkins() {
        List<CharacterSkin> skinList = new ArrayList<CharacterSkin>();




        CharacterSkin firstSkin = new CharacterSkin("player1.png", new String[]{"Open the app"}, new CharacterSkin.UnlockPredicate() {
            @Override
            public boolean run() {
                return true;
            }
        });
        firstSkin.setUnlocked(true);
        skinList.add(firstSkin);
        skinList.add(new CharacterSkin("player5.png",
               new String[]{textPredicate(PredicateType.SCORE, 10)},
               new CharacterSkin.UnlockPredicate() {
                   @Override
                   public boolean run() {
                       return gameActivityStore.getHighScore() >= 10;
                   }
               }));
        skinList.add(new CharacterSkin("player4.png",
                new String[]{textPredicate(PredicateType.SCORE, 25)},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getHighScore() >= 25;
                    }
                }));
        skinList.add(new CharacterSkin("player7.png",
                new String[]{textPredicate(PredicateType.SCORE, 50)},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getHighScore() >= 50;
                    }
                }));
        skinList.add(new CharacterSkin("player10.png",
                new String[]{textPredicate(PredicateType.JUMPS, 1000)},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getTotalJumps() >= 1000;
                    }
                }));
        skinList.add(new CharacterSkin("player6.png",
                new String[]{textPredicate(PredicateType.JUMPS, 2500)},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getTotalJumps() >= 2500;
                    }
                }));
        skinList.add(new CharacterSkin("player8.png",
                new String[]{textPredicate(PredicateType.JUMPS, 5000)},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getTotalJumps() >= 5000;
                    }
                }));
        skinList.add(new CharacterSkin("player9.png",
                new String[]{textPredicate(PredicateType.PLAYS, 1000)},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getTotalPlays() >= 1000;
                    }
                }));
        skinList.add(new CharacterSkin("player2.png",
                new String[]{"Play 2 days in a row"},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getDaysPlayedInARow() >= 2;
                    }
                }));
        skinList.add(new CharacterSkin("player3.png",
                new String[]{"Play 7 days in a row"},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getDaysPlayedInARow() >= 7;
                    }
                }));
        skinList.add(new CharacterSkin("blowKiss.png",
                new String[]{"Share with a friend"},
                new CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.hasSharedApp() == true;
                    }
                }));
        return skinList;
    }

    private void alertListenersOfSelectedSkinChange() {
        for (Runnable callback : selectedSkinChangeListeners.values()) {
            callback.run();
        }
    }

    private CharacterSkin getCharacterSkin(String imageName) {
        for (CharacterSkin characterSkin : getAllSkins()) {
            if (characterSkin.imageName.equals(imageName)) {
                return characterSkin;
            }
        }
        return getAllSkins().get(0);
    }

    private String textPredicate(PredicateType type, int value) {
        String firstWord = null;
        switch (type) {
            case SCORE:
                firstWord = "Score: ";
                break;
            case JUMPS:
                firstWord = "Jumps: ";
                break;
            case PLAYS:
                firstWord = "Plays: ";
                break;
        }
        return firstWord + ThousandsFormatter.format(value);
    }

    private enum PredicateType {
        SCORE, JUMPS, PLAYS
    }
}
