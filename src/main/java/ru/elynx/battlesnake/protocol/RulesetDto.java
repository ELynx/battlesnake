package ru.elynx.battlesnake.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RulesetDto {
    public static final String ROYALE_RULESET_NAME = "royale";

    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isRoyale() {
        return ROYALE_RULESET_NAME.equalsIgnoreCase(getName());
    }

    @Override
    public String toString() {
        return "RulesetDto{name='" + name + "', version='" + version + "'}";
    }
}
