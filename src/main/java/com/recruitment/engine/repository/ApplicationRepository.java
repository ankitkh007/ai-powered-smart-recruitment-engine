package com.recruitment.engine.repository;

import com.recruitment.engine.entity.Application;
import com.recruitment.engine.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByCandidateIdAndJobDescriptionId(Long candidateId, Long jobDescriptionId);

    List<Application> findByJobDescriptionId(Long jobDescriptionId);

    @Query("SELECT a FROM Application a LEFT JOIN AIScore s ON s.application = a " +
            "WHERE a.jobDescription.id = :jobId ORDER BY s.matchPercentage DESC NULLS LAST")
    List<Application> findByJobIdRankedByScore(@Param("jobId") Long jobId);

    long countByStatus(ApplicationStatus status);
}