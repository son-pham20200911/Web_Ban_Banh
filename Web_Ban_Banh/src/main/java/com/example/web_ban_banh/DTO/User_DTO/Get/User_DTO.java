package com.example.web_ban_banh.DTO.User_DTO.Get;

import com.example.web_ban_banh.Entity.Gender;
import com.example.web_ban_banh.Entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User_DTO {
    private int id;
    private String lastName;
    private String firstName;
    private String address;
    private Date dateOfBirth;
    private String phoneNumber;
    private Gender gender;
    private String email;
    private Role role;
}
