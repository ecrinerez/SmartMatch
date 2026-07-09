package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.EmployerProfile;
import com.project.smartmatch.model.entity.JobPosting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Spring Boot context'ini gerçek olarak ayağa kaldırır
@Testcontainers // Docker container'larını otomatik yönetir
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Gerçek container veritabanını korur
class JobPostingRepositoryTest {

    // Docker üzerinde tamamen izole bir PostgreSQL container ayağa kaldırıyoruz
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("smartmatch_db")
            .withUsername("postgres")
            .withPassword("20033691ecrin");

    // application.yaml veritabanı bağlantı bilgilerini Docker'ın ürettiği dinamik portla ezer
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private EmployerProfileRepository employerProfileRepository;

    // Her test metodu çalışmadan önce veritabanını temizler ve taze verileri ekler
    @BeforeEach
    void setUp() {
        jobPostingRepository.deleteAll();
        employerProfileRepository.deleteAll();

        // 1. İş ilanı için gerekli olan işveren profilini hazırlayıp kaydediyoruz
        EmployerProfile employer = new EmployerProfile();
        employer.setCompanyName("Treasy Finansal Teknolojiler");
        employer = employerProfileRepository.save(employer);

        // 2. Senin Full-Text Search metodunu denemek için Java ilanı oluşturuyoruz
        JobPosting job1 = new JobPosting();
        job1.setTitle("Senior Java Developer");
        job1.setDescription("Deep knowledge in Spring Boot, Redis, and robust backend systems.");
        job1.setCity("Istanbul");
        job1.setEmployer(employer);
        job1.setRequiredSkills(List.of("Java", "Spring Boot"));

        // 3. Filtreleme ve sayfalama testleri için Ankara lokasyonlu ilan oluşturuyoruz
        JobPosting job2 = new JobPosting();
        job2.setTitle("React Frontend Engineer");
        job2.setDescription("Building high performance user interfaces using React and TypeScript.");
        job2.setCity("Ankara");
        job2.setEmployer(employer);
        job2.setRequiredSkills(List.of("React", "TypeScript"));

        jobPostingRepository.saveAll(List.of(job1, job2));
    }

    @Test
    @DisplayName("PostgreSQL tsvector kullanarak akıllı kelime araması (Full-Text Search) başarıyla çalışmalıdır")
    void searchJobPostings_FullTextSearch_Success() {
        // ACT: Senin JobPostingRepository içindeki orijinal searchJobPostings metodunu çağırıyoruz
        List<JobPosting> result = jobPostingRepository.searchJobPostings("Spring");

        // ASSERT: tsvector eşleşmesini kontrol ediyoruz
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getTitle().contains("Java"));
    }

    @Test
    @DisplayName("İlanlar şehre göre filtrelenebilmeli ve sayfalama metadata bilgileri doğru dönmelidir")
    void filterJobPostings_PaginationAndFiltering_Success() {
        // ACT: Senin orijinal findJobsWithFilters metodunu Istanbul şehri ve active=true filtreleriyle çağırıyoruz
        Page<JobPosting> result = jobPostingRepository.findJobsWithFilters("Istanbul", true, PageRequest.of(0, 1));

        // ASSERT: Filtreleme isabetini ve sayfalama yapısını denetliyoruz
        assertNotNull(result);
        assertEquals(1, result.getContent().size()); // Sayfa boyutu 1 olmalı
        assertEquals("Senior Java Developer", result.getContent().get(0).getTitle());
        assertEquals(0, result.getNumber()); // Mevcut sayfa indeksi 0 olmalı
    }
}