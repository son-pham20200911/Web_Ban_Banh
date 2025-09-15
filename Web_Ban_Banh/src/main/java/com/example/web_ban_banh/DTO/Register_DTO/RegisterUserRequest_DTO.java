package com.example.web_ban_banh.DTO.Register_DTO;

import com.example.web_ban_banh.Entity.Gender;
import com.example.web_ban_banh.Entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest_DTO {
    @NotBlank(message = "Không được để trống Họ và Tên")
    @Length(max = 255, message = "Số lượng ký tự không được quá 255")
    private String fullName;

    @NotBlank(message = "Không được để trống Đại Chỉ")
    @Length(max = 255, message = "Số lượng ký tự không được quá 500")
    private String address;

    @PastOrPresent(message = "Ngày Sinh phải là ngày trong quá khứ")
    @NotNull(message = "Không được để trống Ngày Sinh")
    @JsonFormat(pattern = "dd/MM/yyy")
    private Date dateOfBirth;

    @NotBlank(message = "Không được để trống Số Điện Thoại")
    @Pattern(regexp = "^0[0-9]{9}$",message = "Số Điện Thoại phải có 10 chữ số và bắt đầu bằng số 0")
    private String phoneNumber;

    @NotNull(message = "Không được để trống Giới Tính")
    private Gender gender;

    @NotBlank(message = "Không được để trống Email")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*_-]+(@gmail.com)$",message = "Email của bạn phải đúng định dạng")
    private String email;
    private Role role;

    @NotBlank(message = "Không được để trống Username")
    @Length(max = 500,message = "Username không được quá 500 ký tự")
    private String userName;

    @NotBlank(message = "Không được để trống Password")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_])[a-zA-Z0-9!@#$%^&*()_]{8,20}$",
            message = "Password phải có 1 chữ in hoa, 1 chữ thường, 1 ký tự đặc biệt !@#$%^&*()_ và số lượng ký tự từ 8 đến 20 ký tự")
    private String password;

}
