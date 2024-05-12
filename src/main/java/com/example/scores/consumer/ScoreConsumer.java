package com.example.scores.consumer;

import com.example.scores.processor.ScoreProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScoreConsumer {
    private final ScoreProcessor scoreProcessor;

    public ScoreConsumer(ScoreProcessor scoreProcessor) {
        this.scoreProcessor = scoreProcessor;
    }

    @KafkaListener(topics = "scores", groupId = "top-scores-group")
    public void consumeScore(String message) {
        String[] parts = message.split(",");
        String playerName = parts[0];
        int score = Integer.parseInt(parts[1]);
        scoreProcessor.processScore(playerName, score);
    }
}