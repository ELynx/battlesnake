package ru.elynx.battlesnake.entity;

import lombok.Value;

@Value
public class Rules {
    static String ROYALE_RULES_NAME = "royale";

    String name;
    String version;
    int timeout;

    boolean isRoyale() {
        return ROYALE_RULES_NAME.equals(name);
    }
}
