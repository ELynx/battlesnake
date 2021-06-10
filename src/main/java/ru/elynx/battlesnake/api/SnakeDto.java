package ru.elynx.battlesnake.api;

import java.util.List;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SnakeDto {
    @NonNull
    String id;
    @NonNull
    String name;

    @NonNull
    Integer health;

    @NonNull
    List<CoordsDto> body;

    Integer latency;

    @NonNull
    CoordsDto head;
    @NonNull
    Integer length;

    String shout;

    String squad;
}
