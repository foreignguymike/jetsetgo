package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.distraction.jetsetgo.Ability;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Passive;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.TextEntity;

public class PerkScreen extends Screen {

    private final TextEntity titleText;
    private final TextEntity abilityText;
    private final TextEntity passiveText;

    private Button abilityIcon;
    private Button passive1Icon;
    private Button passive2Icon;

    private Button playButton;

    public PerkScreen(Context context) {
        super(context);

        ignoreInput = true;
        in = new Transition(context, Transition.Type.FLASH_IN, 0.5f, () -> ignoreInput = false);
        in.start();
        out = new Transition(context, Transition.Type.CHECKERED_OUT, 0.5f, () -> context.sm.replace(new PlayScreen(context)));

        titleText = new TextEntity(context.getFont(Context.VCR20, 2), "PERKS", Constants.WIDTH / 2f, Constants.HEIGHT - 40, TextEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);

        abilityText = new TextEntity(context.getFont(Context.VCR20), "Choose 1 ability", Constants.WIDTH / 2f, Constants.HEIGHT - 100, TextEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);
        abilityIcon = new Button(context.getImage("perk"), Constants.WIDTH / 2f, Constants.HEIGHT - 140);

        passiveText = new TextEntity(context.getFont(Context.VCR20), "Choose 2 passives", Constants.WIDTH / 2f, Constants.HEIGHT - 200, TextEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);
        passive1Icon = new Button(context.getImage("perk"), Constants.WIDTH / 2f - 50, Constants.HEIGHT - 240);
        passive2Icon = new Button(context.getImage("perk"), Constants.WIDTH / 2f + 50, Constants.HEIGHT - 240);
        updateIcons();

        playButton = new Button(context.getImage("playbutton"), Constants.WIDTH / 2f, 50);
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
            } else if (passive1Icon.contains(m.x, m.y, 5, 5)) {
                context.sm.push(new PerkSelectorScreen(context, PerkSelectorScreen.Type.PASSIVE1, Passive.values()));
            } else if (passive2Icon.contains(m.x, m.y, 5, 5)) {
                context.sm.push(new PerkSelectorScreen(context, PerkSelectorScreen.Type.PASSIVE2, Passive.values()));
            } else if (context.perksSet() && playButton.contains(m.x, m.y, 5, 5)) {
                ignoreInput = true;
                out.setCallback(() -> context.sm.replace(new PlayScreen(context)));
                out.start();
            }
        }
    }

    @Override
    public void update(float dt) {
        in.update(dt);
        out.update(dt);
    }

    @Override
    public void render() {
        Utils.clearScreen(Constants.PURPLE);
        sb.begin();
        sb.setProjectionMatrix(uiCam.combined);
        titleText.render(sb);
        abilityText.render(sb);
        passiveText.render(sb);

        abilityIcon.render(sb);
        passive1Icon.render(sb);
        passive2Icon.render(sb);
        if (context.perksSet()) {
            playButton.render(sb);
        }

        in.render(sb);
        out.render(sb);
        sb.end();

    }
}
