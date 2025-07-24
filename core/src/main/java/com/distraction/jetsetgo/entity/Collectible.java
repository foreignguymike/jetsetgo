package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Utils;

public class Collectible extends Entity {

    public enum Type {
        WATERMELON("watermelon", 100),
        ;

        public String name;
        public int points;
        Type(String name, int points) {
            this.name = name;
            this.points = points;
        }
    }

    private final Type type;
    private final TextureRegion image;

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

    @Override
    public void render(SpriteBatch sb) {
        Utils.drawCentered(sb, image, x, y);
    }
}
