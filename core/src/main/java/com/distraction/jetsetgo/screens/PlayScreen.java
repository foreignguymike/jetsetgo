package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
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

    private final int mapWidth = 2000;
    private final int mapHeight = 2000;

    private float timer = 30;
    private int timerInt = MathUtils.ceil(timer);

    private int score;

    private final Button[] perkIcons;
    private final TextEntity scoreText;
    private final TextEntity timeText;

    public PlayScreen(Context context) {
        super(context);

        perkIcons = new Button[3];
        perkIcons[0] = new Button(context.getImage(context.ability.getName()), 30, Constants.HEIGHT - 30);
        perkIcons[1] = new Button(context.getImage(context.passive1.getName()), 80, Constants.HEIGHT - 30);
        perkIcons[2] = new Button(context.getImage(context.passive2.getName()), 130, Constants.HEIGHT - 30);

        scoreText = new TextEntity(context.getFont(Context.VCR20), "0", Constants.WIDTH - 20, Constants.HEIGHT - 20, TextEntity.Alignment.RIGHT);
        scoreText.setColor(Color.WHITE);
        timeText = new TextEntity(context.getFont(Context.VCR20, 2), timerInt + "", Constants.WIDTH / 2f, Constants.HEIGHT - 20, TextEntity.Alignment.CENTER);
        timeText.setColor(Color.WHITE);

        particles = new ArrayList<>();
        collectibles = new ArrayList<>();

        player = new Player(context, particles);
        player.x = mapWidth / 2f;
        player.y = mapHeight / 2f;

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
            if (player.intersects(c)) {
                score += c.getPoints();
                scoreText.setText(score + "");
                c.remove = true;
            }
            if (c.remove) collectibles.remove(i--);
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
        for (Particle p : particles) p.render(sb);
        player.render(sb);
        for (Collectible c : collectibles) c.render(sb);

        sb.setProjectionMatrix(uiCam.combined);
        for (Button b : perkIcons) b.render(sb);
        timeText.render(sb);
        scoreText.render(sb);

        sb.end();
    }

}
