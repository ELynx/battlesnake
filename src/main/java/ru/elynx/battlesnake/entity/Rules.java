package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public class Rules {
    private static final String ROYALE_RULES_NAME = "royale";

    @NonNull
    String name;
    @NonNull
    String version;
    int timeout;

    public boolean isRoyale() {
        return ROYALE_RULES_NAME.equals(name);
    }
}
