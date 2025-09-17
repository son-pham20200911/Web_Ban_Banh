package com.example.web_ban_banh.DTO.PasswordResetToken_DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest_DTO {
    private String token;
    private String newPassword;
}
