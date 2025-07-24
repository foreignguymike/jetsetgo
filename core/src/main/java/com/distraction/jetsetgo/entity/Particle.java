package com.distraction.jetsetgo.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.distraction.jetsetgo.Animation;
import com.distraction.jetsetgo.Utils;

public class Particle extends Entity {

    private final Animation animation;

    // if true, remove on single animation loop
    public boolean singleAnimate;

    public Particle(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void update(float dt) {
        animation.update(dt);

        if (singleAnimate && animation.getFinishCount() > 0) {
            remove = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Utils.drawRotated(sb, animation.getImage(), x, y, rad);
    }
}
