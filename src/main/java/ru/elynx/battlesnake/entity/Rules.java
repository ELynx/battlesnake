package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public class Rules {
    private static final String ROYALE_RULES_NAME = "royale";
    private static final int ROYALE_HAZARD_DAMAGE = 15;

    @NonNull
    String name;
    @NonNull
    String version;
    int timeout;

    public boolean isRoyale() {
        return ROYALE_RULES_NAME.equals(name);
    }

    public int getRoyaleHazardDamage() {
        return ROYALE_HAZARD_DAMAGE;
    }
}
