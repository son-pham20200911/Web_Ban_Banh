package com.example.web_ban_banh.Service.SupportTicket_Service;

import com.example.web_ban_banh.DTO.TicKet_DTO.TicketDTO;
import com.example.web_ban_banh.DTO.TicKet_DTO.TicketMessageDTO;

import java.util.List;

public interface SupportTicket_ServiceIn {
    TicketDTO createTicket(TicketDTO dto, Integer userId);
    List<TicketDTO> getTicketsByUser(Integer userId);
    List<TicketDTO> adminGetTickets(String status, String keyword);
    TicketDTO getTicketDetail(Integer ticketId, Integer currentUserId, boolean isAdmin);
    TicketMessageDTO addMessage(Integer ticketId, Integer userId, String message, boolean isStaff);
    void updateStatus(Integer ticketId, String status);
}
