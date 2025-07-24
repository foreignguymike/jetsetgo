package com.distraction.jetsetgo;

import com.badlogic.gdx.graphics.Color;

@SuppressWarnings("all")
public class Constants {

    public static final String TITLE = "Jet Set Go!";
    public static final int WIDTH = 640;
    public static final int HEIGHT = 360;
    public static final int SCALE = 2;
    public static final int SWIDTH = WIDTH * SCALE;
    public static final int SHEIGHT = HEIGHT * SCALE;

    public static final boolean FULLSCREEN = false;

    public static final String VERSION = "v0.0.1";

    // PICO-8 (https://www.lexaloffle.com/pico-8.php)
    // Palette from lospec. https://lospec.com/palette-list/pico-8
    public static final Color[] COLORS = new Color[]{
        Color.valueOf("000000"),
        Color.valueOf("1D2B53"),
        Color.valueOf("7E2553"),
        Color.valueOf("008751"),
        Color.valueOf("AB5236"),
        Color.valueOf("5F574F"),
        Color.valueOf("C2C3C7"),
        Color.valueOf("FFF1E8"),
        Color.valueOf("FF004D"),
        Color.valueOf("FFA300"),
        Color.valueOf("FFEC27"),
        Color.valueOf("00E436"),
        Color.valueOf("29ADFF"),
        Color.valueOf("83769C"),
        Color.valueOf("FF77A8"),
        Color.valueOf("FFCCAA")
    };

    public static final Color BLACK = COLORS[0];
    public static final Color DARK_GREEN = COLORS[3];
    public static final Color WHITE = COLORS[7];
    public static final Color BLUE = COLORS[12];
    public static final Color PURPLE = COLORS[13];
}
