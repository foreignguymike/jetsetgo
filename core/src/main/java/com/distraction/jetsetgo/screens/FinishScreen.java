package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.math.Vector2;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;

public class FinishScreen extends Screen {

    private float dim;

    public FinishScreen(Context context) {
        super(context);
        transparent = true;
        setPanTransition(
            new Vector2(Constants.WIDTH / 2f, -Constants.HEIGHT / 2f),
            new Vector2(Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            cam
        );
    }

    @Override
    public void input() {
        if (ignoreInput) return;
    }

    @Override
    public void update(float dt) {
        in.update(dt);
        out.update(dt);

        if (out.started()) {
            dim -= 5 * dt;
            if (dim < 0f) dim = 0f;
        } else if (in.started()) {
            dim += 5 * dt;
            if (dim > 0.8f || in.isFinished()) dim = 0.8f;
        }
    }

    @Override
    public void render() {
        sb.begin();

        sb.setProjectionMatrix(uiCam.combined);
        sb.setColor(0, 0, 0, dim);
        sb.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);

        sb.setProjectionMatrix(cam.combined);
        sb.setColor(1, 1, 1, 1);

        sb.end();
    }
}
