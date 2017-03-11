package com.qeue.ahhh_round.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.qeue.ahhh_round.MainApplication;
import com.qeue.ahhh_round.activities.helpers.GooglePlayGamesHelper;
import com.qeue.ahhh_round.components.RoundedButton;
import com.qeue.ahhh_round.components.TextView;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends Activity {
    private List<TextView> textViews = new ArrayList<>();
    private TextView highScoreValueTextView;
    private TextView totalPlaysValueTextView;
    private TextView totalJumpsValueTextView;
    private RoundedButton leaderboardsButton;
    private RoundedButton doneButton;
    private com.qeue.ahhh_round.stores.GameActivityStore gameActivityStore;
    private com.qeue.ahhh_round.stores.CharacterSkinStore characterSkinStore;
    private GooglePlayGamesHelper googlePlayGamesHelper;
    private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameActivityStore = ((MainApplication) getApplication()).getGameActivityStore();
        characterSkinStore = ((MainApplication) getApplication()).getCharacterSkinStore();
        bus = ((MainApplication) getApplication()).getBus();
        googlePlayGamesHelper = new GooglePlayGamesHelper(this, bus, gameActivityStore, characterSkinStore);
        setContentView(com.qeue.ahhh_round.R.layout.stats_activity);
        View root = findViewById(android.R.id.content);
        root.setBackgroundColor(com.qeue.ahhh_round.components.Color.OFF_WHITE.argb);
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
        TextView header = (TextView) findViewById(com.qeue.ahhh_round.R.id.stats_activity_header);
        header.initialize(com.qeue.ahhh_round.utils.AssetSizeUtil.outOfGameFontSize(26), com.qeue.ahhh_round.AhhhRound.DEFAULT_FONT_NAME, com.qeue.ahhh_round.components.Color.OFF_BLACK);
    }

    public void setupLabels() {
        highScoreValueTextView = (TextView) findViewById(com.qeue.ahhh_round.R.id.stats_activity_high_score_value);
        textViews.add(highScoreValueTextView);
        totalPlaysValueTextView = (TextView) findViewById(com.qeue.ahhh_round.R.id.stats_activity_total_plays_value);
        textViews.add(totalPlaysValueTextView);
        totalJumpsValueTextView = (TextView) findViewById(com.qeue.ahhh_round.R.id.stats_activity_total_jumps_value);
        textViews.add(totalJumpsValueTextView);
        textViews.add((TextView) findViewById(com.qeue.ahhh_round.R.id.stats_activity_high_score_label));
        textViews.add((TextView) findViewById(com.qeue.ahhh_round.R.id.stats_activity_total_jumps_label));
        textViews.add((TextView) findViewById(com.qeue.ahhh_round.R.id.stats_activity_total_plays_label));

        for (TextView tv : textViews) {
            tv.initialize(com.qeue.ahhh_round.utils.AssetSizeUtil.outOfGameFontSize(20), com.qeue.ahhh_round.AhhhRound.DEFAULT_FONT_NAME, com.qeue.ahhh_round.components.Color.OFF_BLACK);
        }

        highScoreValueTextView.setTextSize(com.qeue.ahhh_round.utils.AssetSizeUtil.outOfGameFontSize(18));
        highScoreValueTextView.setText(com.qeue.ahhh_round.utils.ThousandsFormatter.format(gameActivityStore.getHighScore()));
        totalPlaysValueTextView.setTextSize(com.qeue.ahhh_round.utils.AssetSizeUtil.outOfGameFontSize(18));
        totalPlaysValueTextView.setText(com.qeue.ahhh_round.utils.ThousandsFormatter.format(gameActivityStore.getTotalPlays()));
        totalJumpsValueTextView.setTextSize(com.qeue.ahhh_round.utils.AssetSizeUtil.outOfGameFontSize(18));
        totalJumpsValueTextView.setText(com.qeue.ahhh_round.utils.ThousandsFormatter.format(gameActivityStore.getTotalJumps()));
    }

    public void setupButtons() {
        doneButton = (RoundedButton) findViewById(com.qeue.ahhh_round.R.id.stats_activity_done_button);
        doneButton.initialize(com.qeue.ahhh_round.utils.AssetSizeUtil.outOfGameFontSize(19), com.qeue.ahhh_round.AhhhRound.DEFAULT_FONT_NAME, com.qeue.ahhh_round.components.Color.OFF_BLACK, com.qeue.ahhh_round.components.Color.OFF_WHITE);
        doneButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return false;
            }
        });
        leaderboardsButton = (RoundedButton) findViewById(com.qeue.ahhh_round.R.id.stats_activity_leaderboards_button);
        leaderboardsButton.initialize(com.qeue.ahhh_round.utils.AssetSizeUtil.outOfGameFontSize(19), com.qeue.ahhh_round.AhhhRound.DEFAULT_FONT_NAME, com.qeue.ahhh_round.components.Color.OFF_BLACK, com.qeue.ahhh_round.components.Color.OFF_WHITE);
        leaderboardsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                googlePlayGamesHelper.attemptToShowLeaderboard();
                return false;
            }
        });
    }
}
