package com.distraction.jetsetgo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.distraction.jetsetgo.screens.ScreenManager;

public class Context {

    public static final String MAP = "map.tmx";
    public static final String VCR20 = "fonts/vcr20.fnt";
    public static final String M5X716 = "fonts/m5x716.fnt";
    private static final String ATLAS = "jsg.atlas";

    public AssetManager assets;

    public ScreenManager sm;
    public SpriteBatch sb;

    public Perk ability = null;
    public Perk passive1 = null;
    public Perk passive2 = null;

    public Context() {
        assets = new AssetManager();
        assets.load(VCR20, BitmapFont.class);
        assets.load(M5X716, BitmapFont.class);
        assets.load(ATLAS, TextureAtlas.class);
        assets.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assets.load(MAP, TiledMap.class);
        assets.finishLoading();

        sb = new SpriteBatch();
//        sm = new ScreenManager(new com.distraction.jetsetgo.screens.PlayScreen(this));
        sm = new ScreenManager(new com.distraction.jetsetgo.screens.PerkScreen(this));
    }

    public boolean perksSet() {
        return ability != null && passive1 != null && passive2 != null;
    }

    public TiledMap getMap() {
        return assets.get(MAP);
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
