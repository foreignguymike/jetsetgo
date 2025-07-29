package com.distraction.jetsetgo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.distraction.jetsetgo.Ability;
import com.distraction.jetsetgo.Constants;
import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.Passive;
import com.distraction.jetsetgo.Perk;
import com.distraction.jetsetgo.Utils;
import com.distraction.jetsetgo.entity.Button;
import com.distraction.jetsetgo.entity.Collectible;
import com.distraction.jetsetgo.entity.Countdown;
import com.distraction.jetsetgo.entity.Entity;
import com.distraction.jetsetgo.entity.Particle;
import com.distraction.jetsetgo.entity.Player;
import com.distraction.jetsetgo.entity.TextEntity;
import com.distraction.jetsetgo.entity.Water;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayScreen extends Screen {

    private static final int MAX_COMBO = 10;
    private static final float ABILITY_TIMER_MAX = 5;

    enum State {
        COUNTDOWN,
        GO
    }

    private State state = State.COUNTDOWN;

    private final Player player;

    private final List<Particle> particles;
    private final List<Collectible> collectibles;

    private final int mapWidth = 2000;
    private final int mapHeight = 2000;

    private float countdownTimer = 5;
    private final Countdown[] countdowns;

    private float timer = 30;
    private int timerInt = MathUtils.ceil(timer);

    private int score;

    private final TextEntity scoreText;
    private final TextEntity timeText;
    private final Button backButton;
    private final Button restartButton;

    private int combo;
    private float comboTimer;
    private float comboTimerMax;
    private final TextEntity comboText;

    private final List<Perk> passives;

    private boolean abilityUsed;
    private float abilityTimer;
    private final TextEntity abilityText;

    private final Water water;

    public PlayScreen(Context context) {
        super(context);

        context.data.reset();

        ignoreInput = true;
        in = new Transition(context, Transition.Type.CHECKERED_IN, 0.5f, () -> ignoreInput = false);
        in.start();
        out = new Transition(context, Transition.Type.CHECKERED_OUT, 0.5f, () -> context.sm.replace(new PlayScreen(context)));

        scoreText = new TextEntity(context.getFont(Context.VCR20), "0", Constants.WIDTH - 20, Constants.HEIGHT - 30, TextEntity.Alignment.RIGHT);
        scoreText.setColor(Constants.WHITE);
        timeText = new TextEntity(context.getFont(Context.VCR20, 2), timerInt + "", Constants.WIDTH / 2f, Constants.HEIGHT - 40, TextEntity.Alignment.CENTER);
        timeText.setColor(Constants.WHITE);

        comboText = new TextEntity(context.getFont(Context.M5X716, 2), "", Constants.WIDTH / 2f, Constants.HEIGHT / 2f + 30, TextEntity.Alignment.CENTER);
        comboText.setColor(Constants.WHITE);
        comboTimerMax = 1;

        backButton = new Button(context.getImage("back"), 30, Constants.HEIGHT - 30);
        restartButton = new Button(context.getImage("restart"), 80, Constants.HEIGHT - 30);

        countdowns = new Countdown[] {
            new Countdown(context.getImage("countdownready"), Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            new Countdown(context.getImage("countdown3"), Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            new Countdown(context.getImage("countdown2"), Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            new Countdown(context.getImage("countdown1"), Constants.WIDTH / 2f, Constants.HEIGHT / 2f),
            new Countdown(context.getImage("countdowngo"), Constants.WIDTH / 2f, Constants.HEIGHT / 2f)
        };
        countdowns[0].start();

        particles = new ArrayList<>();
        collectibles = new ArrayList<>();

        player = new Player(context, particles);
        player.x = mapWidth / 2f;
        player.y = mapHeight / 2f;
        cam.position.x = MathUtils.clamp(player.x, cam.viewportWidth / 2f, mapWidth - cam.viewportWidth / 2f);
        cam.position.y = MathUtils.clamp(player.y, cam.viewportHeight / 2f, mapHeight - cam.viewportHeight / 2f);
        cam.update();

        abilityText = new TextEntity(context.getFont(Context.M5X716), context.ability.getName(), Constants.WIDTH / 2f, 20, TextEntity.Alignment.CENTER);

        // check perks
        passives = new ArrayList<>();
        passives.add(context.passive1);
        passives.add(context.passive2);
        if (passives.contains(Passive.SPEEDO_MODE)) player.maxSpeedMulti = 1.5f;
        if (passives.contains(Passive.SURF_STEERING)) player.steerSpeedMulti = 3;
        if (passives.contains(Passive.CHAIN_REACTION)) comboTimerMax = 3;
        if (passives.contains(Passive.SUMMER_HOURS)) {
            timer = 37;
            timeText.setText("37");
        }

        // parse map
        TiledMap map = context.getMap();
        Map<String, Collectible.Type> typeMapping = new HashMap<>();
        typeMapping.put("watermelon", Collectible.Type.WATERMELON);
        typeMapping.put("beachball", Collectible.Type.BEACH_BALL);
        typeMapping.put("sunglasses", Collectible.Type.SUNGLASSES);
        for (Map.Entry<String, Collectible.Type> entry : typeMapping.entrySet()) {
            MapLayer layer = map.getLayers().get(entry.getKey());
            for (MapObject o : layer.getObjects()) {
                MapProperties props = o.getProperties();
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                collectibles.add(new Collectible(context, entry.getValue(), x, y));
            }
        }

        water = new Water(context);

        context.audio.playMusic("bg", 0.3f, true);
    }

    private void collect(Collectible c) {
        context.audio.playSoundCut("collect", 0.5f);
        int points = MathUtils.ceil(c.getPoints() * (1 + combo * 0.05f));
        if (abilityTimer > 0 && context.ability == Ability.DOUBLE_DIP) points *= 2;
        score += points;
        scoreText.setText(score + "");
        c.remove = true;

        if (combo < MAX_COMBO) {
            combo += 1;
            comboText.setText(combo + "x");
        }
        if (comboTimer < comboTimerMax) comboTimer = comboTimerMax;
    }

    private void useAbility() {
        if (!abilityUsed) {
            abilityUsed = true;
            context.audio.playSound("ability", 0.5f);
            if (context.ability == Ability.WHIRLPOOL) {
                Entity camBounds = new Entity();
                camBounds.x = cam.position.x;
                camBounds.y = cam.position.y;
                camBounds.w = Constants.WIDTH;
                camBounds.h = Constants.HEIGHT;
                for (int i = 0; i < collectibles.size(); i++) {
                    Collectible c = collectibles.get(i);
                    if (camBounds.contains(c.x, c.y)) {
                        c.setPlayer(player);
                    }
                }
            } else if (context.ability == Ability.DOUBLE_DIP) {
                abilityTimer = ABILITY_TIMER_MAX;
            } else if (context.ability == Ability.HEAT_WAVE) {
                abilityTimer = ABILITY_TIMER_MAX;
                if (combo < 10) {
                    combo = 10;
                    comboText.setText(combo + "x");
                }
                comboTimer = comboTimerMax + ABILITY_TIMER_MAX;
            }
        }
    }

    private void restart() {
        ignoreInput = true;
        out.setCallback(() -> context.sm.replace(new PlayScreen(context)));
        out.start();
        context.audio.playSound("click");
    }

    private void back() {
        ignoreInput = true;
        out.setCallback(() -> context.sm.replace(new PerkScreen(context)));
        out.start();
        context.audio.playSound("click");
    }

    @Override
    public void input() {
        if (ignoreInput) return;

        player.up = state == State.GO && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W));
        player.down = state == State.GO && (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S));
        player.left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        player.right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);

        if (state == State.GO && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            useAbility();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restart();
        }

        if (Gdx.input.justTouched()) {
            unproject();
            if (restartButton.contains(m.x, m.y, 2, 2)) {
                restart();
            }
            if (backButton.contains(m.x, m.y, 2, 2)) {
                back();
            }
        }
    }

    @Override
    public void update(float dt) {
        in.update(dt);
        out.update(dt);

        water.update(dt);
        water.x = -cam.position.x;
        water.y = -cam.position.y;

        for (Countdown cd : countdowns) cd.update(dt);

        if (state == State.COUNTDOWN) {
            float previousCountdownTimer = countdownTimer;
            countdownTimer -= dt;
            player.update(dt);

            if (previousCountdownTimer > 3.3f && countdownTimer < 3.3f) countdowns[1].start();
            else if (previousCountdownTimer > 2.3f && countdownTimer < 2.3f) countdowns[2].start();
            else if (previousCountdownTimer > 1.3f && countdownTimer < 1.3f) countdowns[3].start();
            else if (previousCountdownTimer > 0.3f && countdownTimer < 0.3f) countdowns[4].start();

            if (previousCountdownTimer > 3f && countdownTimer < 3f) context.audio.playSound("countdownlow");
            if (previousCountdownTimer > 2f && countdownTimer < 2f) context.audio.playSound("countdownlow");
            if (previousCountdownTimer > 1f && countdownTimer < 1f) context.audio.playSound("countdownlow");
            if (countdownTimer < 0) {
                state = State.GO;
                context.audio.playSound("countdownhigh");
            }
            return;
        }

        timer -= dt;
        int newTimerInt = MathUtils.ceil(timer);
        if (timerInt != newTimerInt) { // avoid unnecessary glyph relayout
            timerInt = newTimerInt;
            timeText.setText(timerInt + "");
        }

        if (timer < 0) {
            context.data.score = score;
            context.sm.push(new FinishScreen(context));
            context.audio.playSound("stop", 0.5f);
            return;
        }

        comboTimer -= dt;
        if (comboTimer < 0) {
            combo = 0;
            comboText.setText("");
        }

        abilityTimer -= dt;

        if (player.x > mapWidth) player.x -= mapWidth;
        if (player.x < 0) player.x += mapWidth;
        if (player.y > mapHeight) player.y -= mapHeight;
        if (player.y < 0) player.y += mapHeight;
        cam.position.x = MathUtils.clamp(player.x, cam.viewportWidth / 2f, mapWidth - cam.viewportWidth / 2f);
        cam.position.y = MathUtils.clamp(player.y, cam.viewportHeight / 2f, mapHeight - cam.viewportHeight / 2f);
        cam.update();

        player.update(dt);

        for (int i = 0; i < collectibles.size(); i++) {
            Collectible c = collectibles.get(i);
            c.update(dt);
            if (passives.contains(Passive.MAIN_ATTRACTION) && c.contains(player.x, player.y, 40, 40)) {
                c.setPlayer(player);
            }
            if (player.intersects(c)) {
                collect(c);
            }
            if (c.remove) collectibles.remove(i--);
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            p.update(dt);
            if (p.remove) particles.remove(i--);
        }
    }

    @Override
    public void render() {
        Utils.clearScreen(Constants.BLUE);
        sb.begin();

        sb.setProjectionMatrix(uiCam.combined);
        water.render(sb);

        sb.setProjectionMatrix(cam.combined);
        sb.setColor(1, 1, 1, 1);
        for (Particle p : particles) p.render(sb);
        player.render(sb);
        for (Collectible c : collectibles) c.render(sb);

        sb.setProjectionMatrix(uiCam.combined);
        timeText.render(sb);
        scoreText.render(sb);
        comboText.render(sb);
        backButton.render(sb);
        restartButton.render(sb);
        for (Countdown cd : countdowns) cd.render(sb);
        // combo bar
        if (comboTimer > 0) {
            sb.setColor(Constants.BLACK);
            sb.draw(pixel, Constants.WIDTH / 2f - 26, Constants.HEIGHT / 2f + 19, 52, 4);
            sb.setColor(Constants.DARK_GREEN);
            sb.draw(pixel, Constants.WIDTH / 2f - 25, Constants.HEIGHT / 2f + 20, 50, 2);
            sb.setColor(Constants.GREEN);
            sb.draw(pixel, Constants.WIDTH / 2f - 25, Constants.HEIGHT / 2f + 20, Math.min(50 * comboTimer / comboTimerMax, 50), 2);
        }

        // ability bar
        if (abilityTimer > 0) {
            sb.setColor(Constants.BLACK);
            sb.draw(pixel, Constants.WIDTH / 2f - 102, abilityText.y - 7, 204, 16);
            sb.setColor(Constants.PURPLE);
            sb.draw(pixel, Constants.WIDTH / 2f - 100, abilityText.y - 5, Math.max(200 * abilityTimer / ABILITY_TIMER_MAX, 0), 12);
            abilityText.render(sb);
        }

        in.render(sb);
        out.render(sb);

        sb.end();
    }

}
