package com.example.web_ban_banh.DTO.PasswordResetToken_DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest_DTO {
    @NotBlank(message = "Có vẻ bạn đang thiếu Token")
    private String token;

    @NotBlank(message = "Không được để trống Password")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-])[a-zA-Z0-9!@#$%^&*()-]{8,20}$"
            ,message = "Mật khẩu phải có 1 chữ thường, 1 chữ hoa, 1 chữ số, 1 ký tự đặc biệt !@#$%^&*()- và độ dài từ 8 đến 20 ký tự")
    private String newPassword;
}
