package ru.elynx.battlesnake.webserver;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    @PostMapping("/start")
    public String start(String argument) {
        return "{ \"color\": \"#ffbf00\", \"headType\": \"smile\", \"tailType\": \"regular\" }";
    }

    @PostMapping("/move")
    public String move(String argument) {
        return "{ \"move\": \"up\", \"shout\": \"1% ready\" }";
    }

    @PostMapping("/end")
    public String end(String argument) {
        return "";
    }

    @PostMapping("/ping")
    public String ping(String argument) {
        return "";
    }
}
