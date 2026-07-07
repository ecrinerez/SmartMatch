package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //"Sana verdiğim kullanıcı id'sine ait bildirimleri bul, bunlardan sadece
    // okunmamış (false) olanları seç ve onları en yeni tarihten en eski tarihe göre
    // sıralayarak bana getir."
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}    //Bu metot güvenlik içindir; kullanıcının başkasına ait bir bildirimi yanlışlıkla "okundu" yapmasını engeller.