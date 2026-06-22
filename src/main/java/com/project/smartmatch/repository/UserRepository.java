package com.project.smartmatch.repository;

import com.project.smartmatch.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Override
    Optional<User> findById(Integer integer);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}