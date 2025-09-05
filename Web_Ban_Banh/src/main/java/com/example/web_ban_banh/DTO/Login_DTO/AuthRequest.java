package com.example.web_ban_banh.DTO.Login_DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Không được để trống Username")
    private String username;
    @NotBlank(message = "Không được để trống Password")
    private String password;
}
