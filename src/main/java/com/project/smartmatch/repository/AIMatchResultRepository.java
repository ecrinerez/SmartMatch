package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.AIMatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIMatchResultRepository extends JpaRepository<AIMatchResult, Long> {

    // 1. Veritabanından İlan ID'sine göre arama yapmak için:
    Optional<AIMatchResult> findByJobId(Long jobId);

    // 2. Veritabanından Aday ID'sine göre arama yapmak için:
    Optional<AIMatchResult> findByCandidateId(Long candidateId);
}