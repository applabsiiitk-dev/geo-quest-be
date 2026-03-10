package com.applabs.geo_quest.exception;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(String teamId) {
        super("Team not found: " + teamId);
    }
}
