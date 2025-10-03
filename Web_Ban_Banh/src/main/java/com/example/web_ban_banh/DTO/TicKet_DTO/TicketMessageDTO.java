package com.example.web_ban_banh.DTO.TicKet_DTO;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class TicketMessageDTO {
    private Long messageId;
    private Integer userId;
    private String userName;
    private String message;
    private Boolean isStaffReply;
    private Timestamp createdAt;
}
