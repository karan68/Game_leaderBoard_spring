package com.example.scores.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;


@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false)
    private int score;

    public Score(){

    }

    // Constructor with playerName and score
    public Score(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }
    public void updateScore(int newScore) {
        if (newScore >= 0 && newScore <= 100) {
            this.score = newScore;
        } else {
            throw new IllegalArgumentException("Score value should be between 0 and 100");
        }
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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