package ru.elynx.battlesnake.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Value
public class Move {
    MoveCommand command;
    String shout = null;
}
