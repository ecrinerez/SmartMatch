package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobPostingId(Long jobPostingId);

    List<Application> findByCandidateId(Long candidateId);

    boolean existsByCandidateIdAndJobPostingId(Long candidateId, Long jobPostingId);
}
//optional=Sorgunun sonucunda kesinlikle tek bir satır dönecekse
//list=birden fazla dönerse
//birden fazla ilana başvurmak ve ilana birden fazla kişinin başvurması