package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.entity.Particle;
import com.distraction.jetsetgo.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayScreen extends Screen {

    private final Player player;

    private final List<Particle> particles;

    public PlayScreen(Context context) {
        super(context);

        particles = new ArrayList<>();

        player = new Player(context, particles);
        player.x = 100;
        player.y = 100;
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
        sb.setProjectionMatrix(cam.combined);
        for (Particle p : particles) p.render(sb);
        player.render(sb);
        sb.end();
    }

}
