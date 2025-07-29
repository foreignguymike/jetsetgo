package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.distraction.jetsetgo.Animation;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;

public class Water extends Entity {

    private static final int SIZE = 16;

    private final Animation animation;
    private final int numRows;
    private final int numCols;

    public Water(Context context) {
        animation = new Animation(context.getImage("water").split(SIZE, SIZE)[0], 0.5f);
        a = 0.2f;

        numRows = Constants.HEIGHT / SIZE + 2;
        numCols = Constants.WIDTH / SIZE + 2;
    }

    @Override
    public void update(float dt) {
        animation.update(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(1, 1, 1, a);
        float startx = x % SIZE - SIZE;
        float starty = y % SIZE - SIZE;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                sb.draw(animation.getImage(), startx + col * SIZE, starty + row * SIZE);
            }
        }
        sb.setColor(1, 1, 1, 1);
    }
}
