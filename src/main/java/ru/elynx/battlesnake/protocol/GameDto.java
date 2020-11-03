package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class GameDto {
    @NotNull
    @NotEmpty
    private String id;
    @NotNull
    @NotEmpty
    private RulesetDto ruleset;
    @PositiveOrZero // TODO can be zero?
    private Integer timeout;

    public GameDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RulesetDto getRuleset() {
        return ruleset;
    }

    public void setRuleset(RulesetDto ruleset) {
        this.ruleset = ruleset;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
