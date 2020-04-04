package ru.elynx.battlesnake.engine;

import org.springframework.stereotype.Service;

@Service
public class GameStrategyFactory implements IGameStrategyFactory {
    public GameStrategy makeGameStrategy() {
        return new GameStrategy();
    }
}
