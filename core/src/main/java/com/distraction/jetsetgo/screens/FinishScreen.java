package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.TextEntity;

public class FinishScreen extends Screen {

    private float dim;

    private final boolean newHighscore;

    private final TextEntity scoreText;
    private final TextEntity titleText;

    private final Button submitButton;
    private final TextEntity submitText;
    private boolean loading;

    private final Button backButton;
    private final Button restartButton;
    private final Button scoresButton;

    private float timer;

    public FinishScreen(Context context) {
        super(context);
        transparent = true;
        setPanTransition(
            new Vector2(Constants.WIDTH / 2f, -Constants.HEIGHT / 2f),
            new Vector2(Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            cam
        );

        scoreText = new TextEntity(context.getFont(Context.VCR20, 2f), context.data.score + "", Constants.WIDTH / 2f, Constants.HEIGHT / 2f - 20, TextEntity.Alignment.CENTER);
        scoreText.setColor(Constants.WHITE);

        newHighscore = context.isHighscore(context.data.name, context.data.score);
        titleText = new TextEntity(context.getFont(Context.VCR20, 3f), newHighscore ? "NEW HIGHSCORE!" : "Try again", Constants.WIDTH / 2f, Constants.HEIGHT / 2f + 70, TextEntity.Alignment.CENTER);
        titleText.setColor(Constants.WHITE);

        submitButton = new Button(context.getImage("submit"), Constants.WIDTH / 2f, 80);
        submitText = new TextEntity(context.getFont(Context.M5X716, 2f), "Submitted!", Constants.WIDTH / 2f, 70, TextEntity.Alignment.CENTER);
        scoresButton = new Button(context.getImage("scores"), Constants.WIDTH / 2f, 40);

        backButton = new Button(context.getImage("back"), 30, Constants.HEIGHT - 30);
        restartButton = new Button(context.getImage("restart"), 80, Constants.HEIGHT - 30);
    }

    private void submit() {
        if (context.data.name.isEmpty() || !context.leaderboardsInitialized) return;
        if (context.data.submitted) return;
        if (loading) return;
        loading = true;
        context.audio.playSound("click");
        context.submitScore(context.data.name, context.data.score, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                // throwing an exception with SubmitScoreResponse here for some reason
                // just doing a sus true check instead
                if (res.contains("true")) {
                    context.data.submitted = true;
                    context.fetchLeaderboard(success -> {});
                    context.audio.playSound("submit");
                } else {
                    failed(null);
                }
                loading = false;
            }

            @Override
            public void failed(Throwable t) {
                ignoreInput = false;
                loading = false;
            }

            @Override
            public void cancelled() {
                failed(null);
            }
        });
    }

    @Override
    public void input() {
        if (ignoreInput) return;

        if (Gdx.input.justTouched()) {
            unproject();
            if (newHighscore && submitButton.contains(m.x, m.y, 2, 2)) {
                submit();
            }
            if (restartButton.contains(m.x, m.y, 2, 2)) {
                ignoreInput = true;
                out.setCallback(() -> {
                    context.sm.pop();
                    context.sm.replace(new PlayScreen(context));
                });
                out.start();
                context.audio.playSound("click");
            }
            if (backButton.contains(m.x, m.y, 2, 2)) {
                ignoreInput = true;
                out.setCallback(() -> context.sm.replace(new PerkScreen(context)));
                out.start();
                context.audio.playSound("pluck");
            }
            if (scoresButton.contains(m.x, m.y, 2, 2)) {
                ignoreInput = true;
                context.sm.push(new ScoreScreen(context));
                context.audio.playSound("click");
            }
        }
    }

    @Override
    public void update(float dt) {
        in.update(dt);
        out.update(dt);
        timer += dt;

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
        titleText.render(sb);
        if (newHighscore) {
            if (!context.data.submitted) {
                submitButton.render(sb);
            } else {
                submitText.render(sb);
            }
        }
        if (loading) {
            for (int i = 0; i < 5; i++) {
                float x = submitButton.x + submitButton.w / 2f + 10 * MathUtils.cos(-6f * timer + i * 0.1f) - 5;
                float y = submitButton.y + 10 * MathUtils.sin(-6f * timer + i * 0.1f) - 5;
                sb.draw(pixel, x, y, 2, 2);
            }
        }
        scoreText.render(sb);
        if (in.isFinished() && !out.started()) {
            backButton.render(sb);
            restartButton.render(sb);
        }
        scoresButton.render(sb);

        sb.end();
    }
}
