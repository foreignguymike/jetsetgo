package com.distraction.jetsetgo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.screens.PerkScreen;
import com.distraction.jetsetgo.screens.PlayScreen;
import com.distraction.jetsetgo.screens.ScreenManager;

public class Context {

    public static final String VCR20 = "fonts/vcr20.fnt";
    public static final String M5X716 = "fonts/m5x716.fnt";
    private static final String ATLAS = "jsg.atlas";

    public AssetManager assets;

    public ScreenManager sm;
    public SpriteBatch sb;

    public Context() {
        assets = new AssetManager();
        assets.load(VCR20, BitmapFont.class);
        assets.load(M5X716, BitmapFont.class);
        assets.load(ATLAS, TextureAtlas.class);
        assets.finishLoading();

        sb = new SpriteBatch();
//        sm = new ScreenManager(new PlayScreen(this));
        sm = new ScreenManager(new PerkScreen(this));
    }

    public TextureRegion getImage(String key) {
        TextureRegion region = assets.get(ATLAS, TextureAtlas.class).findRegion(key);
        if (region == null) throw new IllegalArgumentException("image " + key + " not found");
        return region;
    }

    public TextureRegion getPixel() {
        return getImage("pixel");
    }

    public BitmapFont getFont(String name) {
        return getFont(name, 1f);
    }

    public BitmapFont getFont(String name, float scale) {
        BitmapFont originalFont = assets.get(name, BitmapFont.class);
        BitmapFont scaledFont = new BitmapFont(originalFont.getData().getFontFile(), originalFont.getRegion(), false);
        scaledFont.getData().setScale(scale);
        return scaledFont;
    }

    public void dispose() {
        sb.dispose();
    }

}
