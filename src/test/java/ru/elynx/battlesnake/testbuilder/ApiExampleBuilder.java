package ru.elynx.battlesnake.testbuilder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiExampleBuilder {
    public String gameState() {
        return "{\"game\":{\"id\":\"game-00fe20da-94ad-11ea-bb37\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\",\"settings\":{\"foodSpawnChance\":25,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":5},\"squad\":{\"allowBodyCollisions\":true,\"sharedElimination\":true,\"sharedHealth\":true,\"sharedLength\":true}}},\"timeout\":500},\"turn\":14,\"board\":{\"height\":11,\"width\":11,\"food\":[{\"x\":5,\"y\":5},{\"x\":9,\"y\":0},{\"x\":2,\"y\":6}],\"hazards\":[{\"x\":3,\"y\":2}],\"snakes\":[{\"id\":\"snake-508e96ac-94ad-11ea-bb37\",\"name\":\"My Snake\",\"health\":54,\"body\":[{\"x\":0,\"y\":0},{\"x\":1,\"y\":0},{\"x\":2,\"y\":0}],\"head\":{\"x\":0,\"y\":0},\"length\":3,\"shout\":\"why are we shouting??\"},{\"id\":\"snake-b67f4906-94ae-11ea-bb37\",\"name\":\"Another Snake\",\"health\":16,\"body\":[{\"x\":5,\"y\":4},{\"x\":5,\"y\":3},{\"x\":6,\"y\":3},{\"x\":6,\"y\":2}],\"latency\":\"222\",\"head\":{\"x\":5,\"y\":4},\"length\":4,\"shout\":\"I'm not really sure...\",\"squad\":\"THIS WAS NOT IN EXAMPLE\"}]},\"you\":{\"id\":\"snake-508e96ac-94ad-11ea-bb37\",\"name\":\"My Snake\",\"health\":54,\"body\":[{\"x\":0,\"y\":0},{\"x\":1,\"y\":0},{\"x\":2,\"y\":0}],\"head\":{\"x\":0,\"y\":0},\"length\":3,\"shout\":\"why are we shouting??\"}}";
    }

    public String standardRulesetName() {
        return "standard";
    }

    public String royaleRulesetName() {
        return "royale";
    }
}
