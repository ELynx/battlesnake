package ru.elynx.battlesnake.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MoveDto {
    @NonNull
    String move;
    String shout;
}
