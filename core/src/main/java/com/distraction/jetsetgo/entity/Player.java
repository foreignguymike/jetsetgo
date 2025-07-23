package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.Context;

public class Player extends Entity {

    public static final float GRAVITY = 500;
    public static final float MAX_GLIDE_TIME = 3f;

    private final Context context;

    private final TextureRegion pixel;

    public Player(Context context) {
        this.context = context;
        pixel = context.getPixel();
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch sb) {
    }
}
