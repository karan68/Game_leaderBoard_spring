package com.example.scores.repository;

import com.example.scores.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    @Query(value = "SELECT s.id, s.player_name, s.score FROM scores s ORDER BY s.score DESC LIMIT :n",nativeQuery = true)
//    SELECT s.playerName, s.score FROM Score s ORDER BY s.score DESC
    List<Score> findTopNByOrderByScoreDesc(@Param("n") int n);
    Page<Score> findAllByOrderByScoreDesc(Pageable pageable);
}