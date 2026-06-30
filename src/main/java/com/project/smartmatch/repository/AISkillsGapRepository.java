package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.AISkillsGap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AISkillsGapRepository extends JpaRepository<AISkillsGap, Long> {
    Optional<AISkillsGap> findByJobIdAndCandidateId(Long jobId, Long candidateId);
}