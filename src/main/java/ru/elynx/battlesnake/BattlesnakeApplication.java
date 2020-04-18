package ru.elynx.battlesnake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BattlesnakeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BattlesnakeApplication.class, args);
    }
}
