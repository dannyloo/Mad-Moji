package com.qeue.ahhh_round.components;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.Button;

public class RoundedButton extends Button {
    public RoundedButton(Context context) {
        super(context);
    }

    public RoundedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(double fontSize, String fontName, Color buttonColor, Color textColor) {
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius((float) fontSize);
        setBackground(shape);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), fontName);
        setBackgroundColor(buttonColor);
        setTypeface(font);
        setTextSize((float) fontSize);
        setTextColor(textColor);
        int padding = (int) (fontSize * 0.8);
        int sidePadding = (int) (padding * 1.5);
        setPadding(sidePadding, padding, sidePadding, padding);
    }

    public void setTextColor(Color color) {
        setTextColor(color.argb);
    }

    public void setBackgroundColor(Color color) {
        if (getBackground() instanceof GradientDrawable) {
            GradientDrawable shape = (GradientDrawable) getBackground();
            shape.setColor(color.argb);
            setBackground(shape);
        } else {
            setBackgroundColor(color.argb);
        }
    }
}
