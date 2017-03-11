package com.qeue.ahhh_round.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qeue.ahhh_round.AhhhRound;
import com.qeue.ahhh_round.R;
import com.qeue.ahhh_round.stores.CharacterSkinStore;
import com.qeue.ahhh_round.utils.AssetSizeUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectSkinListRow extends LinearLayout {
    private CharacterSkinStore characterSkinStore;
    private Drawable skinImage;
    private CharacterSkin characterSkin;
    private ImageView skinView;
    private TextView predicateTextView;
    private RoundedButton selectButton;

    public SelectSkinListRow(Context context) {
        this(context, null);
    }

    public SelectSkinListRow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectSkinListRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        skinView = (ImageView) findViewById(R.id.select_skin_text_view_image);
        predicateTextView = (TextView) findViewById(R.id.select_skin_text_view_predicate_text_view);
        predicateTextView.initialize(AssetSizeUtil.outOfGameFontSize(15), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_BLACK);
        selectButton = (RoundedButton) findViewById(R.id.select_skin_text_view_select_button);
        selectButton.initialize(AssetSizeUtil.outOfGameFontSize(15), AhhhRound.DEFAULT_FONT_NAME, Color.OFF_WHITE, Color.OFF_BLACK);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        characterSkinStore.removeSelectedSkinChangeListener("" + hashCode());
    }

    public void setCharacterSkinAndImage(final CharacterSkin characterSkin, Drawable skinImage, final CharacterSkinStore characterSkinStore) {
        this.characterSkin = characterSkin;
        this.skinImage = skinImage;
        this.characterSkinStore = characterSkinStore;
        characterSkinStore.addSelectedSkinChangeListener("" + hashCode(), new Runnable() {
            @Override
            public void run() {
                updateSelectButton();
            }
        });
        skinView.setImageDrawable(skinImage);
        skinView.setAdjustViewBounds(true);
        skinView.setMaxWidth((int) (skinImage.getIntrinsicWidth() / 6.0));
        skinView.setMaxHeight((int) (skinImage.getIntrinsicHeight() / 6.0));
        setPredicateText(characterSkin.unlockPredicateTextForm);
        updateSelectButton();
    }

    private void setPredicateText(List<String> predicateTextSegments) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> predicateIterator = predicateTextSegments.iterator();
        List<Integer> predicateSeparatorPositions = new ArrayList<>();
        int currentPositionInString = 0;
        while (predicateIterator.hasNext()) {
            String predicate = predicateIterator.next();
            sb.append(predicate);
            currentPositionInString += predicate.length();
            if (predicateIterator.hasNext()) {
                predicateSeparatorPositions.add(currentPositionInString + 1);
                sb.append("\nOR\n");
            }
        }
        SpannableString multiSizedString = new SpannableString(sb);
        for (Integer predicateSeparatorPosition : predicateSeparatorPositions) {
            multiSizedString.setSpan(new RelativeSizeSpan(0.66f), predicateSeparatorPosition, predicateSeparatorPosition + 2, 0);
        }
        predicateTextView.setText(multiSizedString);
    }

    private void updateSelectButton() {
        if (characterSkin != null) {
            if (characterSkin.imageName.equals(characterSkinStore.getSelectedCharacterSkin().imageName)) {
                selectButton.setText("Selected");
                selectButton.setTextColor(Color.OFF_WHITE);
                selectButton.setBackgroundColor(Color.OFF_BLACK);
            } else if (characterSkin.isUnlocked()) {
                selectButton.setText("Select");
                selectButton.setTextColor(Color.OFF_BLACK);
                selectButton.setBackgroundColor(Color.OFF_WHITE);
            } else {
                selectButton.setText("Locked");
                selectButton.setTextColor(Color.GRAY);
                selectButton.setBackgroundColor(Color.OFF_WHITE);
            }
        }
    }
}
