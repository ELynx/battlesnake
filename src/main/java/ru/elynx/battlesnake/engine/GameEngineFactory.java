package ru.elynx.battlesnake.engine;

public class GameEngineFactory {
    public static IGameEngine makeGameEngine() {
        return new GameEngine();
    }
}
