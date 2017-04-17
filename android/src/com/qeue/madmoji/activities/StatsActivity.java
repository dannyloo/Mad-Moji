package com.qeue.madmoji.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.badlogic.gdx.Gdx;
import com.qeue.madmoji.MainApplication;
import com.qeue.madmoji.AhhhRound;
import com.qeue.madmoji.activities.helpers.GooglePlayGamesHelper;
import com.qeue.madmoji.components.Color;
import com.qeue.madmoji.components.RoundedButton;
import com.qeue.madmoji.components.TextView;
import com.qeue.madmoji.stores.CharacterSkinStore;
import com.qeue.madmoji.stores.GameActivityStore;
import com.qeue.madmoji.utils.AssetSizeUtil;
import com.qeue.madmoji.utils.ThousandsFormatter;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends Activity {
    private List<TextView> textViews = new ArrayList<>();
    private TextView highScoreValueTextView;
    private TextView totalPlaysValueTextView;
    private TextView totalJumpsValueTextView;
    private TextView totalDaysPlayedTextView;
    private RoundedButton leaderboardsButton;
    private RoundedButton doneButton;
    private GameActivityStore gameActivityStore;
    private CharacterSkinStore characterSkinStore;
    private GooglePlayGamesHelper googlePlayGamesHelper;
    private Bus bus;
    private double width;
    private double height;
    private double pointFont;
    private double statFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameActivityStore = ((MainApplication) getApplication()).getGameActivityStore();
        characterSkinStore = ((MainApplication) getApplication()).getCharacterSkinStore();
        bus = ((MainApplication) getApplication()).getBus();
        googlePlayGamesHelper = new GooglePlayGamesHelper(this, bus, gameActivityStore, characterSkinStore);
        setContentView(com.qeue.madmoji.R.layout.stats_activity);
        View root = findViewById(android.R.id.content);
        root.setBackgroundColor(Color.OFF_WHITE.argb);

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        pointFont = height/68;
        statFont = height/53;
        System.out.println("height is " + height);
        System.out.println("point is " + pointFont);
        System.out.println("stat is " + statFont);

        setupHeader();
        setupLabels();
        setupButtons();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googlePlayGamesHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void setupHeader() {
        TextView header = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_header);
        header.initialize(AssetSizeUtil.outOfGameFontSize(48), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK);
    }

    public void setupLabels() {

        highScoreValueTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_high_score_value);
        textViews.add(highScoreValueTextView);
        totalPlaysValueTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_plays_value);
        textViews.add(totalPlaysValueTextView);
        totalJumpsValueTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_jumps_value);
        textViews.add(totalJumpsValueTextView);
        totalDaysPlayedTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_days_value);
        textViews.add(totalDaysPlayedTextView);

        textViews.add((TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_high_score_label));
        textViews.add((TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_jumps_label));
        textViews.add((TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_plays_label));
        textViews.add((TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_days_label));

        for (TextView tv : textViews) {
            tv.initialize(AssetSizeUtil.outOfGameFontSize(20), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK);
        }

        highScoreValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(32));
        highScoreValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getHighScore()));

        totalPlaysValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(32));
        totalPlaysValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getTotalPlays()));

        totalJumpsValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(32));
        totalJumpsValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getTotalJumps()));

        totalDaysPlayedTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(32));
        totalDaysPlayedTextView.setText(ThousandsFormatter.format(gameActivityStore.getDaysPlayedInARow()));
    }

    public void setupButtons() {
        doneButton = (RoundedButton) findViewById(com.qeue.madmoji.R.id.stats_activity_done_button);
        doneButton.initialize(AssetSizeUtil.outOfGameFontSize(19), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK, Color.OFF_WHITE);
        doneButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return false;
            }
        });
        leaderboardsButton = (RoundedButton) findViewById(com.qeue.madmoji.R.id.stats_activity_leaderboards_button);
        leaderboardsButton.initialize(AssetSizeUtil.outOfGameFontSize(19), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK, Color.OFF_WHITE);
        leaderboardsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                googlePlayGamesHelper.attemptToShowLeaderboard();
                return false;
            }
        });
    }

    public void signInToLeaderboards(){
        googlePlayGamesHelper.attemptToShowLeaderboard();
    }
}
