package com.example.scores;

import com.example.scores.entity.Score;
import com.example.scores.repository.ScoreRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.util.Random;

@SpringBootApplication
public class ScoresApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScoresApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ScoreRepository ScoreRepository) throws Exception {
        Faker faker = new Faker();

        return (String[] args) -> {
            for (int i = 0; i < 100; i++) {
                Random ran = new Random();
                int x = ran.nextInt(100);
                String name = faker.name().fullName();
                Score userR = new Score(name, x);
                ScoreRepository.save(userR);
            }
            ScoreRepository.findAll().forEach(user -> System.out.println(user));
        };
    }
}