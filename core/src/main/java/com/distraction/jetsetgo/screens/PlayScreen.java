package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Passive;
import com.distraction.jetsetgo.Perk;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.Collectible;
import com.distraction.jetsetgo.entity.Particle;
import com.distraction.jetsetgo.entity.Player;
import com.distraction.jetsetgo.entity.TextEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayScreen extends Screen {

    private final Player player;

    private final List<Particle> particles;
    private final List<Collectible> collectibles;
    private final List<Collectible> magnetCollectibles;

    private final int mapWidth = 2000;
    private final int mapHeight = 2000;

    private float timer = 30;
    private int timerInt = MathUtils.ceil(timer);

    private int score;

    private final Button[] perkIcons;
    private final TextEntity scoreText;
    private final TextEntity timeText;

    private int combo;
    private float comboTimer;
    private float comboTimerMax;
    private TextEntity comboText;

    private List<Perk> passives;

    public PlayScreen(Context context) {
        super(context);

        ignoreInput = true;
        in = new Transition(context, Transition.Type.CHECKERED_IN, 0.5f, () -> ignoreInput = false);
        in.start();
        out = new Transition(context, Transition.Type.CHECKERED_OUT, 0.5f, () -> context.sm.replace(new PlayScreen(context)));

        perkIcons = new Button[3];
        perkIcons[0] = new Button(context.getImage(context.ability.getName()), 30, Constants.HEIGHT - 30);
        perkIcons[1] = new Button(context.getImage(context.passive1.getName()), 80, Constants.HEIGHT - 30);
        perkIcons[2] = new Button(context.getImage(context.passive2.getName()), 130, Constants.HEIGHT - 30);

        scoreText = new TextEntity(context.getFont(Context.VCR20), "0", Constants.WIDTH - 20, Constants.HEIGHT - 20, TextEntity.Alignment.RIGHT);
        scoreText.setColor(Constants.WHITE);
        timeText = new TextEntity(context.getFont(Context.VCR20, 2), timerInt + "", Constants.WIDTH / 2f, Constants.HEIGHT - 20, TextEntity.Alignment.CENTER);
        timeText.setColor(Constants.WHITE);

        comboText = new TextEntity(context.getFont(Context.M5X716, 2), score + "", Constants.WIDTH / 2f, Constants.HEIGHT / 2f + 30, TextEntity.Alignment.CENTER);
        comboText.setColor(Constants.WHITE);
        comboTimerMax = 1;

        particles = new ArrayList<>();
        collectibles = new ArrayList<>();
        magnetCollectibles = new ArrayList<>();

        player = new Player(context, particles);
        player.x = mapWidth / 2f;
        player.y = mapHeight / 2f;

        // check perks
        passives = new ArrayList<>();
        passives.add(context.passive1);
        passives.add(context.passive2);
        if (passives.contains(Passive.SPEEDO_MODE)) player.maxSpeedMulti = 1.5f;
        if (passives.contains(Passive.SURF_STEERING)) player.steerSpeedMulti = 3;
        if (passives.contains(Passive.SUMMER_HOURS)) timer = 37;
        if (passives.contains(Passive.CHAIN_REACTION)) comboTimerMax = 3;

        // parse map
        TiledMap map = context.getMap();
        Map<String, Collectible.Type> typeMapping = new HashMap<>();
        typeMapping.put("watermelon", Collectible.Type.WATERMELON);
        typeMapping.put("beachball", Collectible.Type.BEACH_BALL);
        typeMapping.put("sunglasses", Collectible.Type.SUNGLASSES);
        for (Map.Entry<String, Collectible.Type> entry : typeMapping.entrySet()) {
            MapLayer layer = map.getLayers().get(entry.getKey());
            for (MapObject o : layer.getObjects()) {
                MapProperties props = o.getProperties();
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                collectibles.add(new Collectible(context, entry.getValue(), x, y));
            }
        }
        System.out.println("total items loaded: " + collectibles.size());
    }

    private void collect(Collectible c) {
        score += MathUtils.ceil(c.getPoints() * (1 + combo * 0.05f));
        scoreText.setText(score + "");
        c.remove = true;

        if (combo < 10) {
            combo += 1;
            comboText.setText(combo + "x");
        }
        comboTimer = comboTimerMax;
    }

    @Override
    public void input() {
        if (ignoreInput) return;
        player.up = Gdx.input.isKeyPressed(Input.Keys.UP);
        player.down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        player.left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        player.right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    @Override
    public void update(float dt) {
        in.update(dt);
        out.update(dt);
        timer -= dt;
        int newTimerInt = MathUtils.ceil(timer);
        if (timerInt != newTimerInt) { // avoid unnecessary glyph relayout
            timerInt = newTimerInt;
            timeText.setText(timerInt + "");
        }

        if (timer < 0) {
            context.sm.push(new FinishScreen(context));
            return;
        }

        comboTimer -= dt;
        if (comboTimer < 0) {
            combo = 0;
            comboText.setText(combo + "x");
        }

        if (player.x > mapWidth) player.x -= mapWidth;
        if (player.x < 0) player.x += mapWidth;
        if (player.y > mapHeight) player.y -= mapHeight;
        if (player.y < 0) player.y += mapHeight;
        cam.position.x = MathUtils.clamp(player.x, cam.viewportWidth / 2f, mapWidth - cam.viewportWidth / 2f);
        cam.position.y = MathUtils.clamp(player.y, cam.viewportHeight / 2f, mapHeight - cam.viewportHeight / 2f);
        cam.update();

        player.update(dt);

        for (int i = 0; i < collectibles.size(); i++) {
            Collectible c = collectibles.get(i);
            if (passives.contains(Passive.MAIN_ATTRACTION) && c.contains(player.x, player.y, 40, 40)) {
                c.setPlayer(player);
                magnetCollectibles.add(c);
            }
            if (player.intersects(c)) {
                collect(c);
            }
            if (c.remove) collectibles.remove(i--);
        }

        for (int i = 0; i < magnetCollectibles.size(); i++) {
            Collectible c = magnetCollectibles.get(i);
            c.update(dt);
            if (c.remove) magnetCollectibles.remove(i--);
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            p.update(dt);
            if (p.remove) particles.remove(i--);
        }
    }

    @Override
    public void render() {
        Utils.clearScreen(Constants.BLUE);
        sb.begin();

        sb.setProjectionMatrix(cam.combined);
        sb.setColor(1, 1, 1, 1);
        for (Particle p : particles) p.render(sb);
        player.render(sb);
        for (Collectible c : collectibles) c.render(sb);

        sb.setProjectionMatrix(uiCam.combined);
        for (Button b : perkIcons) b.render(sb);
        timeText.render(sb);
        scoreText.render(sb);
        comboText.render(sb);
        // combo bar
        if (comboTimer > 0) {
            sb.setColor(Constants.DARK_GREEN);
            sb.draw(pixel, Constants.WIDTH / 2f - 25, Constants.HEIGHT / 2f + 20, 50, 2);
            sb.setColor(Constants.GREEN);
            sb.draw(pixel, Constants.WIDTH / 2f - 25, Constants.HEIGHT / 2f + 20, 50 * comboTimer / comboTimerMax, 2);
        }

        in.render(sb);
        out.render(sb);

        sb.end();
    }

}
