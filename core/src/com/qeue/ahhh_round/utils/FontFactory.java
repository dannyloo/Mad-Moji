package com.qeue.ahhh_round.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.qeue.ahhh_round.components.Font;

import java.util.HashMap;

public class FontFactory {
    private HashMap<String, String> labelToFullFontMap = new HashMap<String, String>();
    private HashMap<String, Double> labelToFontSizeMap = new HashMap<String, Double>();
    private AssetManager assetManager;

    public FontFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void loadFont(String label, String name, double size) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParams.fontFileName = name;
        fontParams.fontParameters.size = (int) size;
        assetManager.load(name + size, BitmapFont.class, fontParams);
        labelToFullFontMap.put(label, name + size);
        labelToFontSizeMap.put(label, size);
    }

    public Font get(String name) {
        return new Font(assetManager.get(labelToFullFontMap.get(name), BitmapFont.class), labelToFontSizeMap.get(name));
    }
}
