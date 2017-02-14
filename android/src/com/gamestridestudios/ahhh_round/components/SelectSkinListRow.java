package com.gamestridestudios.ahhh_round.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gamestridestudios.ahhh_round.AhhhRound;
import com.gamestridestudios.ahhh_round.R;
import com.gamestridestudios.ahhh_round.stores.CharacterSkinStore;
import com.gamestridestudios.ahhh_round.utils.AssetSizeUtil;

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
        skinView.setMaxWidth((int) (skinImage.getIntrinsicWidth() / 7.0));
        skinView.setMaxHeight((int) (skinImage.getIntrinsicHeight() / 7.0));
        predicateTextView.setText(characterSkin.unlockPredicateTextForm.get(0));
        updateSelectButton();
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
