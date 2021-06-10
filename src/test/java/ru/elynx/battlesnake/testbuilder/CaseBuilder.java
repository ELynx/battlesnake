package ru.elynx.battlesnake.testbuilder;

import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;

public class CaseBuilder {
    private static final String ROYALE_RULES_NAME = "royale";

    private CaseBuilder() {
    }

    public static HazardPredictor empty_space_better_than_snake() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___A_______\n" + //
                "___aaa_yyy_\n" + //
                "_____BbY_y_\n" + //
                "______b_yy_\n" + //
                "____bbb_y__\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n");

        return generator.build();
    }

    public static HazardPredictor avoid_fruit_surrounded_by_snake() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "yyyv<______\n" + //
                "y0^<^______\n" + //
                "yY__y______\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n").setHealth("Y", 2);

        return generator.build();
    }

    public static HazardPredictor avoid_fruit_in_corner_easy() {
        // easy because there is no way out if entering fruit corner
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_________yY\n" + //
                "____yyyyyy0\n").setHealth("Y", 2);

        return generator.build();
    }

    public static HazardPredictor avoid_fruit_in_corner_hard() {
        // hard because it is necessary to predict that growth would close the exit
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_________yY\n" + //
                "_________y0\n").setHealth("Y", 2);

        return generator.build();
    }

    public static HazardPredictor dont_die_for_food() {
        // head to head even with snake of same length is lose
        // health is left at max to avoid starvation rage
        // more of way to prevent greedy grab from under the train
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____y\n" + //
                "____y\n" + //
                "____Y\n" + //
                "____0\n" + //
                "____A\n" + //
                "____a\n" + //
                "____a\n");

        return generator.build();
    }

    public static HazardPredictor dont_die_for_food_and_hunt() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____y____\n" + //
                "____y____\n" + //
                "____Y____\n" + //
                "__bB0Cc__\n" + //
                "____A____\n" + //
                "____a____\n" + //
                "____a____\n").setHealth("Y", 2);

        return generator.build();
    }

    public static HazardPredictor dont_give_up() {
        // given no food spawns, tail will clear out the passage out in 5 turns
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____Y\n" + //
                ">>>>^\n" + //
                "^<<<_\n" + //
                "_____\n");

        return generator.build();
    }

    public static HazardPredictor eat_in_hazard() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "_____0_____\n" + //
                "___A_____0_\n" + //
                "__v^______0\n" + //
                "_v<^_______\n" + //
                "_>>^_bB____\n" + //
                "__bbv^_>v0_\n" + //
                "_bb_>^_^v0Y\n" + //
                "_b_____^v_y\n" + //
                "_______^v_y\n" + //
                "_____0__y_y\n" + //
                "__0_____yyy\n");

        generator.setRulesetName(ROYALE_RULES_NAME);
        generator.setTurn(122);
        generator.setHealth("A", 64);
        generator.setHealth("B", 52);
        generator.setHealth("Y", 69);
        generator.setLatency("A", 38);
        generator.setLatency("B", 16);
        generator.setLatency("Y", 77);
        generator.setHazards("" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n" + //
                "HH_______HH\n");

        return generator.build();
    }

    public static HazardPredictor sees_the_inevitable() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "_________c_\n" + //
                "_________c_\n" + //
                "_________vC\n" + //
                "_________>^\n" + //
                "____a_aaaaA\n" + //
                "0___aaayyY_\n" + //
                "____yyyy___\n" + //
                "____y______\n" + //
                "___yy______\n");

        generator.setTurn(106);
        generator.setHealth("A", 99);
        generator.setHealth("C", 86);
        generator.setHealth("Y", 95);
        generator.setLatency("A", 81);
        generator.setLatency("C", 58);
        generator.setLatency("Y", 66);

        return generator.build();
    }

    public static HazardPredictor does_not_go_into_hazard_lake() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "y________W<\n" + //
                "yy__wwwww>^\n" + //
                "_y_________\n" + //
                "yy_________\n" + //
                "y__________\n" + //
                "Y__________\n" + //
                "_v<<<______\n" + //
                "_>>>>xX____\n" + //
                "0______0___\n");

        generator.setRulesetName(ROYALE_RULES_NAME);
        generator.setTurn(55);
        generator.setHealth("Y", 90);
        generator.setHealth("W", 99);
        generator.setHealth("X", 85);
        generator.setLatency("Y", 83);
        generator.setLatency("W", 72);
        generator.setLatency("X", 175);
        generator.setHazards("" + //
                "HHHHHHHHHHH\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "HHHHHHHHHHH\n");

        return generator.build();
    }

    public static HazardPredictor sees_escape_route() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_v<<_____0_\n" + //
                "v<_^______v\n" + //
                ">>A^____B<v\n" + //
                "___^____>^v\n" + //
                "yyY^____^<<\n" + //
                "y_>^_______\n" + //
                "yyyyy______\n");

        generator.setTurn(106);
        generator.setHealth("A", 97);
        generator.setHealth("B", 93);
        generator.setHealth("Y", 78);
        generator.setLatency("A", 91);
        generator.setLatency("B", 59);
        generator.setLatency("Y", 86);

        return generator.build();
    }

    // same as above but with added fantasy about other snake's options
    public static HazardPredictor sees_escape_route_plus() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_v<<_____0_\n" + //
                "v<_^<_____v\n" + //
                ">>A_^___B<v\n" + //
                "___>^___>^v\n" + //
                "yyY^____^<<\n" + //
                "y_>^_______\n" + //
                "yyyyy______\n");

        generator.setTurn(106);
        generator.setHealth("A", 97);
        generator.setHealth("B", 93);
        generator.setHealth("Y", 78);
        generator.setLatency("A", 91);
        generator.setLatency("B", 59);
        generator.setLatency("Y", 86);

        return generator.build();
    }

    public static HazardPredictor hazard_better_than_lose() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "_____0_____\n" + //
                "__0____y___\n" + //
                "yyyy__yy___\n" + //
                "yv<yyyy____\n" + //
                "yv^<_______\n" + //
                "yA_^<______\n" + //
                "Y__>^______\n" + //
                "___a_______\n" + //
                "___aa____a_\n" + //
                "0___aaaa_a_\n" + //
                "_______aaa_\n");

        generator.setRulesetName(ROYALE_RULES_NAME);
        generator.setTurn(174);
        generator.setHealth("A", 89);
        generator.setHealth("Y", 97);
        generator.setLatency("A", 31);
        generator.setLatency("Y", 84);
        generator.setHazards("" + //
                "HHHHHHHHHHH\n" + //
                "HHHHHHHHHHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "________HHH\n" + //
                "HHHHHHHHHHH\n");

        return generator.build();
    }

    public static HazardPredictor does_not_corner_self() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "________00_\n" + //
                "________aa_\n" + //
                "____0_B__a_\n" + //
                "______b__a_\n" + //
                "_____bb__a_\n" + //
                "____bb___a_\n" + //
                "_________a_\n" + //
                "yyyy_____a_\n" + //
                "y__yy____a_\n" + //
                "____yy___A_\n" + //
                "_____yyyY__\n");

        generator.setTurn(74);
        generator.setHealth("A", 90);
        generator.setHealth("B", 63);
        generator.setHealth("Y", 95);
        generator.setLatency("A", 22);
        generator.setLatency("B", 85);
        generator.setLatency("Y", 87);

        return generator.build();
    }
}
