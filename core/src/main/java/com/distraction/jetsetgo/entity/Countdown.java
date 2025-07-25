package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.jetsetgo.Utils;

public class Countdown extends Entity {

    private final TextureRegion image;

    private boolean started;
    private float timer;
    private float scale = 10;

    public Countdown(TextureRegion image, float x, float y) {
        this.image = image;
        this.x = x;
        this.y = y;
        a = 0;
    }

    public void start() {
        started = true;
    }

    @Override
    public void update(float dt) {
        if (!started) return;
        timer += dt;

        // 300ms scale down, alpha in
        // 500ms alpha out
        scale = 10 - 9 * timer / 0.3f;
        if (timer < 0.3f) a = timer / 0.3f;
        else a = 1 - (timer - 0.3f) / 0.5f;

        scale = MathUtils.clamp(scale, 1, 10);
        a = MathUtils.clamp(a, 0,  1);

        if (timer > 0.8f) started = false;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!started) return;
        sb.setColor(1, 1, 1, a);
        Utils.drawCentered(sb, image, x, y, scale);
    }
}
