package com.example.qls.repositories;

import com.example.qls.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findUserById(Long id);

    Boolean existsByEmail(String email);

    User findByEmailAndPassword(String email, String password);
}
