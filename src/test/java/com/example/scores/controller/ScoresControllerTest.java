package com.example.scores.controller;

import com.example.scores.entity.Score;
import com.example.scores.model.ScoreModel;
import com.example.scores.repository.ScoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ScoresController.class)
public class ScoresControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScoreRepository scoreRepository;

    @Test
    public void testGetTopScores() throws Exception {
        // Arrange
        Score score1 = new Score("Player 1", 100);
        Score score2 = new Score("Player 2", 90);
        Score score3 = new Score("Player 3", 80);
        List<Score> topScores = Arrays.asList(score1, score2, score3);
        when(scoreRepository.findTopNByOrderByScoreDesc(3)).thenReturn(topScores);

        // Act and Assert
        mockMvc.perform(get("/game/topscores?n=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerName").value("Player 1"))
                .andExpect(jsonPath("$[0].score").value(100))
                .andExpect(jsonPath("$[1].playerName").value("Player 2"))
                .andExpect(jsonPath("$[1].score").value(90))
                .andExpect(jsonPath("$[2].playerName").value("Player 3"))
                .andExpect(jsonPath("$[2].score").value(80));
    }

    @Test
    public void testAddScore() throws Exception {
        // Arrange
        ScoreModel scoreModel = new ScoreModel("Player 4", 75);

        // Act and Assert
        mockMvc.perform(post("/game/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerName\":\"Player 4\",\"score\":75}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetScoresPagination() throws Exception {
        // Arrange
        // Mocking the scores returned by the repository
        Score score1 = new Score("bhanu", 100);
        Score score2 = new Score("Catherine Gottlieb", 99);
        Score score3 = new Score("Adonis Jerde", 98);
        Score score4 = new Score("Francesca Cormier", 96);
        Score score5 = new Score("Maximo MacGyver", 96);
        List<Score> scores = Arrays.asList(score1, score2, score3, score4, score5);
        Page<Score> scorePage = new PageImpl<>(scores);

        when(scoreRepository.findAllByOrderByScoreDesc(any())).thenReturn(scorePage);

        // Act and Assert
        mockMvc.perform(get("/game/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.scoreList", hasSize(5)))
                .andExpect(jsonPath("$._embedded.scoreList[0].playerName").value("bhanu"))
                .andExpect(jsonPath("$._embedded.scoreList[0].score").value(100))
                .andExpect(jsonPath("$._embedded.scoreList[1].playerName").value("Catherine Gottlieb"))
                .andExpect(jsonPath("$._embedded.scoreList[1].score").value(99))
                .andExpect(jsonPath("$._embedded.scoreList[2].playerName").value("Adonis Jerde"))
                .andExpect(jsonPath("$._embedded.scoreList[2].score").value(98))
                .andExpect(jsonPath("$._embedded.scoreList[3].playerName").value("Francesca Cormier"))
                .andExpect(jsonPath("$._embedded.scoreList[3].score").value(96))
                .andExpect(jsonPath("$._embedded.scoreList[4].playerName").value("Maximo MacGyver"))
                .andExpect(jsonPath("$._embedded.scoreList[4].score").value(96))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }



    @Test
    public void testGetTopScoresEmpty() throws Exception {
        // Arrange
        when(scoreRepository.findTopNByOrderByScoreDesc(anyInt())).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(get("/game/topscores?n=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testInvalidInput() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/game/topscores?n=-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddScoreEmptyBody() throws Exception {
        // Act and Assert
        mockMvc.perform(post("/game/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEmptyDatabase() throws Exception {
        // Arrange
        when(scoreRepository.findAllByOrderByScoreDesc(any())).thenReturn(Page.empty());

        // Act and Assert
        mockMvc.perform(get("/game/scores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist());
    }

    @Test
    public void testLargePageRequest() throws Exception {
        // Arrange
        int largePageSize = 1000;

        // Act and Assert
        mockMvc.perform(get("/game/scores?page=0&size=" + largePageSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(lessThanOrEqualTo(largePageSize)));
    }

    @Test
    public void testInvalidPageRequest() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/game/scores?page=-1&size=5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPaginationLinks() throws Exception {
        // Arrange
        int pageNumber = 1;
        int pageSize = 5;

        // Act and Assert
        mockMvc.perform(get("/game/scores?page=" + pageNumber + "&size=" + pageSize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/game/scores?page=" + pageNumber + "&size=" + pageSize));

    }

    @Test
    public void testInsertInvalidScores() throws Exception {
        // Arrange
        String requestBodyOverLimit = "{\"playerName\":\"Player\", \"score\":101}";
        String requestBodyUnderLimit = "{\"playerName\":\"Player\", \"score\":-10}";

        // Act and Assert
        // Test for score greater than 1000
        mockMvc.perform(post("/game/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyOverLimit))
                .andExpect(status().isBadRequest());

        // Test for score less than 0
        mockMvc.perform(post("/game/player")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyUnderLimit))
                .andExpect(status().isBadRequest());
    }




    @Test
    public void testErrorHandling() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/game/scores"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testUpdateScore() throws Exception {
        // Arrange
        Long id = 1L;
        int newScore = 85;
        Score score = new Score("Player 1", 80);
        when(scoreRepository.existsById(id)).thenReturn(true);
        when(scoreRepository.findById(id)).thenReturn(java.util.Optional.of(score));

        // Act and Assert
        mockMvc.perform(put("/game/scores/{id}", id)
                        .param("newScore", String.valueOf(newScore)))
                .andExpect(status().isOk())
                .andExpect(content().string("Score updated successfully"));
    }

    @Test
    public void testUpdateScore_NotFound() throws Exception {
        // Arrange
        Long id = 1L;
        int newScore = 85;
        when(scoreRepository.existsById(id)).thenReturn(false);

        // Act and Assert
        mockMvc.perform(put("/game/scores/{id}", id)
                        .param("newScore", String.valueOf(newScore)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testGetScoreById() throws Exception {
        // Arrange
        Long id = 1L;
        Score score = new Score("Player 1", 80);
        when(scoreRepository.existsById(id)).thenReturn(true);
        when(scoreRepository.findById(id)).thenReturn(java.util.Optional.of(score));

        // Act and Assert
        mockMvc.perform(get("/game/scores/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("Player 1"))
                .andExpect(jsonPath("$.score").value(80));
    }
    @Test
    public void testGetScoreById_NotFound() throws Exception {
        // Arrange
        Long id = 1L;
        when(scoreRepository.existsById(id)).thenReturn(false);

        // Act and Assert
        mockMvc.perform(get("/game/scores/{id}", id))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testDeletePlayerAndScore() throws Exception {
        // Arrange
        Long id = 1L;
        when(scoreRepository.existsById(id)).thenReturn(true);

        // Act and Assert
        mockMvc.perform(delete("/game/scores/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Player and score deleted successfully"));
    }

    @Test
    public void testDeletePlayerAndScore_NotFound() throws Exception {
        // Arrange
        Long id = 1L;
        when(scoreRepository.existsById(id)).thenReturn(false);

        // Act and Assert
        mockMvc.perform(delete("/game/scores/{id}", id))
                .andExpect(status().isNotFound());
    }
}