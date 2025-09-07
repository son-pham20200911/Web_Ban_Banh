package com.example.web_ban_banh.Repository.Discount_code;

import com.example.web_ban_banh.Entity.Discount_code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface Discount_code_RepoIn extends JpaRepository<Discount_code,Integer> {
    @Query("SELECT dc FROM Discount_code dc WHERE dc.code=:code AND dc.activated=:activated")
    Optional<Discount_code> findByCodeAndActivsted(@Param("code") String code, @Param("activated") boolean activated);

    @Query("SELECT dc FROM Discount_code dc WHERE dc.code=:code")
    Discount_code findByCode(@Param("code") String code);

}
