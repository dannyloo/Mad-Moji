package com.qeue.madmoji.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qeue.madmoji.MainApplication;
import com.qeue.madmoji.AhhhRound;
import com.qeue.madmoji.components.RoundedButton;
import com.qeue.madmoji.components.SelectSkinListRow;
import com.qeue.madmoji.components.TextView;
import com.qeue.madmoji.components.CharacterSkin;
import com.qeue.madmoji.stores.CharacterSkinStore;
import com.qeue.madmoji.stores.GameActivityStore;
import com.qeue.madmoji.utils.AssetSizeUtil;
import com.qeue.madmoji.utils.ThousandsFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectSkinActivity extends Activity {
    private ListView skinListView;
    private List<CharacterSkin> skins;
    private CharacterSkinStore characterSkinStore;
    private GameActivityStore gameActivityStore;
    private List<Drawable> skinDrawableCache = new ArrayList<>();
    private RoundedButton doneButton;

    private List<TextView> textViews = new ArrayList<>();
    private TextView highScoreValueTextView;
    private TextView totalPlaysValueTextView;
    private TextView totalJumpsValueTextView;
    private TextView totalDaysPlayedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.qeue.madmoji.R.layout.select_skin_activity);
        View root = findViewById(android.R.id.content);
        root.setBackgroundColor(com.qeue.madmoji.components.Color.OFF_WHITE.argb);
        characterSkinStore = ((MainApplication) getApplication()).getCharacterSkinStore();
        gameActivityStore = ((MainApplication) getApplication()).getGameActivityStore();
        gameActivityStore.setHasUnseenLockedCharacterSkins(false);
        setupCharacterSkins();
        setupHeader();
        setupButton();
        setupListView();
    }

    private void setupCharacterSkins() {
        skins = characterSkinStore.getAllSkins();
        for (CharacterSkin skin : skins) {
            try {
                Drawable drawable = Drawable.createFromResourceStream(getResources(), new TypedValue(), getResources().getAssets().open(skin.imageName), null);
                skinDrawableCache.add(drawable);
            } catch (IOException e) {
                skinDrawableCache.add(new ShapeDrawable());
            }
        }
    }

    private void setupHeader() {
        TextView header = (TextView) findViewById(com.qeue.madmoji.R.id.select_skin_text_view);
        header.initialize(AssetSizeUtil.outOfGameFontSize(26), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header1 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_jumps_label);
        header1.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header2 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_plays_label);
        header2.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header3 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_high_score_label);
        header3.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header4 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_days_label);
        header4.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header5 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_jumps_value);
        header5.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header6 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_plays_value);
        header6.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header7 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_high_score_value);
        header7.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);

        TextView header8 = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_days_value);
        header8.initialize(AssetSizeUtil.outOfGameFontSize(14), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK);


        highScoreValueTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_high_score_value);
        textViews.add(highScoreValueTextView);
        totalPlaysValueTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_plays_value);
        textViews.add(totalPlaysValueTextView);
        totalJumpsValueTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_jumps_value);
        textViews.add(totalJumpsValueTextView);
        totalDaysPlayedTextView = (TextView) findViewById(com.qeue.madmoji.R.id.stats_activity_total_days_value);
        textViews.add(totalDaysPlayedTextView);

        highScoreValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(14));
        highScoreValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getHighScore()));

        totalPlaysValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(14));
        totalPlaysValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getTotalPlays()));

        totalJumpsValueTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(14));
        totalJumpsValueTextView.setText(ThousandsFormatter.format(gameActivityStore.getTotalJumps()));

        totalDaysPlayedTextView.setTextSize(AssetSizeUtil.outOfGameFontSize(14));
        totalDaysPlayedTextView.setText(ThousandsFormatter.format(gameActivityStore.getDaysPlayedInARow()));
    }

    private void setupButton() {
        doneButton = (RoundedButton) findViewById(com.qeue.madmoji.R.id.skin_activity_done_button);
        doneButton.initialize(AssetSizeUtil.outOfGameFontSize(19), AhhhRound.DEFAULT_FONT_NAME, com.qeue.madmoji.components.Color.OFF_BLACK, com.qeue.madmoji.components.Color.OFF_WHITE);
        doneButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return false;
            }
        });
    }

    private void setupListView() {
        skinListView = (ListView) findViewById(com.qeue.madmoji.R.id.skin_list_view);
        skinListView.setAdapter(new SkinListAdapter(this, 0, skins));
        skinListView.setDividerHeight(0);
        skinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!skins.get(i).imageName.equals(characterSkinStore.getSelectedCharacterSkin().imageName)
                        && skins.get(i).isUnlocked()) {
                    characterSkinStore.setSelectedCharacterSkin(skins.get(i));
                }
            }
        });
        doneButton.measure(0, 0);
    }

    private class SkinListAdapter extends ArrayAdapter {
        public SkinListAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SelectSkinListRow row = (SelectSkinListRow) convertView;
            if (row == null) {
                row = (SelectSkinListRow) LayoutInflater.from(SelectSkinActivity.this).inflate(com.qeue.madmoji.R.layout.select_skin_list_row, parent, false);
            }
            row.setCharacterSkinAndImage(skins.get(position), skinDrawableCache.get(position), characterSkinStore);

            if (position == getCount() - 1){
                float scale = getResources().getDisplayMetrics().density;
                int bottomPadding = (int) (doneButton.getMeasuredHeight() + scale * 16);
                row.setPadding(0, 0, 0, bottomPadding);
            } else {
                row.setPadding(0, 0, 0, 0);
            }

            return row;
        }
    }
}
