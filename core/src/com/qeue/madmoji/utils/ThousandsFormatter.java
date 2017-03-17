package com.qeue.madmoji.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class ThousandsFormatter {
    public static String format(int score) {
        return NumberFormat.getInstance(Locale.US).format(score);
    }
}
