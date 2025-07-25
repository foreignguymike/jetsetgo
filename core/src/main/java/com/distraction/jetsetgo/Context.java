package com.distraction.jetsetgo;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.distraction.jetsetgo.gj.GameJoltClient;
import com.distraction.jetsetgo.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

public class Context {

    public static final String MAP = "map.tmx";
    public static final String VCR20 = "fonts/vcr20.fnt";
    public static final String M5X716 = "fonts/m5x716.fnt";
    private static final String ATLAS = "jsg.atlas";

    public static final int MAX_SCORES = 20;
    private static final int MINIMUM_SCORE = 3000;

    public AssetManager assets;

    public ScreenManager sm;
    public SpriteBatch sb;

    public GameJoltClient client;
    public boolean leaderboardsRequesting;
    public boolean leaderboardsInitialized;
    public List<ILeaderBoardEntry> entries = new ArrayList<>();

    public PlayerData data = new PlayerData();

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
//        sm = new ScreenManager(new com.distraction.jetsetgo.screens.PerkScreen(this));
        sm = new ScreenManager(new com.distraction.jetsetgo.screens.NameScreen(this));
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

    public void fetchLeaderboard(SuccessCallback callback) {
        if (Constants.LEADERBOARD_ID == 0) {
            callback.callback(false);
            return;
        }
        entries.clear();
        if (leaderboardsRequesting) return;
        leaderboardsRequesting = true;
        client.fetchLeaderboardEntries("", MAX_SCORES, false, leaderBoard -> {
            if (leaderBoard != null) {
                leaderboardsRequesting = false;
                leaderboardsInitialized = true;
                entries.clear();
                for (int i = 0; i < leaderBoard.size; i++) {
                    entries.add(leaderBoard.get(i));
                }
            }
            callback.callback(leaderBoard != null);
        });
    }

    public void submitScore(String name, int score, Net.HttpResponseListener listener) {
        client.setGuestName(name);
        client.submitToLeaderboard("", score, null, 10000, listener);
    }

    public boolean isHighscore(String name, int score) {
        if (!leaderboardsInitialized) return false;
        if (score < MINIMUM_SCORE) return false;
        ILeaderBoardEntry existingEntry = null;
        for (ILeaderBoardEntry entry : entries) {
            if (entry.getUserDisplayName().equalsIgnoreCase(name)) {
                existingEntry = entry;
                break;
            }
        }
        boolean top = entries.size() < MAX_SCORES || score > Integer.parseInt(Utils.getLast(entries).getFormattedValue());
        if (existingEntry != null) {
            return score > Integer.parseInt(existingEntry.getFormattedValue()) && top;
        } else {
            return top;
        }
    }

    public void dispose() {
        sb.dispose();
    }

}
