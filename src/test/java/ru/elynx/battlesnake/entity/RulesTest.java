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
        Rules tested = EntityBuilder.rulesWithName(ApiExampleBuilder.royaleRulesetName());
        assertTrue(tested.isRoyale());
    }

    @Test
    void test_is_not_royale() {
        Rules tested = EntityBuilder.rulesWithName(ApiExampleBuilder.standardRulesetName());
        assertFalse(tested.isRoyale());
    }

    @Test
    void test_hazard_damage() {
        Rules tested = EntityBuilder.rulesWithName(ApiExampleBuilder.royaleRulesetName());
        assertEquals(15, tested.getHazardDamage());
    }
}
