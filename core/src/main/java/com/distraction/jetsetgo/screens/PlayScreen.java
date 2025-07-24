package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.entity.Collectable;
import com.distraction.jetsetgo.entity.Particle;
import com.distraction.jetsetgo.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayScreen extends Screen {

    private final Player player;

    private final List<Particle> particles;
    private final List<Collectable> collectables;

    private int score;

    private BitmapFont font = new BitmapFont();

    public PlayScreen(Context context) {
        super(context);

        particles = new ArrayList<>();
        collectables = new ArrayList<>();

        player = new Player(context, particles);
        player.x = 100;
        player.y = 100;

        // test collectables
        collectables.add(new Collectable(context, Collectable.Type.WATERMELON, 300, 300));
        collectables.add(new Collectable(context, Collectable.Type.WATERMELON, 340, 340));
        collectables.add(new Collectable(context, Collectable.Type.WATERMELON, 380, 380));
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
        cam.position.set(player.x, player.y, 0);
        cam.update();

        player.update(dt);

        for (int i = 0; i < collectables.size(); i++) {
            Collectable c = collectables.get(i);
            if (player.intersects(c)) {
                score += c.getPoints();
                c.remove = true;
            }
            if (c.remove) collectables.remove(i--);
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
        font.draw(sb, "score: " + score, 10, Constants.HEIGHT - 10);
        font.draw(sb, (int) player.x + ", " + (int) player.y, 10, Constants.HEIGHT - 25);
        sb.setProjectionMatrix(cam.combined);
        for (Particle p : particles) p.render(sb);
        player.render(sb);
        for (Collectable c : collectables) c.render(sb);
        sb.end();
    }

}
