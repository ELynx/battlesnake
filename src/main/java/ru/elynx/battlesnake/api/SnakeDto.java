package ru.elynx.battlesnake.api;

import java.util.List;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class SnakeDto {
    @NonNull
    String id;
    @NonNull
    String name;

    @NonNull
    Integer health;

    @NonNull
    List<CoordsDto> body;

    @NonNull
    Integer latency;

    @NonNull
    CoordsDto head;
    @NonNull
    Integer length;

    @NonNull
    String shout;

    @NonNull
    String squad;
}