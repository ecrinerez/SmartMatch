package com.project.smartmatch.service;

import com.project.smartmatch.model.entity.EmployerProfile;
import com.project.smartmatch.model.entity.JobPosting;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.model.request.JobPostingRequest;
import com.project.smartmatch.model.response.JobPostingResponse;
import com.project.smartmatch.repository.JobPostingRepository;
import com.project.smartmatch.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobPostingServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private JobPostingService jobPostingService;

    private User employerUser;
    private EmployerProfile employerProfile;
    private JobPosting sampleJob;

    @BeforeEach
    void setUp() {
        employerProfile = new EmployerProfile();
        employerProfile.setId(10L);
        employerProfile.setCompanyName("Treasy Finansal Teknolojiler");

        employerUser = new User();
        employerUser.setEmail("employer@company.com");
        employerUser.setEmployerProfile(employerProfile);
        employerProfile.setUser(employerUser);

        sampleJob = new JobPosting();
        sampleJob.setId(100L);
        sampleJob.setTitle("Java Developer");
        sampleJob.setDescription("Spring Boot expert required");
        sampleJob.setEmployer(employerProfile);
        sampleJob.setCity("Istanbul");
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when an unauthorized user tries to update a job posting")
    void updateJobPosting_NotOwner_ThrowsAccessDeniedException() {
        JobPostingRequest request = new JobPostingRequest();
        request.setTitle("Unauthorized Title Update");

        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(sampleJob));

        assertThrows(AccessDeniedException.class, () ->
                jobPostingService.updateJobPosting(100L, request, "other@company.com")
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when employer company name is empty during creation")
    void createJobPosting_EmptyCompanyName_ThrowsIllegalArgumentException() {
        employerProfile.setCompanyName("");
        JobPostingRequest request = new JobPostingRequest();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(employerUser));

        assertThrows(IllegalArgumentException.class, () ->
                jobPostingService.createJobPosting(request, "employer@company.com")
        );
    }

    @Test
    @DisplayName("Should successfully update job posting fields and push to Redis sorted set when requested by owner")
    void updateJobPosting_Success() {
        JobPostingRequest request = new JobPostingRequest();
        request.setTitle("Senior Java Developer");
        request.setDescription("Updated description fields");
        request.setCity("Istanbul");
        request.setRequiredSkills(List.of("Java", "Redis", "Docker"));

        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(sampleJob));
        when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(sampleJob);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        JobPostingResponse response = jobPostingService.updateJobPosting(100L, request, "employer@company.com");

        assertNotNull(response);
        assertEquals("Senior Java Developer", response.getTitle());
        verify(jobPostingRepository, times(1)).save(any(JobPosting.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when requested job posting ID does not exist")
    void getJobPostingById_NotFound_ThrowsEntityNotFoundException() {
        when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> jobPostingService.getJobPostingById(999L));
    }

    @Test
    @DisplayName("Should successfully delete job posting and clear cache when requested by owner")
    void deleteJobPosting_Success() {
        when(jobPostingRepository.findById(100L)).thenReturn(Optional.of(sampleJob));
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        assertDoesNotThrow(() -> jobPostingService.deleteJobPosting(100L, "employer@company.com"));
        verify(jobPostingRepository, times(1)).delete(sampleJob);
    }
}