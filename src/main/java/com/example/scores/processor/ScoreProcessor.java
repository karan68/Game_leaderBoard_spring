package com.example.scores.processor;

import com.example.scores.entity.Score;
import com.example.scores.repository.ScoreRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoreProcessor {
    private final ScoreRepository ScoreRepository;

    public ScoreProcessor(ScoreRepository ScoreRepository) {
        this.ScoreRepository = ScoreRepository;
    }
    public void processScore(String playerName, int score) {
        processScore(playerName, score, 5); // Default top 5
    }
    public void processScore(String playerName, int score, int topN) {
        List<Score> topScores = ScoreRepository.findTopNByOrderByScoreDesc(topN);
        if (topScores.size() < topN || score > topScores.get(topScores.size() - 1).getScore()) {
            Score newScore = new Score(playerName, score);
            ScoreRepository.save(newScore);
            if (topScores.size() == topN) {
                ScoreRepository.delete(topScores.get(topN - 1));
            }
        }
    }
}