package com.project.smartmatch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "employer_stats")
@Immutable // Bu bir View olduğu için Java tarafında değiştirilemez (read-only) yapılır
@Getter
@Setter
public class EmployerStats {
    @Id
    private Long employerId;
    private Long totalPostings;
    private Long pendingApplications;
    private Long acceptedApplications;
    private Long rejectedApplications;
}