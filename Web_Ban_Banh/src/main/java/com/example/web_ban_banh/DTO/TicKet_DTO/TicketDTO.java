package com.example.web_ban_banh.DTO.TicKet_DTO;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class TicketDTO {
    private int ticketId;
    private Integer userId;
    private String userName;
    private String subject;
    private String description;
    private String bookingReference;
    private String priority ;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp resolvedAt;
    private List<TicketMessageDTO> messages;
}
