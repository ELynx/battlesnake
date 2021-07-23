package ru.elynx.battlesnake.testbuilder;

import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.entity.Board;
import ru.elynx.battlesnake.entity.BoardWithActiveHazards;
import ru.elynx.battlesnake.entity.GameState;

public class CaseBuilder {
    private CaseBuilder() {
    }

    public static GameState empty_space_better_than_snake() {
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

    public static GameState avoid_fruit_surrounded_by_snake_2_hp() {
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

    public static GameState avoid_fruit_surrounded_by_snake_10_hp() {
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
                "___________\n").setHealth("Y", 10);

        return generator.build();
    }

    public static GameState avoid_fruit_in_corner_easy_2_health() {
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

    public static GameState avoid_fruit_in_corner_hard_2_health() {
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
                "________yy0\n").setHealth("Y", 2);

        return generator.build();
    }

    public static GameState avoid_fruit_in_corner_easy_10_health() {
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
                "____yyyyyy0\n").setHealth("Y", 10);

        return generator.build();
    }

    public static GameState avoid_fruit_in_corner_hard_10_health() {
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
                "________yy0\n").setHealth("Y", 10);

        return generator.build();
    }

    public static GameState dont_die_for_food() {
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

    public static GameState dont_die_for_food_flip() {
        // head to head even with snake of same length is lose
        // health is left at max to avoid starvation rage
        // more of way to prevent greedy grab from under the train
        AsciiToGameState generator = new AsciiToGameState("" + //
                "a____\n" + //
                "a____\n" + //
                "A____\n" + //
                "0____\n" + //
                "Y____\n" + //
                "y____\n" + //
                "y____\n");

        return generator.build();
    }

    public static GameState dont_die_for_food_and_hunt() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____y____\n" + //
                "____y____\n" + //
                "____Y____\n" + //
                "__bB0Cc__\n" + //
                "____A____\n" + //
                "____a____\n" + //
                "____a____\n");

        return generator.build();
    }

    public static GameState dont_die_for_food_and_hunt_flip() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____a____\n" + //
                "____a____\n" + //
                "____A____\n" + //
                "__cC0Bb__\n" + //
                "____Y____\n" + //
                "____y____\n" + //
                "____y____\n");

        return generator.build();
    }

    public static GameState dont_give_up() {
        // given no food spawns, tail will clear out the passage out in 5 turns
        AsciiToGameState generator = new AsciiToGameState("" + //
                "____Y\n" + //
                ">>>>^\n" + //
                "^<<<_\n" + //
                "_____\n");

        return generator.build();
    }

    public static GameState eat_in_hazard() {
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

    public static GameState sees_the_inevitable() {
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

    public static GameState does_not_go_into_hazard_lake() {
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

    public static GameState sees_escape_route() {
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
    public static GameState sees_escape_route_plus() {
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

    public static GameState hazard_better_than_lose() {
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

    public static GameState does_not_corner_self() {
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

    public static GameState avoid_lock_1() {
        // original configuration had ambiguity for B
        // if B moved downwards, it would lock Y as well
        // this depended on what choice `other` component would make
        // current case is modified to cut off B from going down
        // original B
        // ___bb_bB___
        // ____bbb____
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_________>v\n" + //
                "_________^v\n" + //
                "__________A\n" + //
                "___bb__B___\n" + //
                "____bbbb>>Y\n" + //
                "________^<_\n");

        return generator.build();
    }

    public static GameState avoid_lock_2() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "__________a\n" + //
                "__________a\n" + //
                "__________a\n" + //
                "__Aaaaaaaaa\n" + //
                "___________\n" + //
                "Yyyy_______\n" + //
                "___y_______\n" + //
                "__yy_______\n" + //
                "yyy________\n");

        return generator.build();
    }

    public static GameState can_handle_meta_information() {
        AsciiToGameState generator0 = new AsciiToGameState("" + //
                "___________\n" + //
                "______Y____\n" + //
                "______y____\n" + //
                "______y____\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n").setHealth("Y", 99).setTurn(49);

        generator0.setHazards("" + //
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
                "___________\n");

        GameState gameState0 = generator0.build();

        AsciiToGameState generator1 = new AsciiToGameState("" + //
                "___________\n" + //
                "_____Yy____\n" + //
                "______y____\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n").setHealth("Y", 98).setTurn(50);

        generator1.setHazards("" + //
                "HHHHHHHHHHH\n" + //
                "HHHHHHHHHHH\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n");

        GameState gameState1 = generator1.build();

        Board boardWithMeta = BoardWithActiveHazards.fromAdjacentTurns(gameState0.getBoard(), gameState1.getBoard());
        assert (boardWithMeta instanceof BoardWithActiveHazards);
        assert (boardWithMeta.getHazards().size() == 22);
        assert (boardWithMeta.getActiveHazards().size() == 11);

        return new GameState(gameState1.getGameId(), gameState1.getTurn(), gameState1.getRules(), boardWithMeta,
                gameState1.getYou());
    }

    public static GameState eat_food_immediately() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "_____rrrrr_\n" + //
                "__y______R_\n" + //
                "__>>v_Skkk_\n" + //
                "__0Y<_s__K_\n" + //
                "______s____\n" + //
                "_____ss____\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n").setRulesetName(ApiExampleBuilder.royaleRulesetName());

        generator.setTurn(18);
        generator.setHealth("K", 84);
        generator.setHealth("R", 99);
        generator.setHealth("S", 94);
        generator.setHealth("Y", 93);
        generator.setLatency("K", 144);
        generator.setLatency("R", 93);
        generator.setLatency("S", 4);
        generator.setLatency("Y", 79);

        return generator.build();
    }

    public static GameState eat_food_and_conquer_in_two_turns() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "_s_________\n" + //
                "_ss_r______\n" + //
                "__ssr______\n" + //
                "_0_Sr______\n" + //
                "____rrR___0\n" + //
                "_Y_____K___\n" + //
                "_y_____k___\n" + //
                "_y_____k___\n" + //
                "_^vy___k___\n" + //
                "_^<____k___\n").setHazards("" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n" + //
                        "H__________\n");

        generator.setTurn(39);
        generator.setHealth("K", 84);
        generator.setHealth("R", 78);
        generator.setHealth("S", 94);
        generator.setHealth("Y", 96);
        generator.setLatency("K", 122);
        generator.setLatency("R", 90);
        generator.setLatency("S", 4);
        generator.setLatency("Y", 78);

        return generator.build();
    }

    public static GameState attempt_on_enemy_life() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "____v<_____\n" + //
                "__S<v^<____\n" + //
                "0_>^>v_____\n" + //
                "__s__r_____\n" + //
                "0_ss_R_____\n" + //
                "_0_s__Y____\n" + //
                "___syyy____\n" + //
                "___sy______\n" + //
                "___sy_y____\n" + //
                "____yyy____\n").setHazards("" + //
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

        generator.setTurn(96);
        generator.setHealth("R", 47);
        generator.setHealth("S", 72);
        generator.setHealth("Y", 76);
        generator.setLatency("R", 92);
        generator.setLatency("S", 5);
        generator.setLatency("Y", 77);

        return generator.build();
    }

    public static GameState dont_step_under_adversary_1() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "__e________\n" + //
                "__e__o_____\n" + //
                "__eE_o_____\n" + //
                "_____o_____\n" + //
                "_____O___R_\n" + //
                "____Y____r_\n" + //
                "____y_0__r_\n" + //
                "____y____r_\n" + //
                "____y______\n" + //
                "___________\n").setRulesetName(ApiExampleBuilder.royaleRulesetName());

        generator.setTurn(8);
        generator.setLength("O", 5);
        generator.setHealth("O", 100);
        generator.setHealth("R", 94);
        generator.setHealth("E", 94);
        generator.setHealth("Y", 94);
        generator.setLatency("O", 257);
        generator.setLatency("R", 98);
        generator.setLatency("E", 79);
        generator.setLatency("Y", 91);

        return generator.build();
    }

    public static GameState dont_step_under_adversary_2() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "_____ggg___\n" + //
                "_____G_____\n" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                "_____0_____\n" + //
                "___yyy__Rr_\n" + //
                "_____Y__rr_\n" + //
                "______A____\n" + //
                "______aa___\n" + //
                "_______a___\n").setRulesetName(ApiExampleBuilder.royaleRulesetName());

        generator.setTurn(8);
        generator.setLength("A", 5);
        generator.setHealth("A", 100);
        generator.setHealth("R", 96);
        generator.setHealth("G", 96);
        generator.setHealth("Y", 96);
        generator.setLatency("A", 68);
        generator.setLatency("R", 416);
        generator.setLatency("G", 262);
        generator.setLatency("Y", 78);

        return generator.build();
    }

    public static GameState dont_go_into_hazard() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "_00________\n" + //
                "00_________\n" + //
                "___________\n" + //
                "______Yy___\n" + //
                "___G<<<yy_0\n" + //
                "__>>>>^0^<<\n" + //
                ">>^_____>>^\n" + //
                "^<_____yy__\n" + //
                "___v>yyy___\n" + //
                "___v^_____0\n" + //
                "___>^____0_\n").setHazards("" + //
                        "HHHHHHHHHHH\n" + //
                        "HHHHHHHHHHH\n" + //
                        "HHHHHHHHHHH\n" + //
                        "HH________H\n" + //
                        "HH________H\n" + //
                        "HH________H\n" + //
                        "HH________H\n" + //
                        "HH________H\n" + //
                        "HH________H\n" + //
                        "HH________H\n" + //
                        "HHHHHHHHHHH\n");

        generator.setTurn(155);
        generator.setHealth("G", 92);
        generator.setHealth("Y", 63);
        generator.setLatency("G", 235);
        generator.setLatency("Y", 85);

        return generator.build();
    }

    public static GameState go_out_of_hazard_fast() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___Y<<<<___\n" + //
                "0_>>>>v^___\n" + //
                "__^v__>^___\n" + //
                "__^<_______\n" + //
                "_______>v__\n" + //
                "_____v<^G_0\n" + //
                "_____>>^___\n" + //
                "__________0\n" + //
                "___________\n" + //
                "___________\n").setHazards("" + //
                        "HHHHHHHHHHH\n" + //
                        "HHHHHHHHHHH\n" + //
                        "H_______HHH\n" + //
                        "H_______HHH\n" + //
                        "H_______HHH\n" + //
                        "H_______HHH\n" + //
                        "H_______HHH\n" + //
                        "H_______HHH\n" + //
                        "H_______HHH\n" + //
                        "H_______HHH\n" + //
                        "HHHHHHHHHHH\n");

        generator.setTurn(152);
        generator.setHealth("G", 27);
        generator.setHealth("Y", 85);
        generator.setLatency("G", 421);
        generator.setLatency("Y", 83);

        return generator.build();
    }

    public static GameState do_not_lock_two_adversaries() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___v<<___0_\n" + //
                "___>>v_____\n" + //
                "v<___v_____\n" + //
                ">>H_G<_____\n" + //
                "_Y_yy______\n" + //
                "_yyy_______\n" + //
                "___________\n" + //
                "___>v______\n" + //
                "___^>N_____\n" + //
                "___n_______\n" + //
                "___n_____0_\n").setHazards("" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n");

        generator.setTurn(51);
        generator.setHealth("G", 96);
        generator.setHealth("H", 82);
        generator.setHealth("N", 89);
        generator.setHealth("Y", 82);
        generator.setLatency("G", 96);
        generator.setLatency("H", 366);
        generator.setLatency("N", 400);
        generator.setLatency("Y", 79);

        return generator.build();
    }

    public static GameState attack_two_adversaries() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___v<____0_\n" + //
                "___>>v_____\n" + //
                "v_H__v_____\n" + //
                ">>^_v<_____\n" + //
                "_>AvY______\n" + //
                "_^<<_______\n" + //
                "___________\n" + //
                "___>v______\n" + //
                "___^>v_____\n" + //
                "___n_N_____\n" + //
                "_________0_\n").setHazards("" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n");

        generator.setTurn(51);
        generator.setHealth("Y", 96);
        generator.setHealth("H", 82);
        generator.setHealth("N", 89);
        generator.setHealth("A", 82);
        generator.setLatency("Y", 96);
        generator.setLatency("H", 366);
        generator.setLatency("N", 400);
        generator.setLatency("A", 79);

        return generator.build();
    }

    public static GameState self_lock_1() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "_______Gg__\n" + //
                "____0_Y_gg_\n" + //
                "0_>v__yy_g_\n" + //
                "__^>H__^<g_\n" + //
                "__h____>^__\n" + //
                "__h____y___\n" + //
                "__h________\n" + //
                "__h___0____\n" + //
                "__hh_______\n" + //
                "___________\n" + //
                "___________\n").setHazards("" + //
                        "HHHHHHHHHHH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n" + //
                        "_________HH\n");

        generator.setTurn(63);
        generator.setHealth("Y", 94);
        generator.setHealth("H", 95);
        generator.setHealth("G", 85);
        generator.setLatency("Y", 78);
        generator.setLatency("H", 370);
        generator.setLatency("G", 217);

        return generator.build();
    }

    public static GameState self_lock_2() {
        AsciiToGameState generator = new AsciiToGameState("" + //
                "___________\n" + //
                "___________\n" + //
                "___________\n" + //
                ">>Y________\n" + //
                "^<_A_____M0\n" + //
                "_^<a_mmmmm_\n" + //
                "0aaa_______\n" + //
                "_a_________\n" + //
                "_a_________\n" + //
                "_a_________\n" + //
                "_a_________\n").setHazards("" + //
                        "HHHHHHHHHHH\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n" + //
                        "__________H\n");

        generator.setTurn(47);
        generator.setHealth("Y", 93);
        generator.setHealth("A", 94);
        generator.setHealth("M", 85);
        generator.setLatency("Y", 79);
        generator.setLatency("A", 202);
        generator.setLatency("M", 187);

        return generator.build();
    }
}
