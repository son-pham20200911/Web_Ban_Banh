package com.example.web_ban_banh.Repository.Support_ticket;

import com.example.web_ban_banh.Entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SupportTicket_RepoIn extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByUser_Id(Integer userId);
    List<SupportTicket> findByStatus(SupportTicket.Status status);
    List<SupportTicket> findBySubjectContainingIgnoreCase(String keyword);
}
