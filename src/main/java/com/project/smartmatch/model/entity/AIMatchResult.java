package com.project.smartmatch.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"ai_match_results \"") //boşluk koymuştum sonuna
@Getter
@Setter
public class AIMatchResult {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
        private Long id;

    @Column(name="jobId", nullable = false)
    private Long jobId;

    @Column(name="candidateId", nullable = false)
    private Long candidateId;

    @Column(name="score", nullable = false)
    private Integer score;

    @Column(name="reason",columnDefinition = "TEXT")
    private String reason;


}
