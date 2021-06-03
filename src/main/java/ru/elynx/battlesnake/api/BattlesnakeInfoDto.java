package ru.elynx.battlesnake.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BattlesnakeInfoDto {
    @NonNull
    String apiversion;

    String author;

    String color;
    String head;
    String tail;

    String version;
}