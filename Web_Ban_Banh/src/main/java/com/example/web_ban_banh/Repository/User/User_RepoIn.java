package com.example.web_ban_banh.Repository.User;

import com.example.web_ban_banh.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface User_RepoIn extends JpaRepository<User,Integer> {
    Optional<User>findByUserName(String username);
    boolean existsByUserName(String username);
    boolean existsByEmail(String Email);

    boolean existsByPhoneNumber(String phoneNumber);
}
