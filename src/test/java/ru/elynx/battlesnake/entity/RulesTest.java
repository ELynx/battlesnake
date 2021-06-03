package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.testbuilder.ApiExampleBuilder;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("Internals")
class RulesTest {
    @Test
    void test_is_royale() {
        Rules tested1 = EntityBuilder.rulesWithName(ApiExampleBuilder.royaleRulesetName());
        assertTrue(tested1.isRoyale());

        Rules tested2 = EntityBuilder.rulesWithName(ApiExampleBuilder.standardRulesetName());
        assertFalse(tested1.isRoyale());
    }
}
