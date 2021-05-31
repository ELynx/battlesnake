package ru.elynx.battlesnake.api;

import lombok.*;

@Value
public class BattlesnakeInfoDto {
    @NonNull
    String apiversion;

    String author;

    String color;
    String head;
    String tail;

    String version;
}
