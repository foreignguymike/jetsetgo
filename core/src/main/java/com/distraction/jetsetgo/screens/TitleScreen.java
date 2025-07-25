package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.TextEntity;

public class TitleScreen extends Screen {

    private final TextureRegion title;

    private final TextEntity playerText;
    private final TextEntity versionText;

    private final Button playButton;
    private final Button scoresButton;

    private final TextEntity errorText;
    private float errorTextTime;

    public TitleScreen(Context context) {
        super(context);

        title = context.getImage("title");

        ignoreInput = true;
        in = new Transition(context, Transition.Type.FLASH_IN, 0.5f, () -> ignoreInput = false);
        in.start();
        out = new Transition(context, Transition.Type.CHECKERED_OUT, 0.5f, () -> context.sm.replace(new PlayScreen(context)));

        playerText = new TextEntity(context.getFont(Context.M5X716), "Player: " + context.data.name, 10, Constants.HEIGHT - 20);
        versionText = new TextEntity(context.getFont(Context.M5X716), Constants.VERSION, Constants.WIDTH - 5, 5, TextEntity.Alignment.RIGHT);

        playButton = new Button(context.getImage("play"), Constants.WIDTH / 2f - 80, 80);
        scoresButton = new Button(context.getImage("scores"), Constants.WIDTH / 2f + 80, 80);

        errorText = new TextEntity(context.getFont(Context.M5X716), "", Constants.WIDTH / 2f, 5, TextEntity.Alignment.CENTER);
        if (!context.leaderboardsInitialized && !context.leaderboardsRequesting) {
            errorText.setText("Fetching leaderboards...");
            errorTextTime = 30f;
            context.fetchLeaderboard((success) -> {
                errorText.setText(success ? "Leaderboards fetched!" : "Error fetching leaderboards");
                errorTextTime = 3f;
            });
        }
    }

    @Override
    public void input() {
        if (ignoreInput) return;
        if (Gdx.input.justTouched()) {
            unproject();
            if (playerText.contains(m.x, m.y, 5, 3)) {
                ignoreInput = true;
                out = new Transition(context, Transition.Type.FLASH_OUT, 0.5f, () -> context.sm.replace(new NameScreen(context)));
                out.start();
            }
            if (playButton.contains(m.x, m.y, 2, 2)) {
                ignoreInput = true;
                out.setCallback(() -> context.sm.replace(new PerkScreen(context)));
                out.start();
            }
        }
    }

    @Override
    public void update(float dt) {
        in.update(dt);
        out.update(dt);
        errorTextTime -= dt;
    }

    @Override
    public void render() {

        sb.begin();

        sb.setProjectionMatrix(cam.combined);
        sb.setColor(Constants.DARK_BLUE);
        sb.draw(pixel, 0, 0, Constants.WIDTH, Constants.HEIGHT);
        sb.setColor(Constants.BLACK);
        sb.draw(pixel, 0, Constants.HEIGHT / 2f + 27, Constants.WIDTH, 43);

        sb.setColor(1, 1, 1, 1);
        Utils.drawCentered(sb, title, Constants.WIDTH / 2f, Constants.HEIGHT / 2f + 50);

        playerText.render(sb);
        versionText.render(sb);
        if (errorTextTime > 0) errorText.render(sb);

        playButton.render(sb);
        scoresButton.render(sb);

        in.render(sb);
        out.render(sb);

        sb.end();

    }
}
