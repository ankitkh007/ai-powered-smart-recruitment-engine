package com.recruitment.engine.entity;

import com.recruitment.engine.entity.enums.ConfidenceLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_scores")
public class AIScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @Column(name = "match_percentage", nullable = false)
    private Double matchPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ConfidenceLevel confidence;

    @Column(name = "matching_skills", columnDefinition = "TEXT")
    private String matchingSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected AIScore() {
    }

    public AIScore(Application application, Double matchPercentage, ConfidenceLevel confidence,
            String matchingSkills, String missingSkills, String strengths, String weaknesses, String summary) {
        this.application = application;
        this.matchPercentage = matchPercentage;
        this.confidence = confidence;
        this.matchingSkills = matchingSkills;
        this.missingSkills = missingSkills;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.summary = summary;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public Double getMatchPercentage() {
        return matchPercentage;
    }

    public ConfidenceLevel getConfidence() {
        return confidence;
    }

    public String getMatchingSkills() {
        return matchingSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public String getStrengths() {
        return strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}