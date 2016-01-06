package com.gamestridestudios.ahhh_round;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gamestridestudios.ahhh_round.components.CenteredLabel;
import com.gamestridestudios.ahhh_round.components.RoundButton;
import com.gamestridestudios.ahhh_round.events.EnableLeaderboardEvent;
import com.gamestridestudios.ahhh_round.events.RateAppEvent;
import com.gamestridestudios.ahhh_round.events.ShowInterstitialAdEvent;
import com.gamestridestudios.ahhh_round.events.ShowLeaderboardEvent;
import com.gamestridestudios.ahhh_round.events.ShowShareDialogEvent;
import com.gamestridestudios.ahhh_round.events.SuccessfullyShowedAdEvent;
import com.gamestridestudios.ahhh_round.events.UpdateGooglePlayGamesEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AhhhRound extends ApplicationAdapter {
    public static final boolean IS_TAKING_FIRST_SCREENSHOT = false;
    public static final boolean IS_TAKING_SECOND_SCREENSHOT = false;

    private static final String[] CENTER_CIRCLE_COLORS = new String[]{"2E4052","669BBC","0F7173","3C6997","789ABB","419D78","618985","2274A5","555B6E","437C90"};
    private static final String[] GAME_OVER_PHRASES = new String[]{"Oops!", "Ouch!", "Ahhh!", "Yikes!", "Oh no!", "Uh oh!"};
    private static final String COLOR_OFF_WHITE_STRING = "eaf2e3";
    private static final String COLOR_OFF_BLACK_STRING = "595758";
    private static final String COLOR_GRAY_STRING = "CFD4C5";
    private static final int gameCountBeforeAdIsDisplayed = 4;
    private static final int jumpCountBeforeCircleColorChange = 5;
    private static final int timeBeforeAdIsDisplayed = 100;

    private Stage stage;
    private Bus bus;
    private Preferences prefs;
    private BitmapFont defaultFont;
    private BitmapFont scoreFont;
    private AssetManager assetManager;
    private Color colorOffWhite;
    private Color colorOffBlack;
    private Color colorGray;
    private List<Sound> jumpSounds;
    private Sound dieSound;

    private boolean readyToPlay;
    private boolean isGameOver;
    private boolean isTransitioningToSleeping;
    private boolean isTransitioningToMenu;
    private boolean isGameStarted;
    private boolean isLeaderboardEnabled;
    private float deltaTime = -1;
    private int score;
    private long timeLastAdDisplayed;
    private int gamesSinceLastAd;
    private double jumpElapsedTime = -1;
    private float jumpStartAngle;

    private double jumpAirTime;
    private double playerSpeed;
    private double enemySpawnTime;
    private boolean shouldBeSpawningEnemies;
    private double timeSinceLastEnemySpawn;
    private double enemySpawnTimeDeceleration;
    private double playerAcceleration;
    private float width;
    private float height;
    private float centerCircleRadius;
    private float buttonDistanceFromCenter;
    private float buttonRadius;
    private float playerRadius;
    private float jumpHeight;
    private int regularFontSize;
    private int scoreFontSize;
    private int currentCircleColorIndex;
    private List<Image> enemies;
    private Image happyEnemy;
    private Image player;
    private Image centerCircle;
    private Image splashScreenLogo;
    private CenteredLabel gameOverLabel;
    private CenteredLabel gameOverScoreLabel;
    private CenteredLabel gameOverHighScoreLabel;
    private CenteredLabel inGameScoreLabel;
    private CenteredLabel tapToJumpLabel;
    private RoundButton playAgainButton;
    private RoundButton leaderboardButton;
    private RoundButton muteButton;
    private RoundButton shareButton;
    private RoundButton rateButton;
    private Action spinPlayerAction;
    private Action movePlayerToCenterAction;

    public AhhhRound(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    @Override
    public void create() {
        stage = new Stage();
        stage.addListener(new JumpListener());
        prefs = Gdx.app.getPreferences("Ahhh-round");
        setupConstants();
        loadSplashScreen();
        loadAssets();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(colorOffWhite.r, colorOffWhite.g, colorOffWhite.b, colorOffWhite.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        if (readyToPlay) {
            if (!IS_TAKING_FIRST_SCREENSHOT && !IS_TAKING_SECOND_SCREENSHOT) {
                updatePlayerPosition();
            }
            spawnEnemyIfNecessary();
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
        centerCircleRadius = width / 3.3f;
        buttonDistanceFromCenter = centerCircleRadius / 1.8f;
        buttonRadius = centerCircleRadius / 4f;
        playerRadius  = centerCircleRadius / 8.375f;
        jumpHeight = playerRadius * 2.1963f;
        regularFontSize = (int) (width / 11f);
        scoreFontSize = (int) (width / 6.4f);
        if (IS_TAKING_FIRST_SCREENSHOT) {
            currentCircleColorIndex = 1;
        } else if (IS_TAKING_SECOND_SCREENSHOT) {
            currentCircleColorIndex = 3;
        } else {
            currentCircleColorIndex = (int) (Math.random() * CENTER_CIRCLE_COLORS.length);
        }
        colorOffWhite = Color.valueOf(COLOR_OFF_WHITE_STRING);
        colorOffBlack = Color.valueOf(COLOR_OFF_BLACK_STRING);
        colorGray = Color.valueOf(COLOR_GRAY_STRING);
    }

    private void loadSplashScreen() {
        Texture splashLogoTexture = new Texture("splash_logo.png");
        splashLogoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        splashScreenLogo = new Image(splashLogoTexture);
        splashScreenLogo.setBounds(0, 0, width * 0.6f, width * 0.6f * (splashScreenLogo.getHeight() / splashScreenLogo.getWidth()));
        splashScreenLogo.setPosition((width - splashScreenLogo.getWidth()) / 2, (height - splashScreenLogo.getHeight()) / 2);
        stage.addActor(splashScreenLogo);
    }

    private void loadAssets() {
        assetManager = new AssetManager();
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, null, new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter defaultFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        defaultFontParams.fontFileName = "Quicksand-Bold.otf";
        defaultFontParams.fontParameters.size = regularFontSize;
        assetManager.load("defaultFont", BitmapFont.class, defaultFontParams);
        FreetypeFontLoader.FreeTypeFontLoaderParameter scoreFontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        scoreFontParams.fontFileName = "Quicksand-Bold.otf";
        scoreFontParams.fontParameters.size = scoreFontSize;
        assetManager.load("scoreFont", BitmapFont.class, scoreFontParams);

        TextureLoader.TextureParameter fastSquareTexture = new TextureLoader.TextureParameter();
        fastSquareTexture.minFilter = Texture.TextureFilter.MipMapNearestNearest;
        fastSquareTexture.magFilter = Texture.TextureFilter.Linear;
        fastSquareTexture.genMipMaps = true;
        TextureLoader.TextureParameter niceSqaureTexture = new TextureLoader.TextureParameter();
        niceSqaureTexture.minFilter = Texture.TextureFilter.Linear;
        niceSqaureTexture.magFilter = Texture.TextureFilter.Linear;
        niceSqaureTexture.genMipMaps = true;
        TextureLoader.TextureParameter niceRectangularTexture = new TextureLoader.TextureParameter();
        niceRectangularTexture.minFilter = Texture.TextureFilter.Linear;
        niceRectangularTexture.magFilter = Texture.TextureFilter.Linear;

        assetManager.load("sleeping_player.png", Texture.class, IS_TAKING_FIRST_SCREENSHOT ? niceSqaureTexture : fastSquareTexture);
        assetManager.load("player.png", Texture.class, fastSquareTexture);
        assetManager.load("dead_player.png", Texture.class, niceSqaureTexture);
        assetManager.load("enemy.png", Texture.class, niceSqaureTexture);
        assetManager.load("happy_enemy.png", Texture.class, niceRectangularTexture);

        assetManager.load("fa-play.png", Texture.class, niceSqaureTexture);
        assetManager.load("fa-star.png", Texture.class, niceSqaureTexture);
        assetManager.load("fa-share-alt.png", Texture.class, niceSqaureTexture);
        assetManager.load("fa-volume-off.png", Texture.class, niceSqaureTexture);
        assetManager.load("fa-volume-up.png", Texture.class, niceSqaureTexture);
        assetManager.load("fa-bar-chart.png", Texture.class, niceSqaureTexture);


        assetManager.load("die.wav", Sound.class);
        assetManager.load("jump1.wav", Sound.class);
        assetManager.load("jump2.wav", Sound.class);
        assetManager.load("jump3.wav", Sound.class);
    }

    private void finishedLoadingAssets() {
        splashScreenLogo.remove();
        setupFonts();
        loadSounds();
        setupCenterCircle();
        setupInputProcessing();
        setupPlayer();
        setupEnemies();
        setupLabels();
        setupButtons();
        if (IS_TAKING_SECOND_SCREENSHOT) {
            enableLeaderboard(null);
            tapToJumpLabel.getColor().a = 0;
            score = 13;
            prefs.putInteger("HIGHSCORE", 13);
            prefs.flush();
            gameOver(getFakeEnemyForScreenshot());
        } else {
            setToSleeping();
        }
    }

    private void setupFonts() {
        scoreFont = assetManager.get("scoreFont", BitmapFont.class);
        defaultFont = assetManager.get("defaultFont", BitmapFont.class);
    }

    private void loadSounds() {
        dieSound = assetManager.get("die.wav", Sound.class);
        jumpSounds = new ArrayList<Sound>();
        jumpSounds.add(assetManager.get("jump1.wav", Sound.class));
        jumpSounds.add(assetManager.get("jump2.wav", Sound.class));
        jumpSounds.add(assetManager.get("jump3.wav", Sound.class));
    }

    private void setupCenterCircle() {
        Pixmap centerCirclePixmap = new Pixmap((int) Math.ceil(centerCircleRadius * 2), (int) Math.ceil(centerCircleRadius * 2), Pixmap.Format.RGBA8888);
        centerCirclePixmap.setColor(Color.WHITE);
        centerCirclePixmap.fillCircle((int) centerCircleRadius, (int) centerCircleRadius, (int) centerCircleRadius);
        Texture centerCircleTexture = new Texture(centerCirclePixmap);
        centerCircleTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        centerCircle = new Image(centerCircleTexture);
        stage.addActor(centerCircle);
        centerCircle.setPosition(width / 2 - centerCircleRadius, height / 2 - centerCircleRadius);
        centerCircle.setColor(Color.valueOf(CENTER_CIRCLE_COLORS[currentCircleColorIndex]));
    }

    private void setupInputProcessing() {
        Gdx.input.setInputProcessor(stage);
    }

    private void setupPlayer() {
        player = new Image(assetManager.get("sleeping_player.png", Texture.class));
        player = new Image();
        player.setBounds(0, 0, playerRadius * 2, playerRadius * 2);
        player.setOrigin(playerRadius, playerRadius);
        if (IS_TAKING_FIRST_SCREENSHOT) {
            player.setPosition(width / 2 - playerRadius, height / 2 + centerCircleRadius);
        } else if (IS_TAKING_SECOND_SCREENSHOT) {
            player.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get("dead_player.png", Texture.class))));
            player.setPosition(width / 2 - playerRadius, height / 2 - playerRadius);
        } else {
            player.setRotation(180);
            player.setPosition(width / 2 - playerRadius, height / 2 - centerCircleRadius - playerRadius * 2);
        }
        stage.addActor(player);
    }

    private void setupEnemies() {
        enemies = new ArrayList<Image>();
    }

    private void setupLabels() {
        Label.LabelStyle defaultStyle = new Label.LabelStyle(defaultFont, colorOffBlack);
        gameOverLabel = new CenteredLabel("", defaultStyle, regularFontSize, height, width / 2, height - (height / 2 - centerCircleRadius) * 0.25f);
        gameOverLabel.getColor().a = 0;
        stage.addActor(gameOverLabel);
        gameOverScoreLabel = new CenteredLabel("", defaultStyle, regularFontSize, height, width / 2, height - (height / 2 - centerCircleRadius) * 0.5f);
        gameOverScoreLabel.getColor().a = 0;
        stage.addActor(gameOverScoreLabel);
        gameOverHighScoreLabel = new CenteredLabel("", defaultStyle, regularFontSize, height, width / 2, height - (height / 2 - centerCircleRadius) * 0.75f);
        gameOverHighScoreLabel.getColor().a = 0;
        stage.addActor(gameOverHighScoreLabel);
        tapToJumpLabel = new CenteredLabel("TAP TO JUMP", defaultStyle, regularFontSize, height, width / 2, (height / 2 - centerCircleRadius) * 0.5f);
        stage.addActor(tapToJumpLabel);
        Label.LabelStyle inGameScoreStyle = new Label.LabelStyle(scoreFont, colorOffWhite);
        inGameScoreLabel = new CenteredLabel("0", inGameScoreStyle, scoreFontSize, height, width / 2, height / 2);
        updateInGameScoreLabel();
        stage.addActor(inGameScoreLabel);
    }

    private void setupButtons() {
        playAgainButton = new RoundButton(buttonRadius, assetManager.get("fa-play.png", Texture.class), buttonRadius * 0.9f, buttonRadius / 12, colorOffWhite, colorOffBlack);
        playAgainButton.getColor().a = 0;
        playAgainButton.setPositionCenter(width / 2, height / 2 - centerCircleRadius / 1.8f);
        playAgainButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (isGameOver && !isTransitioningToSleeping && !isTransitioningToMenu) {
                    setToSleeping();
                }
            }
        });
        stage.addActor(playAgainButton);
        leaderboardButton = new RoundButton(buttonRadius, assetManager.get("fa-bar-chart.png", Texture.class), buttonRadius, 0, colorOffWhite, isLeaderboardEnabled ? colorOffBlack : colorGray);
        leaderboardButton.getColor().a = 0;
        double leaderboardButtonAngle = -Math.PI / 2 + Math.PI * 2.0 / 5.0;
        leaderboardButton.setPositionCenter((float) (width / 2 + Math.cos(leaderboardButtonAngle) * buttonDistanceFromCenter), (float) (height / 2 + Math.sin(leaderboardButtonAngle) * buttonDistanceFromCenter));
        leaderboardButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (isGameOver) {
                    bus.post(new ShowLeaderboardEvent());
                }
            }
        });
        stage.addActor(leaderboardButton);
        muteButton = new RoundButton(buttonRadius, prefs.getBoolean("IS_MUTED") ? assetManager.get("fa-volume-off.png", Texture.class) : assetManager.get("fa-volume-up.png", Texture.class), prefs.getBoolean("IS_MUTED") ? buttonRadius * 0.7f : buttonRadius, prefs.getBoolean("IS_MUTED") ? -buttonRadius / 32 : 0, colorOffWhite, colorOffBlack);
        muteButton.getColor().a = 0;
        double muteButtonAngle = -Math.PI / 2 - Math.PI * 2.0 / 5.0;
        muteButton.setPositionCenter((float) (width / 2 + Math.cos(muteButtonAngle) * buttonDistanceFromCenter), (float) (height / 2 + Math.sin(muteButtonAngle) * buttonDistanceFromCenter));
        muteButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (isGameOver) {
                    muteOrUnmute();
                }
            }
        });
        stage.addActor(muteButton);
        shareButton = new RoundButton(buttonRadius, assetManager.get("fa-share-alt.png", Texture.class), buttonRadius * 0.8f, -buttonRadius / 32, colorOffWhite, colorOffBlack);
        shareButton.getColor().a = 0;
        double shareButtonAngle = -Math.PI / 2 - Math.PI * 4.0 / 5.0;
        shareButton.setPositionCenter((float) (width / 2 + Math.cos(shareButtonAngle) * buttonDistanceFromCenter), (float) (height / 2 + Math.sin(shareButtonAngle) * buttonDistanceFromCenter));
        shareButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (isGameOver) {
                    bus.post(new ShowShareDialogEvent(score));
                }
            }
        });
        stage.addActor(shareButton);
        rateButton = new RoundButton(buttonRadius, assetManager.get("fa-star.png", Texture.class), buttonRadius * 0.9f, 0, colorOffWhite, colorOffBlack);
        rateButton.getColor().a = 0;
        double rateButtonAngle = -Math.PI / 2 + Math.PI * 4.0 / 5.0;
        rateButton.setPositionCenter((float) (width / 2 + Math.cos(rateButtonAngle) * buttonDistanceFromCenter), (float) (height / 2 + Math.sin(rateButtonAngle) * buttonDistanceFromCenter));
        rateButton.setClickListener(new Runnable() {
            @Override
            public void run() {
                if (isGameOver) {
                    bus.post(new RateAppEvent());
                }
            }
        });
        stage.addActor(rateButton);
    }

    private void setToSleeping() {
        for (Image enemy : enemies) {
            enemy.addAction(Actions.sequence(Actions.scaleTo(0.01f, 0.01f, 0.3f), Actions.removeActor()));
        }
        if (happyEnemy != null) {
            happyEnemy.addAction(Actions.sequence(Actions.scaleTo(0.01f, 0.01f, 0.3f), Actions.removeActor()));
        }
        player.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get("sleeping_player.png", Texture.class))));
        enemies.clear();
        jumpAirTime = 0.20;
        playerSpeed = 2f;
        enemySpawnTime = 1.3;
        enemySpawnTimeDeceleration = 0.1;
        playerAcceleration = 0.05f;
        isTransitioningToSleeping = true;

        if (isGameOver) {
            gameOverLabel.addAction(Actions.fadeOut(0.5f));
            gameOverScoreLabel.addAction(Actions.fadeOut(0.5f));
            gameOverHighScoreLabel.addAction(Actions.fadeOut(0.5f));
            playAgainButton.getColor().a = 0;
            leaderboardButton.getColor().a = 0;
            muteButton.getColor().a = 0;
            shareButton.getColor().a = 0;
            rateButton.getColor().a = 0;
            inGameScoreLabel.addAction(Actions.fadeIn(0.5f));
            tapToJumpLabel.addAction(Actions.fadeIn(0.5f));
            player.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (spinPlayerAction != null) {
                        player.removeAction(spinPlayerAction);
                    }
                    if (movePlayerToCenterAction != null) {
                        player.removeAction(movePlayerToCenterAction);
                    }
                }
            }), Actions.moveTo(width / 2 - playerRadius, height / 2 - centerCircleRadius - playerRadius * 2, 0), Actions.rotateTo(180, 0), Actions.run(new Runnable() {
                @Override
                public void run() {
                    isGameOver = false;
                    isTransitioningToSleeping = false;
                    player.addAction(Actions.fadeIn(0.5f));
                }
            })));
        } else {
            inGameScoreLabel.addAction(Actions.fadeIn(0.5f));
            tapToJumpLabel.addAction(Actions.fadeIn(0.5f));
            isTransitioningToSleeping = false;
        }
        score = 0;
        updateInGameScoreLabel();
        isGameStarted = false;
    }

    private void setGameStarted() {
        tapToJumpLabel.addAction(Actions.fadeOut(0.5f));
        isGameStarted = true;
        player.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get("player.png", Texture.class))));
        shouldBeSpawningEnemies = true;
        timeSinceLastEnemySpawn = enemySpawnTime;
    }

    private void updateInGameScoreLabel() {
        inGameScoreLabel.setText("" + score);
    }

    private void updatePlayerPosition() {
        float previousAngle = getPlayerAngleFromCenter();
        if (deltaTime == -1) {
            previousAngle = (float) -Math.PI / 2;
        }

        deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f);

        if (!isGameOver) {
            float upwardMovement = 0;
            if (jumpElapsedTime > -1) {
                jumpElapsedTime += deltaTime;
                upwardMovement = (float) (-4d * Math.pow(1 / jumpAirTime, 2) * jumpHeight * Math.pow((jumpElapsedTime - jumpAirTime) / 2.0, 2) + jumpHeight);
                if (upwardMovement < 0) {
                    upwardMovement = 0;
                    jumpElapsedTime = -1;
                    finishedJump();
                }
            }
            player.setRotation((float) (player.getRotation() + deltaTime * playerSpeed * 180 / Math.PI));
            player.setPosition((float) (Math.cos(previousAngle + deltaTime * playerSpeed) * (centerCircleRadius + playerRadius + upwardMovement) + width / 2d - playerRadius), (float) (Math.sin(previousAngle + deltaTime * playerSpeed) * (centerCircleRadius + playerRadius + upwardMovement) + height / 2d - playerRadius));
        }
    }

    private float getPlayerAngleFromCenter() {
        return (float) Math.atan2(player.getY() - height / 2d + playerRadius, player.getX() - width / 2d + playerRadius);
    }

    private void spawnEnemyIfNecessary() {
        if (shouldBeSpawningEnemies && timeSinceLastEnemySpawn >= enemySpawnTime) {
            Image enemy = new Image(assetManager.get("enemy.png", Texture.class));
            enemy.setBounds(0, 0, playerRadius * 2, playerRadius * 2);
            enemy.setOrigin(playerRadius, playerRadius);
            double minAngle = getPlayerAngleFromCenter() + Math.PI / 2 + Math.PI / 4;
            double selectedAngle = (minAngle * 1000 + Math.random() * Math.PI * 500.0) / 1000;
            enemy.setRotation((float) (selectedAngle * 180.0 / Math.PI - 90));
            enemy.setPosition((float) (Math.cos(selectedAngle) * (centerCircleRadius + playerRadius) + width / 2d - playerRadius), (float) (Math.sin(selectedAngle) * (centerCircleRadius + playerRadius) + height / 2d - playerRadius));
            stage.addActor(enemy);
            enemies.add(enemy);
            timeSinceLastEnemySpawn = 0;
        }
    }

    private void finishedJump() {
        if (!isGameOver) {
            for (int i = enemies.size() - 1; i > -1; i--) {
                double enemyAngle = Math.atan2(enemies.get(i).getY() - height / 2.0, enemies.get(i).getX() - width / 2.0);
                double jumpEndAngle = getPlayerAngleFromCenter();
                if (isAngleBetween(enemyAngle, jumpStartAngle, jumpEndAngle)) {
                    enemies.get(i).addAction(Actions.sequence(Actions.scaleTo(0.01f, 0.01f, 0.3f), Actions.removeActor()));
                    enemies.remove(i);
                    score++;
                    updateInGameScoreLabel();
                    increaseDifficulty();
                    if (score % jumpCountBeforeCircleColorChange == 0) {
                        if (currentCircleColorIndex == CENTER_CIRCLE_COLORS.length - 1) {
                            currentCircleColorIndex = 0;
                        } else {
                            currentCircleColorIndex++;
                        }
                        centerCircle.addAction(Actions.color(Color.valueOf(CENTER_CIRCLE_COLORS[currentCircleColorIndex]), 1));
                    }
                }
            }
        }
    }

    private void increaseDifficulty() {
        if (score < 25) {
            double oldSpeed = playerSpeed;
            playerSpeed += playerAcceleration;
            playerAcceleration /= 1.05;
            jumpAirTime = jumpAirTime * (oldSpeed / playerSpeed);
            enemySpawnTime -= enemySpawnTimeDeceleration * 0.3;
            enemySpawnTimeDeceleration /= 1.05;
        }
    }

    private void checkForCollisions() {
        if (!isGameOver) {
            for (Image enemy : enemies) {
                if (Math.sqrt((enemy.getX() - player.getX()) * (enemy.getX() - player.getX()) + (enemy.getY() - player.getY()) * (enemy.getY() - player.getY())) < (playerRadius * 2)) {
                    gameOver(enemy);
                }
            }
        }
    }

    private void updateTimeSensitiveVariables() {
        timeSinceLastEnemySpawn += Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f);
    }

    private boolean isAngleBetween(double target, double angle1, double angle2) {
        target = target * (180.0 / Math.PI);
        angle1 = angle1 * (180.0 / Math.PI);
        angle2 = angle2 * (180.0 / Math.PI);

        target = (360 + (target % 360)) % 360;
        angle1 = (3600000 + angle1) % 360;
        angle2 = (3600000 + angle2) % 360;

        if (angle1 < angle2) {
            return angle1 <= target && target <= angle2;
        }
        return angle1 <= target || target <= angle2;
    }

    private Image getFakeEnemyForScreenshot() {
        Image enemy = new Image(assetManager.get("enemy.png", Texture.class));
        enemy.setBounds(0, 0, playerRadius * 2, playerRadius * 2);
        enemy.setOrigin(playerRadius, playerRadius);
        double selectedAngle = Math.PI * 0.75;
        enemy.setRotation((float) (selectedAngle * 180.0 / Math.PI - 90));
        enemy.setPosition((float) (Math.cos(selectedAngle) * (centerCircleRadius + playerRadius) + width / 2d - playerRadius), (float) (Math.sin(selectedAngle) * (centerCircleRadius + playerRadius) + height / 2d - playerRadius));
        stage.addActor(enemy);
        enemies.add(enemy);
        timeSinceLastEnemySpawn = 0;
        return enemy;
    }

    private void gameOver(Image enemyThatKilledPlayer) {
        isTransitioningToMenu = true;
        gamesSinceLastAd++;
        stage.addAction(Actions.sequence(Actions.delay(0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                if (gamesSinceLastAd > gameCountBeforeAdIsDisplayed && System.currentTimeMillis() - timeLastAdDisplayed >= timeBeforeAdIsDisplayed * 1000) {
                    bus.post(new ShowInterstitialAdEvent());
                }
            }
        })));
        updateHighScore();
        player.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get("dead_player.png", Texture.class))));
        jumpElapsedTime = -1;
        player.setZIndex(centerCircle.getZIndex() + 1);
        happyEnemy = new Image(assetManager.get("happy_enemy.png", Texture.class));
        happyEnemy.setBounds(0, 0, playerRadius * 2f * 1.1085f, playerRadius * 2f * 1.0542f);
        happyEnemy.setOrigin(happyEnemy.getWidth() / 2, 0.4742f * happyEnemy.getHeight());
        happyEnemy.setPosition(enemyThatKilledPlayer.getX() + playerRadius - happyEnemy.getWidth() / 2, enemyThatKilledPlayer.getY() + playerRadius - happyEnemy.getHeight() * 0.4742f);
        happyEnemy.setRotation(enemyThatKilledPlayer.getRotation());
        happyEnemy.getColor().a = 0;
        happyEnemy.setZIndex(player.getZIndex() + 1);
        enemyThatKilledPlayer.setZIndex(happyEnemy.getZIndex() + 1);
        stage.addActor(happyEnemy);
        isGameOver = true;
        shouldBeSpawningEnemies = false;
        if (!prefs.getBoolean("IS_MUTED")) {
            dieSound.play();
        }
        enemyThatKilledPlayer.addAction(Actions.fadeOut(0.5f));
        happyEnemy.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.rotateTo(0, 1)));
        if (!IS_TAKING_SECOND_SCREENSHOT) {
            spinPlayerAction = Actions.repeat(-1, Actions.rotateBy(180, 1));
            player.addAction(spinPlayerAction);
            movePlayerToCenterAction = Actions.moveTo(width / 2 - playerRadius, height / 2 - playerRadius, 2);
            player.addAction(movePlayerToCenterAction);
        }
        inGameScoreLabel.addAction(Actions.fadeOut(0.5f));
        gameOverLabel.setText(GAME_OVER_PHRASES[(int) (Math.random() * GAME_OVER_PHRASES.length)]);
        gameOverLabel.addAction(Actions.fadeIn(0.5f));
        gameOverScoreLabel.setText("SCORE: " + score);
        gameOverScoreLabel.addAction(Actions.fadeIn(0.5f));
        gameOverHighScoreLabel.setText("BEST: " + prefs.getInteger("HIGHSCORE"));
        gameOverHighScoreLabel.addAction(Actions.fadeIn(0.5f));
        playAgainButton.addAction(Actions.fadeIn(0.5f));
        leaderboardButton.addAction(Actions.fadeIn(0.5f));
        muteButton.addAction(Actions.fadeIn(0.5f));
        shareButton.addAction(Actions.fadeIn(0.5f));
        rateButton.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                isTransitioningToMenu = false;
            }
        })));
    }

    private void updateHighScore() {
        if (score > prefs.getInteger("HIGHSCORE")) {
            prefs.putInteger("HIGHSCORE", score);
            prefs.flush();
        }
        bus.post(new UpdateGooglePlayGamesEvent(score));
    }

    private void jump() {
        if (!isGameOver) {
            if (!isGameStarted && !isTransitioningToSleeping) {
                setGameStarted();
            }
            if (jumpElapsedTime == -1) {
                jumpElapsedTime = 0;
                jumpStartAngle = getPlayerAngleFromCenter();
                if (!prefs.getBoolean("IS_MUTED")) {
                    int selectedSoundIndex = (int) (Math.random() * jumpSounds.size());
                    jumpSounds.get(selectedSoundIndex).play();
                }
            }
        }

    }

    @Subscribe
    public void successfullyShowedAd(SuccessfullyShowedAdEvent event) {
        timeLastAdDisplayed = System.currentTimeMillis();
        gamesSinceLastAd = 0;
    }

    @Subscribe
    public void enableLeaderboard(EnableLeaderboardEvent event) {
        isLeaderboardEnabled = true;
        if (leaderboardButton != null) {
            leaderboardButton.setIconColor(colorOffBlack);
        }
    }

    private void muteOrUnmute() {
        boolean isMuted = prefs.getBoolean("IS_MUTED");
        isMuted = !isMuted;
        muteButton.setIcon(isMuted ? assetManager.get("fa-volume-off.png", Texture.class) : assetManager.get("fa-volume-up.png", Texture.class));
        muteButton.setIconSizeAndRightOffset(isMuted ? buttonRadius * 0.7f : buttonRadius, isMuted ? -buttonRadius / 32 : 0);
        prefs.putBoolean("IS_MUTED", isMuted);
        prefs.flush();
    }

    private class JumpListener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            jump();
            return false;
        }
    }
}
