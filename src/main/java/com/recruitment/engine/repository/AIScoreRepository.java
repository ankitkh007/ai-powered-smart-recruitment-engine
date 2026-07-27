package com.recruitment.engine.repository;

import com.recruitment.engine.entity.AIScore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AIScoreRepository extends JpaRepository<AIScore, Long> {
    Optional<AIScore> findByApplicationId(Long applicationId);
}