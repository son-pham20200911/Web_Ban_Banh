package com.example.web_ban_banh.Repository.User;

import com.example.web_ban_banh.DTO.User_DTO.Get.UserSecret_DTO;
import com.example.web_ban_banh.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface User_RepoIn extends JpaRepository<User,Integer> {
    Optional<User>findByUserName(String username);
    boolean existsByUserName(String username);
    boolean existsByEmail(String Email);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE CONCAT('%',?1,'%')")
    public Page<User> findByFullName( String fullName, Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);
}
