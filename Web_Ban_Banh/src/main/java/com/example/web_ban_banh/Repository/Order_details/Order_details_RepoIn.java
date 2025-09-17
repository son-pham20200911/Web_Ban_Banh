package com.example.web_ban_banh.Repository.Order_details;

import com.example.web_ban_banh.Entity.Order_details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Order_details_RepoIn extends JpaRepository<Order_details,Integer> {
}
