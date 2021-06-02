package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public class Rules {
    static String ROYALE_RULES_NAME = "royale";

    @NonNull
    String name;
    @NonNull
    String version;
    int timeout;

    // TODO unit test
    public boolean isRoyale() {
        return ROYALE_RULES_NAME.equals(name);
    }
}
