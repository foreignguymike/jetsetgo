package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Utils;

public class Collectible extends Entity {

    public enum Type {
        WATERMELON("watermelon", 100),
        BEACH_BALL("beachball", 150),
        SUNGLASSES("sunglasses", 200),
        ;

        public String name;
        public int points;
        Type(String name, int points) {
            this.name = name;
            this.points = points;
        }
    }

    private static final float SPEED = 600;

    private final Type type;
    private final TextureRegion image;

    private Player player;

    public Collectible(Context context, Type type, float x, float y) {
        this.type = type;
        this.image = context.getImage(type.name);
        this.x = x;
        this.y = y;
        w = image.getRegionWidth();
        h = image.getRegionHeight();
    }

    public int getPoints() {
        return type.points;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void update(float dt) {
        if (player != null) {
            dx = player.x - x;
            dy = player.y - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            dx *= SPEED / dist;
            dy *= SPEED / dist;
            x += dx * dt;
            y += dy * dt;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Utils.drawCentered(sb, image, x, y);
    }
}
