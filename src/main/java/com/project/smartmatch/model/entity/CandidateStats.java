package com.project.smartmatch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "candidate_stats")
@Immutable
@Getter
@Setter
public class CandidateStats {
    @Id
    private Long candidateId;
    private Long totalApplications;
}