package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Perk;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.PerkSelector;

public class PerkSelectorScreen extends Screen {

    public enum Type {
        ABILITY,
        PASSIVE1,
        PASSIVE2
    }

    private final TextureRegion horizontal;
    private final TextureRegion vertical;

    private float dim;

    private final Button backButton;
    private final Type type;
    private final PerkSelector[] selectors;

    public PerkSelectorScreen(Context context, Type type, Perk[] perks) {
        super(context);
        this.type = type;

        horizontal = context.getImage("framehorizontal");
        vertical = context.getImage("framevertical");

        transparent = true;

        setPanTransition(
            new Vector2(Constants.WIDTH / 2f, -Constants.HEIGHT / 2f),
            new Vector2(Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            cam
        );

        selectors = new PerkSelector[perks.length];
        float menuHeight = Math.min(perks.length * 70, 250);
        float top = Constants.HEIGHT / 2f + menuHeight / 2f;
        float spacing = menuHeight / perks.length;
        System.out.println(menuHeight + ", " + top + ", " + spacing);
        for (int i = 0; i < selectors.length; i++) {
            Perk p = perks[i];
            PerkSelector ps = new PerkSelector(context, p, 180, top - i * spacing - spacing / 2f);
            ps.selected = p == context.ability || p == context.passive1 || p == context.passive2;
            selectors[i] = ps;
        }

        backButton = new Button(context.getImage("back"), 30, Constants.HEIGHT - 30);
    }

    @Override
    public void input() {
        if (ignoreInput) return;

        unproject();
        for (PerkSelector p : selectors) {
            p.highlighted = !p.selected && p.contains(m.x, m.y);
        }
        if (Gdx.input.justTouched()) {
            for (PerkSelector p : selectors) {
                if (!p.selected && p.contains(m.x, m.y)) {
                    if (type == Type.ABILITY) context.ability = p.perk;
                    else if (type == Type.PASSIVE1) context.passive1 = p.perk;
                    else if (type == Type.PASSIVE2) context.passive2 = p.perk;
                    ignoreInput = true;
                    out.start();
                }
            }
            if (in.isFinished() && !out.started() && backButton.contains(m.x, m.y)) {
                ignoreInput = true;
                out.start();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ignoreInput = true;
            out.start();
        }
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

        // draw frame
        sb.setColor(Constants.COLORS[5]);
        sb.draw(
            pixel,
            Constants.WIDTH / 2f - horizontal.getRegionWidth() / 2f,
            Constants.HEIGHT / 2f - vertical.getRegionHeight() / 2f,
            horizontal.getRegionWidth(),
            vertical.getRegionHeight()
        );
        sb.setColor(1, 1, 1, 1);
        Utils.drawCentered(sb, vertical, Constants.WIDTH / 2f - horizontal.getRegionWidth() / 2f + 7, Constants.HEIGHT / 2f);
        Utils.drawCentered(sb, vertical, Constants.WIDTH / 2f + horizontal.getRegionWidth() / 2f - 7, Constants.HEIGHT / 2f);
        Utils.drawCentered(sb, horizontal, Constants.WIDTH / 2f, Constants.HEIGHT / 2f + vertical.getRegionHeight() / 2f - 7);
        Utils.drawCentered(sb, horizontal, Constants.WIDTH / 2f, Constants.HEIGHT / 2f - vertical.getRegionHeight() / 2f + 7);

        for (PerkSelector p : selectors) {
            p.render(sb);
        }

        sb.setProjectionMatrix(uiCam.combined);
        if (in.isFinished() && !out.started()) backButton.render(sb);

        sb.end();
    }
}
