package com.gamestridestudios.ahhh_round;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gamestridestudios.ahhh_round.components.AhhhroundGameElement;
import com.gamestridestudios.ahhh_round.components.CenteredLabel;
import com.gamestridestudios.ahhh_round.components.Character;
import com.gamestridestudios.ahhh_round.components.CharacterSkin;
import com.gamestridestudios.ahhh_round.components.CircleSprite;
import com.gamestridestudios.ahhh_round.components.Color;
import com.gamestridestudios.ahhh_round.components.GameState;
import com.gamestridestudios.ahhh_round.components.Image;
import com.gamestridestudios.ahhh_round.components.JumpInputListener;
import com.gamestridestudios.ahhh_round.components.Player;
import com.gamestridestudios.ahhh_round.components.Point;
import com.gamestridestudios.ahhh_round.components.RectangleButton;
import com.gamestridestudios.ahhh_round.components.RoundButton;
import com.gamestridestudios.ahhh_round.events.PurchaseAdRemovalEvent;
import com.gamestridestudios.ahhh_round.events.RateAppEvent;
import com.gamestridestudios.ahhh_round.events.ShowAdEvent;
import com.gamestridestudios.ahhh_round.events.ShowShareDialogEvent;
import com.gamestridestudios.ahhh_round.events.ShowSkinsActivityEvent;
import com.gamestridestudios.ahhh_round.events.ShowStatsActivityEvent;
import com.gamestridestudios.ahhh_round.events.SuccessfullyShowedAdEvent;
import com.gamestridestudios.ahhh_round.events.UpdateRemoveAdsButtonEvent;
import com.gamestridestudios.ahhh_round.stores.CharacterSkinStore;
import com.gamestridestudios.ahhh_round.stores.GameActivityStore;
import com.gamestridestudios.ahhh_round.utils.AngleUtil;
import com.gamestridestudios.ahhh_round.utils.AssetSizeUtil;
import com.gamestridestudios.ahhh_round.utils.FontFactory;
import com.gamestridestudios.ahhh_round.utils.ScoreUpdater;
import com.gamestridestudios.ahhh_round.utils.ScreenshotUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AhhhRound extends ApplicationAdapter {
    public static final int TAKING_SCREENSHOT_NUMBER = -1;
    public static final String DEFAULT_FONT_NAME = "Quicksand-Bold.otf";
    private static Preferences preferences;

    private final String[] CENTER_CIRCLE_COLORS = new String[]{"2E4052","669BBC","0F7173","3C6997","789ABB","419D78","618985","2274A5","555B6E","437C90"};
    private final String[] GAME_OVER_PHRASES = new String[]{"Oops!", "Ouch!", "Ahhh!", "Yikes!", "Oh no!", "Uh oh!"};
    private final String REGULAR_FONT = "REGULAR_FONT";
    private final String TAP_TO_JUMP_FONT = "TAP_TO_JUMP_FONT";
    private final String RECTANGLE_BUTTON_FONT = "RECTANGLE_BUTTON_FONT";
    private final String SCORE_FONT = "SCORE_FONT";
    private final int PLAY_COUNT_BEFORE_RATE_PULSE = 10;
    private final int MIN_GAME_COUNT_BEFORE_AD_IS_DISPLAYED = 6;
    private final int MIN_TIME_BEFORE_AD_IS_DISPLAYED = 120;
    private final int JUMP_COUNT_BEFORE_CIRCLE_COLOR_CHANGE = 5;
    private final double TIME_DELTA_TO_DETECT_PREMATURE_JUMPS = 0.03;
    private final double ENEMY_SPAWN_TIME = 1.3;

    private Stage stage;
    private Bus bus;
    private AssetManager assetManager;
    private GameActivityStore gameActivityStore;
    private CharacterSkinStore characterSkinStore;
    private ScoreUpdater scoreUpdater;
    private FontFactory fontFactory;
    private List<Sound> jumpSounds = new ArrayList<Sound>();
    private Sound deathSound;

    private boolean readyToPlay;
    private boolean isTransitioningToSleeping;
    private boolean isTransitioningToMenu;
    private GameState gameState = GameState.SLEEPING;
    private double deltaTime = -1;
    private int score;
    private long timeLastAdDisplayed;
    private int gamesSinceLastAd;

    private boolean removeAdsButtonVisible;
    private boolean takingScreenshot;
    private double timeSinceLastEnemySpawn;
    private double timeSinceLastJumpAttempt;
    private double width;
    private double height;
    private double midX;
    private double midY;
    private double centerCircleRadius;
    private Point centerCircleCenter;
    private double roundButtonRadius;
    private double playerRadius;
    private double jumpHeight;
    private int currentCircleColorIndex;
    private List<Character> enemies = new ArrayList<Character>();
    private Character killer;
    private Player player;
    private List<AhhhroundGameElement> menuElements = new ArrayList<AhhhroundGameElement>();
    private List<AhhhroundGameElement> inGameElements = new ArrayList<AhhhroundGameElement>();
    private Image logo;
    private Image centerCircle;
    private Image splashScreenLogo;
    private CenteredLabel gameOverLabel;
    private CenteredLabel gameOverScoreLabel;
    private CenteredLabel gameOverHighScoreLabel;
    private CenteredLabel inGameScoreLabel;
    private CenteredLabel tapToJumpLabel;
    private RectangleButton.Style playAgainButtonStyle;
    private RectangleButton playAgainButton;
    private RectangleButton statsButton;
    private RectangleButton skinsButton;
    private RoundButton muteButton;
    private RoundButton removeAdsButton;
    private RectangleButton shareButton;
    private RectangleButton rateButton;
    private Action spinPlayerAction;
    private Action movePlayerToCenterAction;

    public AhhhRound(Bus bus, GameActivityStore gameActivityStore, CharacterSkinStore characterSkinStore) {
        this.bus = bus;
        bus.register(this);
        this.gameActivityStore = gameActivityStore;
        this.characterSkinStore = characterSkinStore;
        scoreUpdater = new ScoreUpdater(bus, gameActivityStore, characterSkinStore);
    }

    public static Preferences getPrefs() {
        if (preferences == null) {
            preferences = Gdx.app.getPreferences("Ahhh-round");
        }
        return preferences;
    }

    @Override
    public void create() {
        stage = new Stage();
        stage.addListener(new JumpInputListener(new Runnable() {
            @Override
            public void run() {
                jump();
            }
        }));
        setupConstants();
        loadSplashScreen();
        loadAssets();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(Color.OFF_WHITE.r, Color.OFF_WHITE.g, Color.OFF_WHITE.b, Color.OFF_WHITE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        if (readyToPlay) {
            if (TAKING_SCREENSHOT_NUMBER == -1) {
                updatePlayerPosition();
            }
            if (shouldSpawnEnemy()) {
                spawnEnemy();
            }
            checkForCollisions();
            updateTimeSensitiveVariables();
        } else if (assetManager.update()) {
            readyToPlay = true;
            finishedLoadingAssets();
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    private void setupConstants() {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        midX = width / 2;
        midY = height / 2;
        AssetSizeUtil.setup(width, height);
        playerRadius = AssetSizeUtil.getHeightIndifferentConstant();
        jumpHeight = playerRadius * 2.2;
        centerCircleRadius = playerRadius * 8.375;
        centerCircleCenter = new Point(midX, midY);
        roundButtonRadius = AssetSizeUtil.inGameFontSize(14);
        if (TAKING_SCREENSHOT_NUMBER == 1) {
            currentCircleColorIndex = 1;
        } else if (TAKING_SCREENSHOT_NUMBER == 2) {
            currentCircleColorIndex = 3;
        } else {
            currentCircleColorIndex = (int) (Math.random() * CENTER_CIRCLE_COLORS.length);
        }
    }

    private void loadSplashScreen() {
        Texture splashLogoTexture = new Texture("splash_logo.png");
        splashLogoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        splashScreenLogo = new Image(splashLogoTexture);
        splashScreenLogo.setBounds(0, 0, width * 0.6, width * 0.6 * (splashScreenLogo.getHeight() / splashScreenLogo.getWidth()));
        splashScreenLogo.setPosition((width - splashScreenLogo.getWidth()) / 2, (height - splashScreenLogo.getHeight()) / 2);
        stage.addActor(splashScreenLogo);
    }

    private void loadAssets() {
        assetManager = new AssetManager();
        fontFactory = new FontFactory(assetManager);
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, null, new FreetypeFontLoader(resolver));

        fontFactory.loadFont(REGULAR_FONT, DEFAULT_FONT_NAME, AssetSizeUtil.inGameFontSize(18));
        fontFactory.loadFont(TAP_TO_JUMP_FONT, DEFAULT_FONT_NAME, AssetSizeUtil.inGameFontSize(18 * 0.9));
        fontFactory.loadFont(RECTANGLE_BUTTON_FONT, DEFAULT_FONT_NAME, AssetSizeUtil.inGameFontSize(14));
        fontFactory.loadFont(SCORE_FONT, DEFAULT_FONT_NAME, AssetSizeUtil.getHeightIndifferentConstant() * 3.75);

        TextureLoader.TextureParameter squareTextureParameter = new TextureLoader.TextureParameter();
        squareTextureParameter.minFilter = Texture.TextureFilter.Linear;
        squareTextureParameter.magFilter = Texture.TextureFilter.Linear;
        squareTextureParameter.genMipMaps = true;
        TextureLoader.TextureParameter rectangularTextureParameter = new TextureLoader.TextureParameter();
        rectangularTextureParameter.minFilter = Texture.TextureFilter.Linear;
        rectangularTextureParameter.magFilter = Texture.TextureFilter.Linear;

        assetManager.load("sleepingPlayer.png", Texture.class, squareTextureParameter);
        for (CharacterSkin skin : characterSkinStore.getAllSkins()) {
            assetManager.load(skin.imageName, Texture.class, squareTextureParameter);
        }
        assetManager.load("enemy.png", Texture.class, squareTextureParameter);
        assetManager.load("killerEnemy.png", Texture.class, rectangularTextureParameter);
        assetManager.load("inGameLogo.png", Texture.class, rectangularTextureParameter);

        assetManager.load("noAds.png", Texture.class, squareTextureParameter);
        assetManager.load("fa-volume-off.png", Texture.class, squareTextureParameter);
        assetManager.load("fa-volume-up.png", Texture.class, squareTextureParameter);

        assetManager.load("die.wav", Sound.class);
        assetManager.load("jump1.wav", Sound.class);
        assetManager.load("jump2.wav", Sound.class);
        assetManager.load("jump3.wav", Sound.class);
    }

    private void finishedLoadingAssets() {
        splashScreenLogo.remove();
        loadSounds();
        setupCenterCircle();
        Gdx.input.setInputProcessor(stage);
        setupPlayer();
        setupLabels();
        setupButtons();
        setupGroups();
        if (TAKING_SCREENSHOT_NUMBER == 2) {
            tapToJumpLabel.setVisibility(false);
            score = 13;
            gameActivityStore.setHighScore(13);
            gameOver(getFakeEnemyForScreenshot());
        } else {
            setToSleeping();
        }
        updateRemoveAdsButton();
    }

    private void loadSounds() {
        deathSound = assetManager.get("die.wav", Sound.class);
        jumpSounds.add(assetManager.get("jump1.wav", Sound.class));
        jumpSounds.add(assetManager.get("jump2.wav", Sound.class));
        jumpSounds.add(assetManager.get("jump3.wav", Sound.class));
    }

    private void setupCenterCircle() {
        centerCircle = new CircleSprite(centerCircleRadius, new Color(CENTER_CIRCLE_COLORS[currentCircleColorIndex]));
        centerCircle.setOrigin((float) centerCircleRadius, (float) centerCircleRadius);
        centerCircle.setPosition(midX - centerCircleRadius , midY - centerCircleRadius);
        stage.addActor(centerCircle);
    }

    private void setupPlayer() {
        player = new Player(playerRadius * 2, playerRadius, -Math.PI / 2, 0.5, assetManager.get("sleepingPlayer.png", Texture.class), jumpHeight);
        player.setZIndex(centerCircle.getZIndex() + 1);
        if (TAKING_SCREENSHOT_NUMBER == 1) {
            player.setPosition(midX - playerRadius, midY + centerCircleRadius);
        } else if (TAKING_SCREENSHOT_NUMBER == 2) {
            player.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(characterSkinStore.getSelectedCharacterSkin().imageName, Texture.class))));
            player.setPosition(midX - playerRadius, midY - playerRadius);
        } else {
            player.setRotation(180);
            player.setPosition(midX - playerRadius, midY - centerCircleRadius - playerRadius * 2);
        }
        stage.addActor(player);
    }

    private void setupLabels() {
        double labelSpacing = AssetSizeUtil.inGameFontSize(18) * 0.7;
        double lineHeight = AssetSizeUtil.inGameFontSize(18) * 0.825;
        logo = new Image(assetManager.get("inGameLogo.png", Texture.class));
        logo.setBounds(0, 0, lineHeight * 1.35 * (logo.getWidth() / logo.getHeight()), lineHeight * 1.35);
        logo.setPosition((width - logo.getWidth()) / 2, height - lineHeight * 0.75 - logo.getHeight() / 2 - (midY - centerCircleRadius * 0.25 - playerRadius * 2 - labelSpacing * 3 - lineHeight * 4.5) / 2);
        logo.setVisibility(false);
        stage.addActor(logo);
        gameOverLabel = new CenteredLabel(fontFactory.get(REGULAR_FONT), Color.OFF_BLACK, height, midX, height - 2 * lineHeight - labelSpacing - (midY - centerCircleRadius * 0.25 - playerRadius * 2 - labelSpacing * 3 - lineHeight * 4.5) / 2);
        gameOverLabel.setVisibility(false);
        stage.addActor(gameOverLabel);
        gameOverScoreLabel = new CenteredLabel(fontFactory.get(REGULAR_FONT), Color.OFF_BLACK, height, midX, height - 3 * lineHeight - labelSpacing * 2 - (midY - centerCircleRadius * 0.25 - playerRadius * 2 - labelSpacing * 3 - lineHeight * 4.5) / 2);
        gameOverScoreLabel.setVisibility(false);
        stage.addActor(gameOverScoreLabel);
        gameOverHighScoreLabel = new CenteredLabel(fontFactory.get(REGULAR_FONT), Color.OFF_BLACK, height, midX, height - 4 * lineHeight - labelSpacing * 3 - (midY - centerCircleRadius * 0.25 - playerRadius * 2 - labelSpacing * 3 - lineHeight * 4.5) / 2);
        gameOverHighScoreLabel.setVisibility(false);
        stage.addActor(gameOverHighScoreLabel);
        tapToJumpLabel = new CenteredLabel("ONE POINT PER TAP", fontFactory.get(TAP_TO_JUMP_FONT), Color.OFF_BLACK, height, midX, (midY - centerCircleRadius) * 0.5);
        stage.addActor(tapToJumpLabel);
        inGameScoreLabel = new CenteredLabel("0", fontFactory.get(SCORE_FONT), Color.OFF_WHITE, height, midX, midY);
        updateInGameScoreLabel();
        stage.addActor(inGameScoreLabel);
    }

    private void setupButtons() {
        double rectButtonHeight = AssetSizeUtil.inGameFontSize(24);
        double buttonSpacing = rectButtonHeight * 0.35;
        double smallButtonWidth = rectButtonHeight * 2.5;
        double largeButtonWidth = smallButtonWidth * 2 + buttonSpacing;

        RectangleButton.Style rectangleButtonStyle = new RectangleButton.Style(Color.OFF_BLACK, Color.OFF_WHITE, fontFactory.get(RECTANGLE_BUTTON_FONT));
        playAgainButtonStyle = new RectangleButton.Style(Color.valueOf(CENTER_CIRCLE_COLORS[currentCircleColorIndex]), Color.OFF_WHITE, fontFactory.get(RECTANGLE_BUTTON_FONT));

        playAgainButton = new RectangleButton(largeButtonWidth, rectButtonHeight, "Play Again", playAgainButtonStyle);
        playAgainButton.setVisibility(false);
        playAgainButton.setPosition((width - largeButtonWidth) / 2, (midY - centerCircleRadius * 0.25 - buttonSpacing * 5 - 4 * rectButtonHeight) / 2);
        playAgainButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (gameState.isGameOver() && !isTransitioningToSleeping && !isTransitioningToMenu) {
                    setToSleeping();
                }
            }
        });
        stage.addActor(playAgainButton);

        statsButton = new RectangleButton(largeButtonWidth, rectButtonHeight, "Stats", rectangleButtonStyle);
        statsButton.setVisibility(false);
        statsButton.setPosition((width - largeButtonWidth) / 2, (rectButtonHeight + buttonSpacing) + (midY - centerCircleRadius * 0.25 - buttonSpacing * 5 - 4 * rectButtonHeight) / 2);
        statsButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (gameState.isGameOver() && !isTransitioningToMenu) {
                    bus.post(new ShowStatsActivityEvent());
                }
            }
        });
        stage.addActor(statsButton);

        skinsButton = new RectangleButton(largeButtonWidth, rectButtonHeight, "Skins", rectangleButtonStyle);
        skinsButton.setVisibility(false);
        skinsButton.setPosition((width - largeButtonWidth) / 2, 2 * (rectButtonHeight + buttonSpacing) + (midY - centerCircleRadius * 0.25 - buttonSpacing * 5 - 4 * rectButtonHeight) / 2);
        skinsButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (gameState.isGameOver() && !isTransitioningToMenu) {
                    skinsButton.endTextPulsate();
                    bus.post(new ShowSkinsActivityEvent());
                }
            }
        });
        stage.addActor(skinsButton);

        shareButton = new RectangleButton(smallButtonWidth, rectButtonHeight, "Share", rectangleButtonStyle);
        shareButton.setVisibility(false);
        shareButton.setPosition((width + buttonSpacing) / 2, 3 * (rectButtonHeight + buttonSpacing) + (midY - centerCircleRadius * 0.25 - buttonSpacing * 5 - 4 * rectButtonHeight) / 2);
        shareButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (gameState.isGameOver() && !isTransitioningToMenu && !takingScreenshot) {
                    takingScreenshot = true;
                    bus.post(new ShowShareDialogEvent(score, ScreenshotUtil.takeScreenShot()));
                    takingScreenshot = false;
                }
            }
        });
        stage.addActor(shareButton);

        rateButton = new RectangleButton(smallButtonWidth, rectButtonHeight, "Rate", rectangleButtonStyle);
        rateButton.setVisibility(false);
        rateButton.setPosition((width - buttonSpacing) / 2 - smallButtonWidth, 3 * (rectButtonHeight + buttonSpacing) + (midY - centerCircleRadius * 0.25 - buttonSpacing * 5 - 4 * rectButtonHeight) / 2);
        rateButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (gameState.isGameOver() && !isTransitioningToMenu) {
                    rateButton.endTextPulsate();
                    gameActivityStore.setHasRatedApp(true);
                    characterSkinStore.checkForAnyNewUnlockedSkins(false);
                    if (gameActivityStore.hasUnseenLockedCharacterSkins()) {
                        skinsButton.beginTextPulsate();
                    }
                    bus.post(new RateAppEvent());
                }
            }
        });
        stage.addActor(rateButton);

        boolean isMuted = gameActivityStore.isMuted();
        muteButton = new RoundButton(roundButtonRadius, isMuted ? assetManager.get("fa-volume-off.png", Texture.class) : assetManager.get("fa-volume-up.png", Texture.class), isMuted ? roundButtonRadius * 0.7 : roundButtonRadius, isMuted ? -roundButtonRadius / 32 : 0, Color.CLEAR, Color.OFF_BLACK);
        muteButton.setPositionCenter(width - roundButtonRadius, roundButtonRadius);
        muteButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                muteOrUnmute();
            }
        });
        stage.addActor(muteButton);

        removeAdsButton = new RoundButton(roundButtonRadius, assetManager.get("noAds.png", Texture.class), roundButtonRadius, 0, Color.CLEAR, Color.OFF_BLACK);
        removeAdsButton.setPositionCenter(roundButtonRadius, roundButtonRadius);
        removeAdsButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (!gameActivityStore.hasPaidToRemoveAds() && removeAdsButtonVisible) {
                    bus.post(new PurchaseAdRemovalEvent());
                }
            }
        });
        updateRemoveAdsButton();
        stage.addActor(removeAdsButton);
    }

    private void setupGroups() {
        menuElements.add(gameOverScoreLabel);
        menuElements.add(gameOverLabel);
        menuElements.add(logo);
        menuElements.add(gameOverHighScoreLabel);
        menuElements.add(playAgainButton);
        menuElements.add(statsButton);
        menuElements.add(shareButton);
        menuElements.add(skinsButton);
        menuElements.add(rateButton);
        inGameElements.add(inGameScoreLabel);
        inGameElements.add(tapToJumpLabel);
    }

    private void setToSleeping() {
        for (Image enemy : enemies) {
            enemy.addAction(Actions.sequence(Actions.scaleTo(0.01f, 0.01f, 0.3f), Actions.removeActor()));
        }
        enemies.clear();
        removeKiller();
        isTransitioningToSleeping = true;

        if (gameState.isGameOver()) {
            centerCircle.addAction(Actions.scaleTo(1, 1, 0.5f));

            for (AhhhroundGameElement element : menuElements) {
                element.fadeOut(0.5);
            }
            for (AhhhroundGameElement element : inGameElements) {
                element.fadeIn(0.5);
            }
            rateButton.endTextPulsate();
            skinsButton.endTextPulsate();
            player.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (spinPlayerAction != null) {
                        player.removeAction(spinPlayerAction);
                    }
                    if (movePlayerToCenterAction != null) {
                        player.removeAction(movePlayerToCenterAction);
                    }
                    isTransitioningToSleeping = false;
                    player.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get("sleepingPlayer.png", Texture.class))));
                    player.setPositionAroundCircle(-Math.PI / 2, centerCircleRadius, centerCircleCenter);
                    player.fadeIn(0.5);
                }
            })));
        } else {
            for (AhhhroundGameElement element : inGameElements) {
                element.fadeIn(0.5);
            }
            isTransitioningToSleeping = false;
        }
        score = 0;
        updateInGameScoreLabel();
        player.addAction(Actions.sequence(Actions.delay(0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                gameState = GameState.SLEEPING;
            }
        })));
    }

    private void startPlaying() {
        tapToJumpLabel.fadeOut(0.5);
        gameState = GameState.PLAYING;
        player.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(characterSkinStore.getSelectedCharacterSkin().imageName, Texture.class))));
        timeSinceLastEnemySpawn = ENEMY_SPAWN_TIME;
    }

    private void updateInGameScoreLabel() {
        inGameScoreLabel.setText("" + score);
    }

    private void updatePlayerPosition() {
        deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f);

        if (!gameState.isGameOver()) {
            player.updateAngleAndPosition(deltaTime, centerCircleRadius, centerCircleCenter);
        }
    }

    private boolean shouldSpawnEnemy() {
        return gameState.isPlaying() && timeSinceLastEnemySpawn >= ENEMY_SPAWN_TIME;
    }

    private void spawnEnemy() {
        double minAngle = player.getCurrentAngleFromCenter() + Math.PI / 2 + Math.PI / 4;
        double selectedAngle = (minAngle * 1000 + Math.random() * Math.PI * 500.0) / 1000.0;
        Character enemy = new Character(playerRadius * 2, playerRadius, selectedAngle, 0.5, assetManager.get("enemy.png", Texture.class));
        enemy.setPositionAroundCircle(centerCircleRadius, centerCircleCenter);
        stage.addActor(enemy);
        enemies.add(enemy);
        timeSinceLastEnemySpawn = 0;
    }

    private void finishedJump() {
        if (!gameState.isGameOver()) {
            score++;
            updateInGameScoreLabel();
            increaseDifficulty();
            if (shouldChangeCenterCircleColor()) {
                changeCenterCircleColor();
            }
            killJumpedEnemies();
            if (timeSinceLastJumpAttempt < TIME_DELTA_TO_DETECT_PREMATURE_JUMPS) {
                jump();
            }
        }
    }

    private boolean shouldChangeCenterCircleColor() {
        return score % JUMP_COUNT_BEFORE_CIRCLE_COLOR_CHANGE == 0;
    }

    private void changeCenterCircleColor() {
        if (currentCircleColorIndex == CENTER_CIRCLE_COLORS.length - 1) {
            currentCircleColorIndex = 0;
        } else {
            currentCircleColorIndex++;
        }
        centerCircle.addAction(Actions.color(Color.valueOf(CENTER_CIRCLE_COLORS[currentCircleColorIndex]), 1));
        playAgainButtonStyle.setButtonColor(Color.valueOf(CENTER_CIRCLE_COLORS[currentCircleColorIndex]));
    }

    private void killJumpedEnemies() {
        for (int i = enemies.size() - 1; i > -1; i--) {
            double enemyAngle = enemies.get(i).getCurrentAngleFromCenter();
            double jumpEndAngle = player.getCurrentAngleFromCenter();
            if (AngleUtil.isAngleBetween(enemyAngle, player.getJumpStartAngle(), jumpEndAngle)) {
                enemies.get(i).addAction(Actions.sequence(Actions.scaleTo(0.01f, 0.01f, 0.3f), Actions.removeActor()));
                enemies.remove(i);
            }
        }
    }

    private void increaseDifficulty() {
        if (score < 100) {
            player.accelerate();
        }
    }

    private void checkForCollisions() {
        if (!gameState.isGameOver()) {
            for (Character enemy : enemies) {
                if (Math.sqrt((enemy.getX() - player.getX()) * (enemy.getX() - player.getX()) + (enemy.getY() - player.getY()) * (enemy.getY() - player.getY())) < (playerRadius * 2)) {
                    gameOver(enemy);
                }
            }
        }
    }

    private void updateTimeSensitiveVariables() {
        timeSinceLastEnemySpawn += Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f);
        timeSinceLastJumpAttempt += Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f);
    }

    private Character getFakeEnemyForScreenshot() {
        double selectedAngle = Math.PI * 0.75;
        Character enemy = new Character(playerRadius * 2, playerRadius, selectedAngle, 0.5f, assetManager.get("enemy.png", Texture.class));
        enemy.setPositionAroundCircle(centerCircleRadius, centerCircleCenter);
        stage.addActor(enemy);
        enemies.add(enemy);
        timeSinceLastEnemySpawn = 0;
        return enemy;
    }

    private void gameOver(Character enemyThatKilledPlayer) {
        isTransitioningToMenu = true;
        gamesSinceLastAd++;
        gameState = GameState.GAME_OVER;
        centerCircle.addAction(Actions.scaleTo(0.25f, 0.25f, 0.5f));
        stage.addAction(Actions.sequence(Actions.delay(0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (!gameActivityStore.hasPaidToRemoveAds()
                        && gamesSinceLastAd > MIN_GAME_COUNT_BEFORE_AD_IS_DISPLAYED
                        && System.currentTimeMillis() - timeLastAdDisplayed >= MIN_TIME_BEFORE_AD_IS_DISPLAYED * 1000) {
                    bus.post(new ShowAdEvent());
                }
            }
        })));
        scoreUpdater.updateWithScore(score);
        transformIntoKillerEnemy(enemyThatKilledPlayer);

        for (Character enemy : enemies) {
            if (enemy == enemyThatKilledPlayer) {
                Point enemyPosition = enemy.getPosition(centerCircleRadius * 0.25, centerCircleCenter);
                enemy.addAction(Actions.sequence(Actions.moveTo((float) enemyPosition.x, (float) enemyPosition.y, 0.5f), Actions.removeActor()));
            } else {
                enemy.addAction(Actions.sequence(Actions.scaleTo(0.01f, 0.01f, 0.3f), Actions.removeActor()));
            }
        }

        player.die();
        if (!gameActivityStore.isMuted()) {
            deathSound.play();
        }
        enemyThatKilledPlayer.fadeOut(0.5);
        if (TAKING_SCREENSHOT_NUMBER != 2) {
            spinPlayerAction = Actions.repeat(-1, Actions.rotateBy(180, 1));
            player.addAction(spinPlayerAction);
            movePlayerToCenterAction = Actions.moveTo((float) (midX - playerRadius), (float) (midY - playerRadius), 0.5f);
            player.addAction(movePlayerToCenterAction);
        }
        gameOverLabel.setText(GAME_OVER_PHRASES[(int) (Math.random() * GAME_OVER_PHRASES.length)]);
        gameOverScoreLabel.setText("SCORE: " + score);
        gameOverHighScoreLabel.setText("BEST: " + gameActivityStore.getHighScore());

        for (AhhhroundGameElement element : menuElements) {
            element.fadeIn(0.5);
        }
        for (AhhhroundGameElement element : inGameElements) {
            element.fadeOut(0.5);
        }
        stage.addAction(Actions.delay(0.5f, Actions.run(new Runnable() {
            @Override
            public void run() {
                isTransitioningToMenu = false;
                if (!gameActivityStore.hasRatedApp() && gameActivityStore.getTotalPlays() > PLAY_COUNT_BEFORE_RATE_PULSE) {
                    rateButton.beginTextPulsate();
                }
                if (gameActivityStore.hasUnseenLockedCharacterSkins()) {
                    skinsButton.beginTextPulsate();
                }
            }
        })));
    }

    private void transformIntoKillerEnemy(Character enemyThatKilledPlayer) {
        double killerWidthToPlayerWidthRatio = 1.1085;
        double percentageOfKillerBodyToVerticalMidpoint = 0.4742;
        killer = new Character(playerRadius * 2 * killerWidthToPlayerWidthRatio, playerRadius, enemyThatKilledPlayer.getCurrentAngleFromCenter(), percentageOfKillerBodyToVerticalMidpoint, assetManager.get("killerEnemy.png", Texture.class));
        killer.setPositionAroundCircle(centerCircleRadius, centerCircleCenter);
        killer.setVisibility(false);
        killer.setZIndex(player.getZIndex() + 1);
        Point killerEnemyPosition = killer.getPosition(centerCircleRadius * 0.25, centerCircleCenter);
        killer.addAction(Actions.moveTo((float) killerEnemyPosition.x, (float) killerEnemyPosition.y, 0.5f));
        killer.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.rotateTo(0, 1)));
        enemyThatKilledPlayer.setZIndex(killer.getZIndex() + 1);
        stage.addActor(killer);
    }

    private void removeKiller() {
        if (killer != null) {
            killer.addAction(Actions.sequence(Actions.scaleTo(0.01f, 0.01f, 0.3f), Actions.removeActor()));
        }
    }

    private void jump() {
        if (!gameState.isGameOver()) {
            if (!gameState.isPlaying() && !isTransitioningToSleeping) {
                startPlaying();
            }
            timeSinceLastJumpAttempt = 0;
            if (!player.isJumping()) {
                player.jumpWithCallback(new Runnable() {
                    @Override
                    public void run() {
                        finishedJump();
                    }
                });
                if (!gameActivityStore.isMuted()) {
                    int selectedSoundIndex = (int) (Math.random() * jumpSounds.size());
                    jumpSounds.get(selectedSoundIndex).play();
                }
            }
        }

    }

    private void muteOrUnmute() {
        boolean isMuted = gameActivityStore.isMuted();
        isMuted = !isMuted;
        muteButton.setIcon(isMuted ? assetManager.get("fa-volume-off.png", Texture.class) : assetManager.get("fa-volume-up.png", Texture.class));
        muteButton.setIconSizeAndRightOffset(isMuted ? roundButtonRadius * 0.7 : roundButtonRadius, isMuted ? -roundButtonRadius / 32 : 0);
        gameActivityStore.setIsMuted(isMuted);
    }

    @Subscribe
    public void successfullyShowedAd(SuccessfullyShowedAdEvent event) {
        timeLastAdDisplayed = System.currentTimeMillis();
        gamesSinceLastAd = 0;
    }

    @Subscribe
    public void updateRemoveAdsButton(UpdateRemoveAdsButtonEvent event) {
        removeAdsButtonVisible = event.visible;
        updateRemoveAdsButton();
    }

    public void updateRemoveAdsButton() {
        if (removeAdsButton != null) {
            removeAdsButton.setVisibility(!gameActivityStore.hasPaidToRemoveAds() && removeAdsButtonVisible);
        }
    }
}
