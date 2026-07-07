package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.EmployerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerStatsRepository extends JpaRepository<EmployerStats, Long> {

    @Query(value = "SELECT " +
            "  coalesce(count(DISTINCT j.id), 0) AS total_postings, " +
            "  coalesce(count(CASE WHEN a.status = 'PENDING' THEN 1 END), 0) AS pending_applications, " +
            "  coalesce(count(CASE WHEN a.status = 'ACCEPTED' THEN 1 END), 0) AS accepted_applications, " +
            "  coalesce(count(CASE WHEN a.status = 'REJECTED' THEN 1 END), 0) AS rejected_applications " +
            "FROM employer_profiles ep " +
            "LEFT JOIN job_postings j ON j.employer_id = ep.id " +
            "LEFT JOIN applications a ON a.job_posting_id = j.id " +
            "WHERE ep.id = :employerId " +
            "GROUP BY ep.id", nativeQuery = true)
    Optional<List<Object[]>> findRawStatsByEmployerId(@Param("employerId") Long employerId);
}