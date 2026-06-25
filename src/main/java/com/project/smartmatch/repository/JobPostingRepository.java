package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    @Query("SELECT j FROM JobPosting j WHERE " +
            "(:city IS NULL OR LOWER(j.city) = LOWER(:city)) AND " +
            "(:isActive IS NULL OR j.isActive = :isActive)")
    Page<JobPosting> findJobsWithFilters(@Param("city") String city,
                                         @Param("isActive") Boolean isActive,
                                         Pageable pageable);
    @Query(value = "SELECT * FROM job_postings WHERE search_vector @@ to_tsquery('turkish', :query)", nativeQuery = true)
    List<JobPosting> searchJobPostings(@Param("query") String query);
}