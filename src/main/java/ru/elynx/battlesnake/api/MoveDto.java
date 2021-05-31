package ru.elynx.battlesnake.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Value
public class MoveDto {
    @NonNull
    String move;
    String shout;
}
