package ru.elynx.battlesnake.engine;

import org.springframework.stereotype.Service;

@Service
public class GameEngineFactory implements IGameEngineFactory {
    public GameEngine makeGameEngine() {
        return new GameEngine();
    }
}
