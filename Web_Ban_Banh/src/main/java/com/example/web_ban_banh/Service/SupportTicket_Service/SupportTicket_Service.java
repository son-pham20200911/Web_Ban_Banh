package com.example.web_ban_banh.Service.SupportTicket_Service;

import com.example.web_ban_banh.DTO.TicKet_DTO.TicketDTO;
import com.example.web_ban_banh.DTO.TicKet_DTO.TicketMessageDTO;
import com.example.web_ban_banh.Entity.SupportTicket;
import com.example.web_ban_banh.Entity.TicketMessage;
import com.example.web_ban_banh.Entity.User;
import com.example.web_ban_banh.Repository.Support_ticket.SupportTicket_RepoIn;
import com.example.web_ban_banh.Repository.Ticket_message.Ticket_Message_RepoIn;
import com.example.web_ban_banh.Repository.User.User_RepoIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportTicket_Service implements SupportTicket_ServiceIn{
    private SupportTicket_RepoIn ticketRepo;
    private Ticket_Message_RepoIn messageRepo;
    private User_RepoIn userRepo;

    @Autowired
    public SupportTicket_Service(SupportTicket_RepoIn ticketRepo, Ticket_Message_RepoIn messageRepo, User_RepoIn userRepo) {
        this.ticketRepo = ticketRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
    }

    @Override
    public TicketDTO createTicket(TicketDTO dto, Integer userId) {
        User user = userRepo.findById(userId).orElseThrow();
        SupportTicket ticket = SupportTicket.builder()
                .user(user)
                .subject(dto.getSubject())
                .description(dto.getDescription())
                .bookingReference(dto.getBookingReference())
                .priority(SupportTicket.Priority.valueOf(dto.getPriority()))
                .status(SupportTicket.Status.OPEN)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        ticket = ticketRepo.save(ticket);
        return mapTicketToDTO(ticket, true);
    }

    @Override
    public List<TicketDTO> getTicketsByUser(Integer userId) {
        return ticketRepo.findByUser_Id(userId)
                .stream()
                .map(t -> mapTicketToDTO(t, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDTO> adminGetTickets(String status, String keyword) {
        List<SupportTicket> tickets;
        if (status != null) {
            tickets = ticketRepo.findByStatus(SupportTicket.Status.valueOf(status));
        } else if (keyword != null) {
            tickets = ticketRepo.findBySubjectContainingIgnoreCase(keyword);
        } else {
            tickets = ticketRepo.findAll();
        }
        return tickets.stream().map(t -> mapTicketToDTO(t, false)).collect(Collectors.toList());
    }

    @Override
    public TicketDTO getTicketDetail(Integer ticketId, Integer currentUserId, boolean isAdmin) {
        SupportTicket ticket = ticketRepo.findById(ticketId).orElseThrow();
        if (!isAdmin && ticket.getUser().getId() != currentUserId) {
            throw new AccessDeniedException("Không có quyền xem ticket này!");
        }

        return mapTicketToDTO(ticket, true);
    }

    @Override
    public TicketMessageDTO addMessage(Integer ticketId, Integer userId, String message, boolean isStaff) {
        SupportTicket ticket = ticketRepo.findById(ticketId).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();

        // Chỉ cho gửi khi ticket chưa đóng
        if (ticket.getStatus() == SupportTicket.Status.CLOSED || ticket.getStatus() == SupportTicket.Status.RESOLVED)
            throw new RuntimeException("Ticket đã đóng!");

        TicketMessage msg = TicketMessage.builder()
                .ticket(ticket)
                .user(user)
                .message(message)
                .isStaffReply(isStaff)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        messageRepo.save(msg);

        ticket.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        if (isStaff && ticket.getStatus() == SupportTicket.Status.OPEN)
            ticket.setStatus(SupportTicket.Status.IN_PROGRESS);
        ticketRepo.save(ticket);

        return mapMessageToDTO(msg);
    }

    @Override
    public void updateStatus(Integer ticketId, String status) {
        SupportTicket ticket = ticketRepo.findById(ticketId).orElseThrow();
        ticket.setStatus(SupportTicket.Status.valueOf(status));
        if (status.equalsIgnoreCase("RESOLVED")) {
            ticket.setResolvedAt(new Timestamp(System.currentTimeMillis()));
        }
        ticket.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        ticketRepo.save(ticket);
    }

    // ==== MAPPER ==== //
    private TicketDTO mapTicketToDTO(SupportTicket t, boolean includeMessages) {
        return TicketDTO.builder()
                .ticketId(t.getTicketId())
                .userId(t.getUser().getId())
                .userName(t.getUser().getFullName())
                .subject(t.getSubject())
                .description(t.getDescription())
                .bookingReference(t.getBookingReference())
                .priority(t.getPriority().name())
                .status(t.getStatus().name())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .resolvedAt(t.getResolvedAt())
                .messages(includeMessages ?
                        t.getMessages().stream().map(this::mapMessageToDTO).collect(Collectors.toList())
                        : null)
                .build();
    }

    private TicketMessageDTO mapMessageToDTO(TicketMessage m) {
        return TicketMessageDTO.builder()
                .messageId(m.getMessageId())
                .userId(m.getUser().getId())
                .userName(m.getUser().getFullName())
                .message(m.getMessage())
                .isStaffReply(m.getIsStaffReply())
                .createdAt(m.getCreatedAt())
                .build();
    }

}
