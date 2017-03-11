package com.qeue.ahhh_round.stores;

import com.qeue.ahhh_round.AhhhRound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterSkinStore {
    private static final String SELECTED_CHARACTER_SKIN_KEY = "SELECTED_CHARACTER_SKIN";

    private static boolean instanceExists;

    private GameActivityStore gameActivityStore;
    private List<com.qeue.ahhh_round.components.CharacterSkin> _allSkins;
    private Map<String, Runnable> selectedSkinChangeListeners = new HashMap<String, Runnable>();
    private com.qeue.ahhh_round.components.CharacterSkin selectedCharacterSkin;

    public CharacterSkinStore(GameActivityStore gameActivityStore) {
        if (instanceExists) {
            throw new RuntimeException("An instance of CharacterSkinStore already exists. You may only create one instance.");
        }
        this.gameActivityStore = gameActivityStore;
        instanceExists = true;
    }

    public List<com.qeue.ahhh_round.components.CharacterSkin> getAllSkins() {
        if (_allSkins == null) {
            _allSkins = Collections.unmodifiableList(populateCharacterSkins());
        }
        return _allSkins;
    }

    public void checkForAnyNewUnlockedSkins(boolean hasAlreadySeenAllUnlockedSkins) {
        for (com.qeue.ahhh_round.components.CharacterSkin skin : getAllSkins()) {
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

    public com.qeue.ahhh_round.components.CharacterSkin getSelectedCharacterSkin() {
        if (selectedCharacterSkin == null) {
            selectedCharacterSkin = getCharacterSkin(AhhhRound.getPrefs().getString(SELECTED_CHARACTER_SKIN_KEY));
        }
        return selectedCharacterSkin;
    }

    public void setSelectedCharacterSkin(com.qeue.ahhh_round.components.CharacterSkin characterSkin) {
        this.selectedCharacterSkin = characterSkin;
        AhhhRound.getPrefs().putString(SELECTED_CHARACTER_SKIN_KEY, characterSkin.imageName);
        AhhhRound.getPrefs().flush();
        alertListenersOfSelectedSkinChange();
    }

    private List<com.qeue.ahhh_round.components.CharacterSkin> populateCharacterSkins() {
        List<com.qeue.ahhh_round.components.CharacterSkin> skinList = new ArrayList<com.qeue.ahhh_round.components.CharacterSkin>();
        com.qeue.ahhh_round.components.CharacterSkin firstSkin = new com.qeue.ahhh_round.components.CharacterSkin("player1.png", new String[]{"Open the app"}, new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
            @Override
            public boolean run() {
                return true;
            }
        });
        firstSkin.setUnlocked(true);
        skinList.add(firstSkin);
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player5.png",
               new String[]{textPredicate(PredicateType.SCORE, 10),
                            textPredicate(PredicateType.JUMPS, 500)},
               new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                   @Override
                   public boolean run() {
                       return gameActivityStore.getHighScore() >= 10 || gameActivityStore.getTotalJumps() >= 500;
                   }
               }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player4.png",
                new String[]{textPredicate(PredicateType.SCORE, 25),
                             textPredicate(PredicateType.JUMPS, 2500)},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getHighScore() >= 25 || gameActivityStore.getTotalJumps() >= 2500;
                    }
                }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player7.png",
                new String[]{textPredicate(PredicateType.SCORE, 50),
                             textPredicate(PredicateType.JUMPS, 5000)},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getHighScore() >= 50 || gameActivityStore.getTotalJumps() >= 5000;
                    }
                }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player10.png",
                new String[]{textPredicate(PredicateType.SCORE, 100),
                             textPredicate(PredicateType.JUMPS, 15000)},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getHighScore() >= 100 || gameActivityStore.getTotalJumps() >= 15000;
                    }
                }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player6.png",
                new String[]{textPredicate(PredicateType.SCORE, 200),
                             textPredicate(PredicateType.JUMPS, 25000)},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getHighScore() >= 200 || gameActivityStore.getTotalJumps() >= 25000;
                    }
                }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player8.png",
                new String[]{"Rate the app"},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.hasRatedApp();
                    }
                }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player9.png",
                new String[]{textPredicate(PredicateType.PLAYS, 1000)},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getTotalPlays() >= 1000;
                    }
                }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player2.png",
                new String[]{textPredicate(PredicateType.JUMPS, 31415)},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getTotalJumps() >= 31415;
                    }
                }));
        skinList.add(new com.qeue.ahhh_round.components.CharacterSkin("player3.png",
                new String[]{"Play 7 days in a row"},
                new com.qeue.ahhh_round.components.CharacterSkin.UnlockPredicate() {
                    @Override
                    public boolean run() {
                        return gameActivityStore.getDaysPlayedInARow() >= 7;
                    }
                }));
        return skinList;
    }

    private void alertListenersOfSelectedSkinChange() {
        for (Runnable callback : selectedSkinChangeListeners.values()) {
            callback.run();
        }
    }

    private com.qeue.ahhh_round.components.CharacterSkin getCharacterSkin(String imageName) {
        for (com.qeue.ahhh_round.components.CharacterSkin characterSkin : getAllSkins()) {
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
        return firstWord + com.qeue.ahhh_round.utils.ThousandsFormatter.format(value);
    }

    private enum PredicateType {
        SCORE, JUMPS, PLAYS
    }
}
