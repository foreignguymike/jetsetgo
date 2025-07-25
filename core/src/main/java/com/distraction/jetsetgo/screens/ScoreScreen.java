package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.TextEntity;

import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

public class ScoreScreen extends Screen {

    private final Button backButton;

    private float dim;

    private final TextEntity titleText;
    private final TextEntity[][] scoreTexts;

    public ScoreScreen(Context context) {
        super(context);

        transparent = true;

        setPanTransition(
            new Vector2(Constants.WIDTH / 2f, -Constants.HEIGHT / 2f),
            new Vector2(Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            cam
        );

        backButton = new Button(context.getImage("back"), 30, Constants.HEIGHT - 30);

        titleText = new TextEntity(context.getFont(Context.VCR20), "HIGH SCORES", Constants.WIDTH / 2f, Constants.HEIGHT - 30, TextEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);

        BitmapFont scoreFont = context.getFont(Context.M5X716);
        scoreTexts = new TextEntity[Context.MAX_SCORES][3];
        for (int i = 0; i < scoreTexts.length; i++) {
            scoreTexts[i][0] = new TextEntity(scoreFont, (i + 1) + "", 220, Constants.HEIGHT - 60 - i * 15);
            scoreTexts[i][1] = new TextEntity(scoreFont, "", 250, scoreTexts[i][0].y);
            scoreTexts[i][2] = new TextEntity(scoreFont, "", 400, scoreTexts[i][0].y);
        }

        updateLeaderboards();
    }

    private void updateLeaderboards() {
        for (int i = 0; i < Context.MAX_SCORES; i++) {
            if (i < context.entries.size()) {
                ILeaderBoardEntry entry = context.entries.get(i);
                scoreTexts[i][1].setText(entry.getUserDisplayName());
                scoreTexts[i][2].setText(entry.getFormattedValue());
            } else {
                scoreTexts[i][1].setText("-");
                scoreTexts[i][2].setText("-");
            }
        }
    }

    @Override
    public void input() {
        if (ignoreInput) return;

        if (Gdx.input.justTouched()) {
            unproject();
            if (in.isFinished() && !out.started() && backButton.contains(m.x, m.y)) {
                ignoreInput = true;
                out.start();
                context.audio.playSound("pluck");
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
        sb.setColor(Constants.DARK_BLUE);
        sb.draw(pixel, 180, 0, (Constants.WIDTH - 180 * 2), Constants.HEIGHT);
        sb.setColor(Constants.PURPLE);
        for (int i = 0; i < scoreTexts.length; i++) {
            if (i % 2 == 0) continue;
            TextEntity[] row = scoreTexts[i];
            float y = row[0].y;
            sb.draw(pixel, 180, y + 9, (Constants.WIDTH - 180 * 2), 15);
        }

        titleText.render(sb);
        for (TextEntity[] scoreText : scoreTexts) {
            scoreText[0].render(sb);
            scoreText[1].render(sb);
            scoreText[2].render(sb);
        }

        sb.setProjectionMatrix(uiCam.combined);
        sb.setColor(1, 1, 1, 1);
        if (in.isFinished() && !out.started()) backButton.render(sb);

        sb.end();
    }
}
