package com.distraction.jetsetgo;

public enum Passive implements Perk {
    MAIN_ATTRACTION, // collectibles in 3m radius are sucked in
    CHAIN_REACTION, // combo timer is 4 seconds instead of 2
    SPEEDO_MODE, // +50% max speed
    SURF_STEERING, // +200% turn speed
    SUMMER_HOURS, // +5 seconds
    ;

    @Override
    public String getName() {
        if (this == MAIN_ATTRACTION) return "mainattraction";
        else if (this == CHAIN_REACTION) return "chainreaction";
        else if (this == SPEEDO_MODE) return "speedomode";
        else if (this == SURF_STEERING) return "surfsteering";
        else return "summerhours";
    }

    @Override
    public String getTitle() {
        if (this == MAIN_ATTRACTION) return "MAIN ATTRACTION";
        else if (this == CHAIN_REACTION) return "CHAIN REACTION";
        else if (this == SPEEDO_MODE) return "SPEEDO MODE";
        else if (this == SURF_STEERING) return "SURF STEERING";
        else return "SUMMER HOURS";
    }

    @Override
    public String getDescription() {
        if (this == MAIN_ATTRACTION) return "Collectibles are pulled to you within 3 meters.";
        else if (this == CHAIN_REACTION) return "Combo timer is increased to 4 seconds.";
        else if (this == SPEEDO_MODE) return "+50% max speed.";
        else if (this == SURF_STEERING) return "+200% turn speed.";
        else return "+7 seconds to run.";
    }
}
