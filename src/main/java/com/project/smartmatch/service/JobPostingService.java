package com.project.smartmatch.service;

import com.project.smartmatch.model.request.JobPostingRequest;
import com.project.smartmatch.model.response.JobPostingResponse;
import com.project.smartmatch.model.entity.EmployerProfile;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.repository.JobPostingRepository;
import com.project.smartmatch.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "active-jobs";
    private static final String LATEST_JOBS_KEY = "jobs:latest";

    // Yeni bir iş ilanı oluşturur ve ilan sahibi olarak ilgili işvereni bağlar.
    @Transactional
    @CacheEvict(value = CACHE_KEY, allEntries = true)
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
        JobPostingResponse response = mapToResponse(savedJob);

        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(LATEST_JOBS_KEY, response, score);
        redisTemplate.opsForZSet().removeRange(LATEST_JOBS_KEY, 0, -21);

        return response;
    }

    // İlanları şehre ve aktiflik durumuna göre süzerek sayfa sayfa (Pageable) listeler.
    @Transactional(readOnly = true)
    public Page<JobPostingResponse> getAllJobPostings(String city, Boolean isActive, Pageable pageable) {
        Page<JobPosting> jobs = jobPostingRepository.findJobsWithFilters(city, isActive, pageable);
        return jobs.map(this::mapToResponse);
    }

    // Belirtilen ID'ye sahip tek bir ilanın detaylarını getirir.
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_KEY, key = "#id")
    public JobPostingResponse getJobPostingById(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + id));
        return mapToResponse(jobPosting);
    }

    // İlanı veritabanından çekip, sadece sahibi olan işverenin güncellemesine izin verir.
    @Transactional
    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public JobPostingResponse updateJobPosting(Long id, JobPostingRequest request, String username) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + id));

        if (!jobPosting.getEmployer().getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("You are not authorized to update this job posting");
        }

        mapRequestToEntity(request, jobPosting);
        JobPosting updatedJob = jobPostingRepository.save(jobPosting);

        JobPostingResponse response = mapToResponse(updatedJob);
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(LATEST_JOBS_KEY, response, score);
        redisTemplate.opsForZSet().removeRange(LATEST_JOBS_KEY, 0, -21);

        return response;
    }

    // İlanı veritabanından çekip, sadece sahibi olan işverenin silmesine izin verir.
    @Transactional
    @CacheEvict(value = CACHE_KEY, allEntries = true)
    public void deleteJobPosting(Long id, String username) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + id));

        if (!jobPosting.getEmployer().getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this job posting");
        }

        jobPostingRepository.delete(jobPosting);

        JobPostingResponse response = mapToResponse(jobPosting);
        redisTemplate.opsForZSet().remove(LATEST_JOBS_KEY, response);
    }

    // Full-text search index'ini kullanarak Türkçe dil kurallarına göre akıllı arama yapar.
    @Transactional(readOnly = true)
    public List<JobPostingResponse> searchJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return jobPostingRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        String formattedQuery = keyword.trim().replaceAll("\\s+", " & ");
        return jobPostingRepository.searchJobPostings(formattedQuery).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<Object> getLatest20Jobs() {
        Set<Object> latestJobs = redisTemplate.opsForZSet().reverseRange(LATEST_JOBS_KEY, 0, 19);
        if (latestJobs == null || latestJobs.isEmpty()) {
            return List.of();
        }
        return List.copyOf(latestJobs);
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