package com.project.smartmatch.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
public class CandidateProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //id sırası tutar; 1,2,3..
    private Long id;

    @OneToOne  //1 kullanıcının 1 profili olabilir
    @JoinColumn(name = "user_id", nullable = false, unique = true) //nullable= Bir kullanıcı profili olmadan var olamaz.
    private User user;  //unique= 1 sıra numasının(id'sinin) sadece bir kişide olduğundan emin olur.

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "resume_url")  //pdf vb olarak almak veritabanını çok şişirir, performansı düşürür.
    private String resumeUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "skills", columnDefinition = "text[]") // Birden fazla metini liste şeklinde tutabilmeyi sağlar
    private List<String> skills;

    @Column(name = "summary", columnDefinition = "TEXT") //metinleri uzun yazabilmeyi sağlar
    private String summary;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @JdbcTypeCode(SqlTypes.JSON) //jsonb yapısı
    @Column(name = "education", columnDefinition = "jsonb")
    private Map<String, Object> education;

}

