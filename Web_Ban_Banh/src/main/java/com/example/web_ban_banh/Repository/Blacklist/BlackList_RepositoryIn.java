package com.example.web_ban_banh.Repository.Blacklist;

import com.example.web_ban_banh.Entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface BlackList_RepositoryIn extends JpaRepository<BlacklistedToken,String> {
    @Query("DELETE FROM BlacklistedToken bt WHERE bt.expiryDate < :date")
    public void deleteAllExpiryDate(@Param("date") Date date);
}
