package ru.elynx.battlesnake.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Value
public class Move {
    @NonNull
    MoveCommand moveCommand;
    String shout; // TODO nullable
}