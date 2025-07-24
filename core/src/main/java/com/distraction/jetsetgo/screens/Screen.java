package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;

public abstract class Screen {

    protected Context context;

    protected final TextureRegion pixel;

    public boolean transparent = false;

    protected OrthographicCamera cam;
    protected OrthographicCamera uiCam;
    protected final Vector3 m;

    protected SpriteBatch sb;

    protected boolean ignoreInput;

    public Transition in = null;
    public Transition out = null;

    protected Screen(Context context) {
        this.context = context;
        this.sb = context.sb;

        pixel = context.getPixel();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT);

        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT);

        m = new Vector3();
    }

    protected void setPanTransition(Vector2 start, Vector2 end, OrthographicCamera cam) {
        ignoreInput = true;
        in = new Transition(
            context,
            Transition.Type.PAN, cam,
            start,
            end,
            0.2f,
            () -> ignoreInput = false
        );
        in.start();
        out = new Transition(
            context,
            Transition.Type.PAN, cam,
            end,
            start,
            0.2f,
            () -> {
                context.sm.pop();
                context.sm.peek().ignoreInput = false;
            }
        );
        cam.position.x = start.x;
        cam.position.y = start.y;
        cam.update();
    }

    protected void unproject() {
        m.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        uiCam.unproject(m);
    }

    public void resume() {}

    public abstract void input();

    public abstract void update(float dt);

    public abstract void render();

}
