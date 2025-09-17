package com.example.web_ban_banh.Repository.PasswordResetToken;

import com.example.web_ban_banh.Entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetToken_RePoIn extends JpaRepository<PasswordResetToken,Integer> {
 Optional<PasswordResetToken>findByToken(String token);
}
