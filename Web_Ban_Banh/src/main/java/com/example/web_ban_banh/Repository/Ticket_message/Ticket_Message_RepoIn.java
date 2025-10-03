package com.example.web_ban_banh.Repository.Ticket_message;

import com.example.web_ban_banh.Entity.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Ticket_Message_RepoIn extends JpaRepository<TicketMessage,Long> {
    List<TicketMessage> findByTicketTicketId(Long ticketId);

}
