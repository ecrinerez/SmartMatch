package com.project.smartmatch.model.entity;

import com.project.smartmatch.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="applications", uniqueConstraints = {
        @UniqueConstraint(
                name = "uc_candidate_job",
                columnNames = {"candidate_id", "job_posting_id"}
        )
})
@Getter
@Setter
public class Application {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn (name="candidate_id")
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.LAZY,optional = false) //ilişki zorunlu demek, Foreign key ilişkilerde kullanılır genelde
    @JoinColumn(name="job_posting_id")
    private JobPosting jobPosting;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ApplicationStatus status;

    @Column(nullable = false,updatable = false) //column null olamaz, update sorgularına dahil edilemez.
    //...At'te updateable genelde kullanılır.
    private LocalDateTime appliedAt;  //...At genelde timestamp için kullanılır
    //LocalDateTime standart kullanımıdır

    @Column(nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.PENDING; // İlk kayıt anında statü boşsa otomatik PENDING
        }
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // Sadece updatedAt güncellenecek
    }

}
