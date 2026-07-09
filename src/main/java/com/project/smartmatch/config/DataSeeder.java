package com.project.smartmatch.config;

import com.project.smartmatch.model.entity.*;
import com.project.smartmatch.model.enums.ApplicationStatus;
import com.project.smartmatch.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Profile("dev") // Sadece development profilinde aktif olur
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Eğer veritabanında zaten kullanıcı varsa tekrar ekleme yapmasın (Mükerrer kaydı önleme)
        if (userRepository.count() > 0) {
            System.out.println("--> Demo data already exists; skipping seeder.");
            return;
        }

        System.out.println("--> Generating demo data...");

        // 1. Rollerin Hazırlanması
        Role candidateRole = roleRepository.findByName("CANDIDATE")
                .orElseGet(() -> { Role r = new Role(); r.setName("CANDIDATE"); return roleRepository.save(r); });
        Role employerRole = roleRepository.findByName("EMPLOYER")
                .orElseGet(() -> { Role r = new Role(); r.setName("EMPLOYER"); return roleRepository.save(r); });

        String encodedPassword = passwordEncoder.encode("secret123");

        // 2. 2 Adet İşveren (Employer) Oluşturulması
        List<EmployerProfile> employers = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            User user = new User();
            user.setEmail("employer" + i + "@company.com");
            user.setPasswordHash(encodedPassword);
            user.setCreatedAt(LocalDateTime.now());
            user.setRoles(Set.of(employerRole));
            User savedUser = userRepository.save(user);

            EmployerProfile empProfile = new EmployerProfile();
            empProfile.setUser(savedUser);
            empProfile.setCompanyName("Tech Comp " + i);
            empProfile.setIndustry("Software");
            empProfile.setWebsiteUrl("https://company" + i + ".com");
            empProfile.setPhoneNumber("555111223" + i);
            empProfile.setDescription("A visionary company producing outstanding projects..");
            employers.add(employerProfileRepository.save(empProfile));
        }

        // 3. 5 Adet Aday (Candidate) Oluşturulması
        List<CandidateProfile> candidates = new ArrayList<>();
        String[] titles = {"Java Developer", "Frontend Engineer", "Full Stack Developer", "QA Tester", "Data Analyst"};
        List<List<String>> skillsGroup = List.of(
                List.of("Java", "Spring Boot", "PostgreSQL", "Git"),
                List.of("JavaScript", "React", "HTML", "CSS"),
                List.of("Java", "React", "Spring Boot", "PostgreSQL"),
                List.of("Selenium", "Junit", "Java", "Postman"),
                List.of("Python", "SQL", "Tableau", "Excel")
        );

        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setEmail("candidate" + i + "@gmail.com");
            user.setPasswordHash(encodedPassword);
            user.setCreatedAt(LocalDateTime.now());
            user.setRoles(Set.of(candidateRole));
            User savedUser = userRepository.save(user);

            CandidateProfile candProfile = new CandidateProfile();
            candProfile.setUser(savedUser);
            candProfile.setTitle(titles[i - 1]);
            candProfile.setPhoneNumber("555222334" + i);
            candProfile.setSkills(skillsGroup.get(i - 1));
            candProfile.setExperienceYears(i + i % 2); // Değişken tecrübe yılları
            candProfile.setSummary("A passionate engineer aiming to develop myself in the software world..");
            candidates.add(candidateProfileRepository.save(candProfile));
        }

        // 4. 10 Adet İş İlanı (Job Posting) Oluşturulması
        List<JobPosting> jobPostings = new ArrayList<>();
        String[] jobTitles = {
                "Senior Java Developer", "React Frontend Specialist", "Spring Boot Backend Intern",
                "Full Stack Developer", "QA Automation Engineer", "Database Administrator",
                "DevOps Engineer", "Data Scientist", "Mobile (Flutter) Developer", "Scrum Master"
        };
        String[] cities = {"Istanbul", "Ankara", "Izmir", "Istanbul", "Ankara", "Bursa", "Istanbul", "Izmir", "Istanbul", "Remote"};

        for (int i = 0; i < 10; i++) {
            JobPosting job = new JobPosting();
            job.setTitle(jobTitles[i]);
            job.setDescription("Position details and required qualifications are listed in this section.");
            job.setCity(cities[i]);
            job.setSalaryMin(30000 + (i * 2000));
            job.setSalaryMax(50000 + (i * 3000));
            job.setIsActive(true);
            // 5 ilan ilk işverene, 5 ilan ikinci işverene bağlanır
            job.setEmployer(i < 5 ? employers.get(0) : employers.get(1));
            jobPostings.add(jobPostingRepository.save(job));
        }

        // 5. 8 Adet Başvuru (Application) Oluşturulması
        // Farklı adayları farklı ilanlara bağlayarak 8 adet başvuru üretimi:
        int applicationCount = 0;
        for (int c = 0; c < candidates.size(); c++) {
            for (int j = 0; j < jobPostings.size(); j++) {
                if (applicationCount >= 8) break;

                // Mükerrerliği önlemek için her adayı farklı bir ilana başvurtalım
                if ((c + j) % 2 == 0) {
                    Application app = new Application();
                    app.setCandidate(candidates.get(c));
                    app.setJobPosting(jobPostings.get(j));
                    // İlk birkaç başvuruyu ACCEPTED/REJECTED yapalım, kalanlar PENDING kalsın
                    if (applicationCount == 0) app.setStatus(ApplicationStatus.ACCEPTED);
                    else if (applicationCount == 1) app.setStatus(ApplicationStatus.REJECTED);
                    else app.setStatus(ApplicationStatus.PENDING);

                    jobApplicationRepository.save(app);
                    applicationCount++;
                }
            }
        }

        System.out.println("--> Demo data successfully loaded! (2 Employers, 5 Candidates, 10 Job Postings, 8 Applications)");
    }
}