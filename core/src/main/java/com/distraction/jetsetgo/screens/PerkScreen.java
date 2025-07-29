package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.jetsetgo.Ability;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Passive;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.TextEntity;
import com.distraction.jetsetgo.entity.Water;

public class PerkScreen extends Screen {

    private final TextEntity titleText;
    private final TextEntity abilityText;
    private final TextEntity passiveText;

    private final Button abilityIcon;
    private final Button passive1Icon;
    private final Button passive2Icon;

    private final Button backButton;
    private final Button goButton;

    private final TextEntity helpText;
    private float helpTimer;

    private final Water water;

    public PerkScreen(Context context) {
        super(context);

        ignoreInput = true;
        in = new Transition(context, Transition.Type.CHECKERED_IN, 0.5f, () -> ignoreInput = false);
        in.start();
        out = new Transition(context, Transition.Type.CHECKERED_OUT, 0.5f, () -> context.sm.replace(new PlayScreen(context)));

        titleText = new TextEntity(context.getFont(Context.VCR20, 2), "PERKS", Constants.WIDTH / 2f, Constants.HEIGHT - 40, TextEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);

        abilityText = new TextEntity(context.getFont(Context.VCR20), "Choose 1 ability", Constants.WIDTH / 2f, Constants.HEIGHT - 100, TextEntity.Alignment.CENTER);
        abilityText.setColor(Constants.WHITE);
        abilityIcon = new Button(context.getImage("perk"), Constants.WIDTH / 2f, Constants.HEIGHT - 140);

        passiveText = new TextEntity(context.getFont(Context.VCR20), "Choose 2 passives", Constants.WIDTH / 2f, Constants.HEIGHT - 200, TextEntity.Alignment.CENTER);
        passiveText.setColor(Constants.WHITE);
        passive1Icon = new Button(context.getImage("perk"), Constants.WIDTH / 2f - 50, Constants.HEIGHT - 240);
        passive2Icon = new Button(context.getImage("perk"), Constants.WIDTH / 2f + 50, Constants.HEIGHT - 240);
        updateIcons();

        backButton = new Button(context.getImage("back"), 30, Constants.HEIGHT - 30);
        goButton = new Button(context.getImage("go"), Constants.WIDTH / 2f, 50);

        helpText = new TextEntity(context.getFont(Context.M5X716), "Click ->", abilityIcon.x - 30, abilityIcon.y, TextEntity.Alignment.RIGHT);

        water = new Water(context);
        water.a = 0.06f;

        context.audio.playMusic("bg", 0.3f, true);
    }

    private void updateIcons() {
        if (context.ability != null) abilityIcon.setImage(context.getImage(context.ability.getName()));
        else abilityIcon.setImage(context.getImage("perk"));
        if (context.passive1 != null) passive1Icon.setImage(context.getImage(context.passive1.getName()));
        else passive1Icon.setImage(context.getImage("perk"));
        if (context.passive2 != null) passive2Icon.setImage(context.getImage(context.passive2.getName()));
        else passive2Icon.setImage(context.getImage("perk"));
    }

    @Override
    public void resume() {
        updateIcons();
    }

    @Override
    public void input() {
        if (ignoreInput) return;

        if (Gdx.input.justTouched()) {
            unproject();
            if (abilityIcon.contains(m.x, m.y, 5, 5)) {
                context.sm.push(new PerkSelectorScreen(context, PerkSelectorScreen.Type.ABILITY, Ability.values()));
                context.audio.playSound("click");
            } else if (passive1Icon.contains(m.x, m.y, 5, 5)) {
                context.sm.push(new PerkSelectorScreen(context, PerkSelectorScreen.Type.PASSIVE1, Passive.values()));
                context.audio.playSound("click");
            } else if (passive2Icon.contains(m.x, m.y, 5, 5)) {
                context.sm.push(new PerkSelectorScreen(context, PerkSelectorScreen.Type.PASSIVE2, Passive.values()));
                context.audio.playSound("click");
            } else if (context.perksSet() && goButton.contains(m.x, m.y, 5, 5)) {
                ignoreInput = true;
                out.setCallback(() -> context.sm.replace(new PlayScreen(context)));
                out.start();
                context.audio.playSound("click");
            }
            if (backButton.contains(m.x, m.y)) {
                ignoreInput = true;
                out = new Transition(context, Transition.Type.FLASH_OUT, 0.5f, () -> context.sm.replace(new TitleScreen(context)));
                out.start();
                context.audio.playSound("pluck");
            }
        }
    }

    @Override
    public void update(float dt) {
        in.update(dt);
        out.update(dt);
        if (context.ability == null) {
            helpTimer += dt;
            helpText.x = abilityIcon.x - 30 + 3 * MathUtils.sin(helpTimer * 6);
        }
        water.update(dt);
    }

    @Override
    public void render() {
        Utils.clearScreen(Constants.DARK_BLUE);
        sb.begin();
        sb.setProjectionMatrix(uiCam.combined);
        water.render(sb);
        titleText.render(sb);
        abilityText.render(sb);
        passiveText.render(sb);

        abilityIcon.render(sb);
        passive1Icon.render(sb);
        passive2Icon.render(sb);
        backButton.render(sb);
        goButton.a = context.perksSet() ? 1f : 0.3f;
        goButton.render(sb);
        if (context.ability == null) helpText.render(sb);

        in.render(sb);
        out.render(sb);
        sb.end();

    }
}
