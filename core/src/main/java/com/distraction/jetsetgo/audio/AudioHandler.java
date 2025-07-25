package com.distraction.jetsetgo.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class AudioHandler {

    private final Map<String, Music> music;
    private final Map<String, Sound> sounds;

    private MusicConfig currentlyPlaying;

    public AudioHandler() {
        music = new HashMap<>();

        sounds = new HashMap<>();
        addSound("enter", "sfx/FreeSFX/Retro Event UI 01.wav");
        addSound("select", "sfx/FreeSFX/Retro Event Acute 08.wav");
        addSound("click", "sfx/FreeSFX/Retro Event Acute 11.wav");
        addSound("collect", "sfx/FreeSFX/Retro Water Drop 01.wav");
        addSound("stop", "sfx/FreeSFX/Retro Event StereoUP 02.wav");
        addSound("submit", "sfx/FreeSFX/Retro Event UI 13.wav");
        addSound("ability", "sfx/FreeSFX/Retro Magic Protection 25(cut).wav");

        addSound("countdownlow", "sfx/countdownlow.wav");
        addSound("countdownhigh", "sfx/countdownhigh.wav");
        addSound("pluck", "sfx/click.ogg");
    }

    private void addMusic(String key, String fileName) {
        music.put(key, Gdx.audio.newMusic(Gdx.files.internal(fileName)));
    }

    private void addSound(String key, String fileName) {
        sounds.put(key, Gdx.audio.newSound(Gdx.files.internal(fileName)));
    }

    public boolean isMusicPlaying() {
        if (currentlyPlaying == null) return false;
        if (currentlyPlaying.getMusic() == null) return false;
        return currentlyPlaying.getMusic().isPlaying();
    }

    public void playMusic(String key, float volume, boolean looping) {
        Music newMusic = music.get(key);
        if (newMusic == null) {
            System.out.println("unknown music: " + key);
            return;
        }
        if (currentlyPlaying != null && newMusic != currentlyPlaying.getMusic()) {
            stopMusic();
        }
        currentlyPlaying = new MusicConfig(music.get(key), volume, looping);
        currentlyPlaying.play();
    }

    public void stopMusic() {
        if (currentlyPlaying != null) {
            currentlyPlaying.stop();
            currentlyPlaying = null;
        }
    }

    public void playSound(String key) {
        playSound(key, 1, false);
    }

    public void playSound(String key, float volume) {
        playSound(key, volume, false);
    }

    public void playSoundCut(String key, float volume) {
        playSound(key, volume, true);
    }

    public void playSound(String key, float volume, boolean cut) {
        for (Map.Entry<String, Sound> entry : sounds.entrySet()) {
            if (entry.getKey().equals(key)) {
                if (cut) entry.getValue().stop();
                entry.getValue().play(volume);
            }
        }
    }

}
