package com.distraction.jetsetgo.screens;

import com.distraction.jetsetgo.Context;
import com.distraction.jetsetgo.entity.Player;

public class PlayScreen extends Screen {

    private final Player player;

    public PlayScreen(Context context) {
        super(context);

        player = new Player(context);
        player.x = 100;
        player.y = 100;
    }

    @Override
    public void input() {
        if (ignoreInput) return;
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render() {
        sb.begin();
        sb.setProjectionMatrix(uiCam.combined);
        sb.setProjectionMatrix(cam.combined);
        sb.end();
    }

}
