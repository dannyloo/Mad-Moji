package com.qeue.ahhh_round.components;

public class Color extends com.badlogic.gdx.graphics.Color {
    public static final Color OFF_WHITE = new Color("eaf2e3");
    public static final Color OFF_BLACK = new Color("595758");
    public static final Color GRAY = new Color("95998e");

    public final String hex;
    public final int argb;

    public Color(String hex) {
        super(com.badlogic.gdx.graphics.Color.valueOf(hex));
        this.hex = hex;
        argb = com.badlogic.gdx.graphics.Color.argb8888(this);
    }
}
