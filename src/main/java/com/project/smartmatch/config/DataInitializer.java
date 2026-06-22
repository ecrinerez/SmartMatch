//böyle ayrı dosya açarak yapmak gerekmiyorsa 22-01'e ekleme yapıyoruz.
package com.project.smartmatch.config;
import com.project.smartmatch.model.entity.Role;
import com.project.smartmatch.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // CANDIDATE verisi yoksa sadece tabloya veri satırı ekler
        if (roleRepository.findByName("CANDIDATE").isEmpty()) {
            Role candidateRole = new Role();
            candidateRole.setName("CANDIDATE");
            roleRepository.save(candidateRole);
            System.out.println("--> CANDIDATE role initialized successfully.");
        }

        // EMPLOYER verisi yoksa sadece tabloya veri satırı ekler
        if (roleRepository.findByName("EMPLOYER").isEmpty()) {
            Role employerRole = new Role();
            employerRole.setName("EMPLOYER");
            roleRepository.save(employerRole);
            System.out.println("--> EMPLOYER role initialized successfully.");
        }
    }
}