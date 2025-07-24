package com.distraction.jetsetgo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Main extends ApplicationAdapter {

    private static final float TICK = 1f / 60f;

    private Context context;

    private float accum;

    @Override
    public void create() {
        context = new Context();

        // for browser, disable arrow navigation
        Gdx.input.setCatchKey(Input.Keys.UP, true);
        Gdx.input.setCatchKey(Input.Keys.DOWN, true);
        Gdx.input.setCatchKey(Input.Keys.LEFT, true);
        Gdx.input.setCatchKey(Input.Keys.RIGHT, true);
        Gdx.input.setCatchKey(Input.Keys.SPACE, true);
    }

    @Override
    public void render() {
        Utils.clearScreen(Constants.BLUE);
        context.sm.input();
        accum += Gdx.graphics.getDeltaTime();
        while (accum > TICK) {
            accum -= TICK;
            context.sm.update(TICK);
        }
        context.sm.render();
    }

    @Override
    public void dispose() {
    }
}
