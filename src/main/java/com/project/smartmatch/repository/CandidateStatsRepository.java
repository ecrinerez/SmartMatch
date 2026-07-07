package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.CandidateStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateStatsRepository extends JpaRepository<CandidateStats, Long> {

    @Query(value = "SELECT " +
            "  coalesce(count(a.id), 0) AS total_applications " +
            "FROM candidate_profiles cp " +
            "LEFT JOIN applications a ON a.candidate_id = cp.id " +
            "WHERE cp.id = :candidateId " +
            "GROUP BY cp.id", nativeQuery = true)
    Optional<Object[]> findRawStatsByCandidateId(@Param("candidateId") Long candidateId);
}