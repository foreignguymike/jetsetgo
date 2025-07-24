package com.distraction.jetsetgo;

public enum Ability implements Perk {
    WHIRLPOOL, // pulls in all collectibles on screen
    DOUBLE_DIP, // collectibles give 2x points for 5 seconds
    HEAT_WAVE // collectibles give 2x combo for 5 seconds
    ;

    @Override
    public String getName() {
        if (this == WHIRLPOOL) return "whirlpool";
        else if (this == DOUBLE_DIP) return "doubledip";
        else return "heatwave";
    }

    @Override
    public String getTitle() {
        if (this == WHIRLPOOL) return "WHIRLPOOL";
        else if (this == DOUBLE_DIP) return "DOUBLE DIP";
        else return "HEAT WAVE";
    }

    @Override
    public String getDescription() {
        if (this == WHIRLPOOL) return "Pulls in all collectibles on the screen.";
        else if (this == DOUBLE_DIP) return "Collectibles give 2x points for 5 seconds.";
        else return "2x combo increase for 5 seconds.";
    }
}
