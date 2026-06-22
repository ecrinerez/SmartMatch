package com.project.smartmatch.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
public class CandidateProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //id sırası tutar; 1,2,3..
    private Integer id;

    @OneToOne  //1 kullanıcının 1 profili olabilir
    @JoinColumn(name = "user_id", nullable = false, unique = true) //nullable= Bir kullanıcı profili olmadan var olamaz.
    private User user;  //unique= 1 sıra numasının(id'sinin) sadece bir kişide olduğundan emin olur.

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "resume_url")  //pdf vb olarak almak veritabanını çok şişirir, performansı düşürür.
    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String education;
}

