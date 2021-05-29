package ru.elynx.battlesnake.protocol;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class CoordsDto {
    @NotNull
    @PositiveOrZero
    private Integer x;
    @NotNull
    @PositiveOrZero
    private Integer y;

    public CoordsDto() {
    }

    public CoordsDto(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    // TODO unit test
    public CoordsDto plus(Move.MovesEnum move) {
        switch (move) {
            case UP :
                return new CoordsDto(getX(), getY() + 1);
            case RIGHT :
                return new CoordsDto(getX() + 1, getY());
            case DOWN :
                return new CoordsDto(getX(), getY() - 1);
            case LEFT :
                return new CoordsDto(getX() - 1, getY());
            default :
                throw new IllegalArgumentException("Move [" + move + "] is not a supported direction");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CoordsDto coordsDto = (CoordsDto) o;
        return x.equals(coordsDto.x) && y.equals(coordsDto.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "CoordsDto{" + x + ", " + y + '}';
    }
}
