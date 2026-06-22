package com.project.smartmatch.service;

import com.project.smartmatch.model.dto.JobPostingRequest;
import com.project.smartmatch.model.dto.JobPostingResponse;
import com.project.smartmatch.model.entity.EmployerProfile;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.repository.JobPostingRepository;
import com.project.smartmatch.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    // Yeni bir iş ilanı oluşturur ve ilan sahibi olarak ilgili işvereni bağlar.
    @Transactional
    public JobPostingResponse createJobPosting(JobPostingRequest request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        EmployerProfile employer = user.getEmployerProfile();
        if (employer == null) {
            throw new AccessDeniedException("User does not have an employer profile");
        }

        JobPosting jobPosting = new JobPosting();
        mapRequestToEntity(request, jobPosting);
        jobPosting.setEmployer(employer);

        JobPosting savedJob = jobPostingRepository.save(jobPosting);
        return mapToResponse(savedJob);
    }

    // İlanları şehre ve aktiflik durumuna göre süzerek sayfa sayfa (Pageable) listeler.
    @Transactional(readOnly = true)
    public Page<JobPostingResponse> getAllJobPostings(String city, Boolean isActive, Pageable pageable) {
        Page<JobPosting> jobs = jobPostingRepository.findJobsWithFilters(city, isActive, pageable);
        return jobs.map(this::mapToResponse);
    }

    // Belirtilen ID'ye sahip tek bir ilanın detaylarını getirir.
    @Transactional(readOnly = true)
    public JobPostingResponse getJobPostingById(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + id));
        return mapToResponse(jobPosting);
    }

    // İlanı veritabanından çekip, sadece sahibi olan işverenin güncellemesine izin verir.
    @Transactional
    public JobPostingResponse updateJobPosting(Long id, JobPostingRequest request, String username) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + id));

        if (!jobPosting.getEmployer().getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("You are not authorized to update this job posting");
        }

        mapRequestToEntity(request, jobPosting);
        JobPosting updatedJob = jobPostingRepository.save(jobPosting);
        return mapToResponse(updatedJob);
    }

    // İlanı veritabanından çekip, sadece sahibi olan işverenin silmesine izin verir.
    @Transactional
    public void deleteJobPosting(Long id, String username) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + id));

        if (!jobPosting.getEmployer().getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this job posting");
        }

        jobPostingRepository.delete(jobPosting);
    }

    // İstekten (Request) gelen verileri veritabanı Entity modeline dönüştürür.
    private void mapRequestToEntity(JobPostingRequest request, JobPosting entity) {
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setRequiredSkills(request.getRequiredSkills());
        entity.setCity(request.getCity());
        entity.setSalaryMin(request.getSalaryMin());
        entity.setSalaryMax(request.getSalaryMax());
    }

    // Veritabanı Entity modelindeki verileri dış dünyaya sunulacak Response modeline dönüştürür.
    private JobPostingResponse mapToResponse(JobPosting entity) {
        JobPostingResponse response = new JobPostingResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setRequiredSkills(entity.getRequiredSkills());
        response.setCity(entity.getCity());
        response.setSalaryMin(entity.getSalaryMin());
        response.setSalaryMax(entity.getSalaryMax());
        response.setIsActive(entity.getIsActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setEmployerId(Long.valueOf(entity.getEmployer().getId()));
        response.setCompanyName(entity.getEmployer().getCompanyName());
        return response;
    }
}