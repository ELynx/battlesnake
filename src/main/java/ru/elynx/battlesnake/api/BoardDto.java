package ru.elynx.battlesnake.api;

import java.util.List;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
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
