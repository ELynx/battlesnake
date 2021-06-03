package ru.elynx.battlesnake.api;

import java.util.List;
import lombok.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BoardDto {
    @NonNull
    Integer height;
    @NonNull
    Integer width;

    @NonNull
    List<CoordsDto> food;
    @NonNull
    List<CoordsDto> hazards;
    @NonNull
    List<SnakeDto> snakes;
}
