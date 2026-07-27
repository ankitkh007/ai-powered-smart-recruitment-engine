package com.recruitment.engine.repository;

import com.recruitment.engine.entity.Candidate;
import com.recruitment.engine.entity.enums.CandidateStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    long countByStatusIn(List<CandidateStatus> statuses);

    List<Candidate> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT c FROM Candidate c WHERE " +
            "(:skill IS NULL OR LOWER(c.skills) LIKE LOWER(CONCAT('%', :skill, '%'))) AND " +
            "(:education IS NULL OR LOWER(c.education) LIKE LOWER(CONCAT('%', :education, '%'))) AND " +
            "(:minExperience IS NULL OR c.experienceYears >= :minExperience)")
    List<Candidate> search(@Param("skill") String skill,
            @Param("education") String education,
            @Param("minExperience") BigDecimal minExperience,
            Sort sort);
}