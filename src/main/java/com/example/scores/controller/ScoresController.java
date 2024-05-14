package com.example.scores.controller;

import com.example.scores.Exception.PlayerNotFoundException;
import com.example.scores.entity.Admin;
import com.example.scores.entity.Employee;
import com.example.scores.entity.Score;
import com.example.scores.model.ScoreModel;
import com.example.scores.repository.ScoreRepository;
import com.example.scores.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/game")
public class ScoresController {
    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PagedResourcesAssembler<Score> pagedResourcesAssembler;

    // Authentication methods
    @PostMapping("/auth/admin")
    public ResponseEntity<String> authenticateAdmin(@RequestParam String username, @RequestParam String password) {
        String response = authService.authenticateAdmin(username, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/employee")
    public ResponseEntity<String> authenticateEmployee(@RequestParam String username, @RequestParam String password) {
        String response = authService.authenticateEmployee(username, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String username) {
        authService.logout(username);
        return ResponseEntity.ok("Logged out successfully");
    }

    // Admin methods
    @PostMapping("/admin/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin, @RequestParam String username) {
        String role = authService.getRole(username);
        if (role != null && role.equals("ADMIN")) {
            return ResponseEntity.ok(authService.createAdmin(admin));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/admin/create-employee")
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee, @RequestParam String username) {
        String role = authService.getRole(username);
        if (role != null && role.equals("ADMIN")) {
            return ResponseEntity.ok(authService.createEmployee(employee));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/admin/scores/{id}")
    public ResponseEntity<?> deletePlayerAndScore(@PathVariable Long id, @RequestParam String username) {
        String role = authService.getRole(username);
        if (role != null && role.equals("ADMIN")) {
            // Check if the player ID exists
            if (!scoreRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            try {
                // Delete the player and their score from the database
                scoreRepository.deleteById(id);
                return ResponseEntity.ok("Player and score deleted successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting player and score");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Common methods
    @Operation(summary = "Get top scores", responses = {
            @ApiResponse(responseCode = "200", description = "Successful response")
    })
    @Tag(name = "get", description = "GET methods of topscores APIs,")
    @GetMapping("topscores")
    public ResponseEntity<?> getScores(@RequestParam(defaultValue = "5") int n) {
        if (n <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Score> scores = scoreRepository.findTopNByOrderByScoreDescCached(n);
        return ResponseEntity.ok(scores);
    }

    @Operation(summary = "Add a new score", responses = {
            @ApiResponse(responseCode = "200", description = "Successful response", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @Tag(name = "post", description = "post methods to set a new player with score")
    @PostMapping("player")
    public ResponseEntity<?> addScore(@RequestBody ScoreModel scoreRequest, @RequestParam(required = false) String username) {
        String role = authService.getRole(username);
        if (role != null && (role.equals("ADMIN") || role.equals("EMPLOYEE"))) {
            if (scoreRequest == null || scoreRequest.getPlayerName() == null) {
                return ResponseEntity.badRequest().build();
            }
            String playerName = scoreRequest.getPlayerName();
            int score = scoreRequest.getScore();
            if (score < 0 || score > 100) {
                return ResponseEntity.badRequest().body("Score value should be between 0 and 100");
            }
            scoreRepository.save(new Score(playerName, score));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "get methods to get players",
            description = "to get players with pagination , you can set page limit also and get links for nect and prev page")
    @GetMapping("scores")
    public ResponseEntity<PagedModel<EntityModel<Score>>> getScores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size)
    {

        if(page < 0)
        {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Score> ScoresPage = scoreRepository.findAllByOrderByScoreDesc(pageable);
        if (ScoresPage == null) {
            ScoresPage = Page.empty();
        }

        PagedModel<EntityModel<Score>> pagedModel = pagedResourcesAssembler.toModel(ScoresPage, ScoresController::entityModel);
        return ResponseEntity.ok(pagedModel);
    }

    private static EntityModel<Score> entityModel(Score Score) {
        return EntityModel.of(Score,
                linkTo(methodOn(ScoresController.class).getScores(0, 5)).withSelfRel());
    }

    @Tag(name = "put", description = "update method to update the score of a player")
    @PutMapping("/scores/{id}")
    public ResponseEntity<?> updateScore(@PathVariable Long id, @RequestParam int newScore, @RequestParam(required = false) String username) {
        String role = authService.getRole(username);
        if (role != null && (role.equals("ADMIN") || role.equals("EMPLOYEE"))) {
            try {
                // Check if the score ID exists
                if (!scoreRepository.existsById(id)) {
                    return ResponseEntity.notFound().build();
                }

                // Fetch the score entity from the database
                Score score = scoreRepository.findById(id).orElse(null);

                // Update the score
                if (score != null) {
                    score.updateScore(newScore);
                    scoreRepository.save(score);
                    return ResponseEntity.ok("Score updated successfully");
                } else {
                    return ResponseEntity.badRequest().body("Invalid score ID");
                }
            } catch (IllegalArgumentException e) {
                // Handle invalid score value
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                // Handle other exceptions
                return ResponseEntity.status(500).body("Internal server error");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "get an employee based on id ")
    @GetMapping("/scores/{id}")
    public ResponseEntity<?> getScoreById(@PathVariable Long id) {

//        // Check if the score ID exists
//        if (!scoreRepository.existsById(id)) {
//            return ResponseEntity.notFound().build();
//        }

        // Fetch the score entity from the database
        Score score = scoreRepository.findById(id).orElse(null);

        // Return the score entity in the response
        if (score != null) {
            return ResponseEntity.ok(score);
        } else {
            throw new PlayerNotFoundException("Player not found with id: " + id); //throwing custom error
        }
    }
}