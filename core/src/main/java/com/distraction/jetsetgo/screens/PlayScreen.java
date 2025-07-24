package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.Collectible;
import com.distraction.jetsetgo.entity.Particle;
import com.distraction.jetsetgo.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayScreen extends Screen {

    private final Player player;

    private final List<Particle> particles;
    private final List<Collectible> collectibles;

    private final int mapWidth = 2000;
    private final int mapHeight = 2000;

    private float timer = 30;

    private int score;

    private final BitmapFont font;
    private final BitmapFont bigFont;

    public PlayScreen(Context context) {
        super(context);

        particles = new ArrayList<>();
        collectibles = new ArrayList<>();

        player = new Player(context, particles);
        player.x = mapWidth / 2f;
        player.y = mapHeight / 2f;

        // test collectibles
        collectibles.add(new Collectible(context, Collectible.Type.WATERMELON, 30, 30));
        collectibles.add(new Collectible(context, Collectible.Type.WATERMELON, 30, mapHeight - 30));
        collectibles.add(new Collectible(context, Collectible.Type.WATERMELON, mapWidth, 30));
        collectibles.add(new Collectible(context, Collectible.Type.WATERMELON, mapWidth - 30, mapHeight - 30));

        font = context.getFont(Context.VCR20);
        font.setColor(Constants.WHITE);
        bigFont = context.getFont(Context.VCR20, 2);
        bigFont.setColor(Constants.WHITE);
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
        sb.begin();
        sb.setProjectionMatrix(uiCam.combined);
        bigFont.draw(sb, MathUtils.ceil(timer) + "", Constants.WIDTH / 2f, Constants.HEIGHT - 25);
        font.draw(sb, "score: " + score, 10, Constants.HEIGHT - 10);
        font.draw(sb, (int) player.x + ", " + (int) player.y, 10, Constants.HEIGHT - 25);
        sb.setProjectionMatrix(cam.combined);
        for (Particle p : particles) p.render(sb);
        player.render(sb);
        for (Collectible c : collectibles) c.render(sb);
        sb.end();
    }

}
