package com.project.smartmatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smartmatch.model.entity.Application;
import com.project.smartmatch.model.entity.CandidateProfile;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.model.enums.ApplicationStatus;
import com.project.smartmatch.model.request.JobApplicationRequest;
import com.project.smartmatch.model.request.JobApplicationStatusRequest;
import com.project.smartmatch.model.response.JobApplicationResponse;
import com.project.smartmatch.repository.CandidateProfileRepository;
import com.project.smartmatch.repository.JobApplicationRepository;
import com.project.smartmatch.repository.JobPostingRepository;
import com.project.smartmatch.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;

    // REDIS PUB/SUB İÇİN EKLENEN BAĞIMLILIKLAR
    private final StringRedisTemplate stringRedisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public JobApplicationResponse applyToJob(JobApplicationRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found."));

        CandidateProfile candidate = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Candidate profile not found for this user."));

        JobPosting jobPosting = jobPostingRepository.findById(request.getJobId())
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + request.getJobId()));

        boolean alreadyApplied = jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidate.getId(), jobPosting.getId());
        if (alreadyApplied) {
            throw new RuntimeException("You have already applied to this job posting!");
        }

        // 4. Başvuruyu oluştur ve veritabanına kaydet
        Application application = new Application();
        application.setCandidate(candidate);
        application.setJobPosting(jobPosting);

        Application savedApplication = jobApplicationRepository.save(application);
        return convertToResponse(savedApplication);
    }

    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getMyApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found."));

        CandidateProfile candidate = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Candidate profile not found for this user."));

        List<Application> applications = jobApplicationRepository.findByCandidateId(candidate.getId());
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getApplicationsByJobId(Long jobId, String employerEmail) {
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job posting not found with id: " + jobId));

        if (!jobPosting.getEmployer().getUser().getEmail().equals(employerEmail)) {
            throw new AccessDeniedException("You are not authorized to view applications for this job posting!");
        }

        List<Application> applications = jobApplicationRepository.findByJobPostingId(jobId);
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobApplicationResponse updateApplicationStatus(Long applicationId, JobApplicationStatusRequest request, String employerEmail) {
        Application application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Job application not found with id: " + applicationId));

        if (!application.getJobPosting().getEmployer().getUser().getEmail().equals(employerEmail)) {
            throw new AccessDeniedException("You are not authorized to update this application's status!");
        }

        application.setStatus(request.getStatus());
        Application updatedApplication = jobApplicationRepository.save(application);

        // STATUS DEĞİŞTİĞİNDE REDIS'E EVENT PUBLISH ETME (BİLDİRİM SİSTEMİ)

        try {
            // 1. Bildirimin hangi adaya gideceği belirlenir. (Adayın User ID'si)
            Long candidateUserId = application.getCandidate().getUser().getId();

            // 2. Kullanıcıya gösterilecek olan mesaj
            String companyName = application.getJobPosting().getEmployer().getCompanyName();
            String jobTitle = application.getJobPosting().getTitle();
            String messageText = String.format("Your application for '%s' position at '%s' has been %s.",
                    jobTitle, companyName, updatedApplication.getStatus().name());

            // 3. JSON alanlarını (userId, type, message, timestamp) bir Map içinde toplanır
            Map<String, Object> notificationEvent = new HashMap<>();
            notificationEvent.put("userId", candidateUserId);
            notificationEvent.put("type", "APPLICATION_STATUS_CHANGED");
            notificationEvent.put("message", messageText);
            notificationEvent.put("timestamp", LocalDateTime.now().toString());

            // 4. Map yapısını JSON metnine dönüştürür
            String jsonEvent = objectMapper.writeValueAsString(notificationEvent);

            // 5. Redis'teki 'notifications' borusuna bu mesaj fırlatılır.
            stringRedisTemplate.convertAndSend(topic.getTopic(), jsonEvent);
            log.info("Notification event successfully published to Redis for user id: {}", candidateUserId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification event to JSON!", e);
        }

        return convertToResponse(updatedApplication);
    }
//Helper Method; Veritabanından gelen karmaşık ve büyük Application nesnesini alıp,
// Postman ekranında görülen sadeleştirilmiş JobApplicationResponse nesnesine dönüştürür.

    private JobApplicationResponse convertToResponse(Application application) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(application.getId());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());
        response.setUpdatedAt(application.getUpdatedAt());

        response.setJobId(application.getJobPosting().getId());
        response.setJobTitle(application.getJobPosting().getTitle());
        response.setCompanyName(application.getJobPosting().getEmployer().getCompanyName());

        response.setCandidateId(application.getCandidate().getId());
        response.setCandidateEmail(application.getCandidate().getUser().getEmail());
        response.setCandidateTitle(application.getCandidate().getTitle());

        return response;
    }
}