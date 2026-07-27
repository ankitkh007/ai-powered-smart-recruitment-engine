package com.recruitment.engine.repository;

import com.recruitment.engine.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByCandidateId(Long candidateId);
}