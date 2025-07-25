package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Perk;
import com.distraction.jetsetgo.Utils;

public class PerkSelector extends Entity {

    private final Context context;

    private final TextureRegion pixel;

    public final Perk perk;
    private final TextureRegion image;

    private final TextEntity titleText;
    private final TextEntity descriptionText;
    private TextEntity description2Text = null;

    public boolean highlighted;
    public boolean selected;

    public PerkSelector(Context context, Perk perk, float x, float y) {
        this.context = context;
        pixel = context.getPixel();

        this.perk = perk;
        image = context.getImage(perk.getName());

        BitmapFont titleFont = context.getFont(Context.VCR20);
        titleFont.setColor(Constants.WHITE);

        BitmapFont font = context.getFont(Context.M5X716);
        font.setColor(Constants.WHITE);

        titleText = new TextEntity(titleFont, perk.getTitle(), x + 60, y + 6);
        descriptionText = new TextEntity(font, perk.getDescription(), x + 60, y - 12);

        String description2 = perk.getDescription2();
        if (description2 != null) {
            titleText.y += 4;
            descriptionText.y += 6;
            description2Text = new TextEntity(font, description2, x + 60, y - 17);
        }

        this.x = x;
        this.y = y;
    }

    private float getTextLength() {
        return Math.max(titleText.w, Math.max(descriptionText.w, description2Text != null ? description2Text.w : 0));
    }

    @Override
    public boolean contains(float x, float y) {
        float left = this.x - image.getRegionWidth() / 2f;
        float right = x + image.getRegionWidth() / 2f + 60 + getTextLength();
        float top = this.y + image.getRegionHeight() / 2f;
        float bottom = this.y - image.getRegionHeight() / 2f;
        return x > left
            && x < right
            && y > bottom
            && y < top;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (highlighted || selected) {
            if (highlighted) sb.setColor(Constants.PURPLE);
            else sb.setColor(Constants.BLACK);
            sb.draw(pixel,
                x - image.getRegionWidth() / 2f - 2,
                y - image.getRegionWidth() / 2f - 2,
                image.getRegionWidth() + 60 + getTextLength() + 4,
                image.getRegionHeight() + 4
            );
        }

        sb.setColor(1, 1, 1, 1);
        Utils.drawCentered(sb, image, x, y);
        titleText.render(sb);
        descriptionText.render(sb);
        if (description2Text != null) description2Text.render(sb);
    }
}
