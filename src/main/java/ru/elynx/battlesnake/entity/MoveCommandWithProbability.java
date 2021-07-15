package ru.elynx.battlesnake.entity;

import lombok.Value;

@Value
public class MoveCommandWithProbability {
    MoveCommand moveCommand;
    double probability;
}
