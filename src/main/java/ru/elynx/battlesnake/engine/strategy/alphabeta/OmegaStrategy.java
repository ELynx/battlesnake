package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.math.FlagMatrix;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.engine.strategy.Common;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class OmegaStrategy implements IGameStrategy {
    private FlagMatrix occupiedPositions;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#314152", "pixel-round", "pixel-round", "not very smart");
    }

    @Override
    public void init(HazardPredictor hazardPredictor) {
        occupiedPositions = FlagMatrix.uninitializedMatrix(hazardPredictor.getGameState().getBoard().getDimensions(),
                true);
    }

    @Override
    public Void processStart(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        occupiedPositions.unsetAll();

        GameState gameState = hazardPredictor.getGameState();
        Common.forAllSnakeBodies(gameState, coordinates -> occupiedPositions.set(coordinates));

        ScoreMaker scoreMaker = new ScoreMaker(gameState.getYou(), gameState);

        Coordinates head = gameState.getYou().getHead();
        List<Pair<Coordinates, MoveCommand>> moves = List.of(new Pair<>(head.move(DOWN), DOWN),
                new Pair<>(head.move(LEFT), LEFT), new Pair<>(head.move(RIGHT), RIGHT), new Pair<>(head.move(UP), UP));

        MoveCommand moveCommand = moves.stream().filter(pair -> !occupiedPositions.isSet(pair.getValue0()))
                .sorted(Comparator
                        .comparingInt((Pair<Coordinates, MoveCommand> pair) -> scoreMaker.scoreMove(pair.getValue0()))
                        .reversed())
                .map(Pair::getValue1).findFirst().orElse(REPEAT_LAST);

        return new Move(moveCommand);
    }

    @Override
    public Void processEnd(HazardPredictor hazardPredictor) {
        return null;
    }

    @Configuration
    public static class OmegaStrategyConfiguration {
        @Bean("Pixel")
        public Supplier<IGameStrategy> omega() {
            return OmegaStrategy::new;
        }
    }
}
