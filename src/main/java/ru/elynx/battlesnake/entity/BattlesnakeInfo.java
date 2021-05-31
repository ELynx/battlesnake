package ru.elynx.battlesnake.entity;

import lombok.Value;

@Value
public class BattlesnakeInfo {
    String author;

    String color;
    String head;
    String tail;

    String version;
}
