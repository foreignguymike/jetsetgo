package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.Utils;

public class Button extends Entity {

    private TextureRegion image;

    public Button(TextureRegion image, float x, float y) {
        this.x = x;
        this.y = y;
        setImage(image);
    }

    public void setImage(TextureRegion image) {
        this.image = image;
        w = image.getRegionWidth();
        h = image.getRegionHeight();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, a);
        Utils.drawCentered(sb, image, x, y);
    }
}
