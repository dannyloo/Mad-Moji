package com.gamestridestudios.ahhh_round.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {
    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(double fontSize, String fontName, Color textColor) {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), fontName);
        setTypeface(font);
        setTextSize((float) fontSize);
        setTextColor(textColor.argb);
    }
}
