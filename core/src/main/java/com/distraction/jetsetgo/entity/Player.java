package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.jetsetgo.Animation;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Utils;

import java.util.List;

public class Player extends Entity {

    private static final float STEER_SPEED = 1;
    private static final float MAX_SPEED = 200;
    private static final float ACCEL = 200;
    private static final float FRICTION = 10;
    private static final float REVERSE_MULTIPLIER = 0.3f;

    private final Context context;
    private final List<Particle> particles;

    private final TextureRegion image;
    private final TextureRegion pixel;

    public float maxSpeedMulti = 1f;
    public float steerSpeedMulti = 1f;

    private float speed;

    // inputs
    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;

    private float waveTimer;

    public Player(Context context, List<Particle> particles) {
        this.context = context;
        this.particles = particles;

        pixel = context.getPixel();

        image = context.getImage("player");
        w = image.getRegionWidth();
        h = image.getRegionHeight();
    }

    @Override
    public void update(float dt) {
        if (left) rad += STEER_SPEED * steerSpeedMulti * dt;
        if (right) rad -= STEER_SPEED * steerSpeedMulti * dt;

        if (up) {
            speed += ACCEL * dt;
        } else if (down) {
            speed -= ACCEL * REVERSE_MULTIPLIER * dt;
        } else {
            speed -= FRICTION * dt;
        }

        speed = MathUtils.clamp(speed, 0, MAX_SPEED * maxSpeedMulti);

        rad = (rad + MathUtils.PI) % MathUtils.PI2;
        if (rad < 0) rad += MathUtils.PI2;
        rad -= MathUtils.PI;

        dx = MathUtils.cos(rad) * speed;
        dy = MathUtils.sin(rad) * speed;

        x += dx * dt;
        y += dy * dt;

        // add wave particles
        waveTimer -= dt;
        if (speed > 20 && waveTimer < 0) {
            Particle p = new Particle(new Animation(context.getImage("wave").split(4, 34)[0], 0.1f + MathUtils.random() * 0.15f));
            p.singleAnimate = true;
            p.x = x;
            p.y = y;
            p.rad = rad - MathUtils.PI;
            particles.add(p);
            waveTimer = 0.04f - 0.035f * speed / (MAX_SPEED * maxSpeedMulti);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Utils.drawRotated(sb, image, x, y, rad);
    }
}
