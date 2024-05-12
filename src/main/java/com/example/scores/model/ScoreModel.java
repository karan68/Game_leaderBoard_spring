package com.example.scores.model;

public class ScoreModel {

    private String playerName;
    private int score;

    public ScoreModel(String s, int i) {
    }

    // Getters and setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

