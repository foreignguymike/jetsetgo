package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.Ability;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Passive;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.FontEntity;

public class PerkScreen extends Screen {

    private final FontEntity titleText;
    private final FontEntity abilityText;
    private final FontEntity passiveText;

    private Ability ability = null;
    private Passive passive1 = null;
    private Passive passive2 = null;
    private TextureRegion abilityIcon;
    private TextureRegion passive1Icon;
    private TextureRegion passive2Icon;

    public PerkScreen(Context context) {
        super(context);

        titleText = new FontEntity(context.getFont(Context.VCR20, 2), "PERKS", Constants.WIDTH / 2f, Constants.HEIGHT - 40, FontEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);

        abilityText = new FontEntity(context.getFont(Context.VCR20), "Choose 1 ability", Constants.WIDTH / 2f, Constants.HEIGHT - 110, FontEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);

        passiveText = new FontEntity(context.getFont(Context.VCR20), "Choose 2 passives", Constants.WIDTH / 2f, Constants.HEIGHT - 230, FontEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);

        abilityIcon = context.getImage("perk");
        passive1Icon = context.getImage("perk");
        passive2Icon = context.getImage("perk");
    }

    @Override
    public void input() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        Utils.clearScreen(Constants.PURPLE);
        sb.begin();
        sb.setProjectionMatrix(uiCam.combined);
        titleText.render(sb);
        abilityText.render(sb);
        passiveText.render(sb);

        Utils.drawCentered(sb, abilityIcon, Constants.WIDTH / 2f, Constants.HEIGHT - 160);
        Utils.drawCentered(sb, passive1Icon, Constants.WIDTH / 2f - 50, Constants.HEIGHT - 280);
        Utils.drawCentered(sb, passive2Icon, Constants.WIDTH / 2f + 50, Constants.HEIGHT - 280);
        sb.end();

    }
}
