package com.example.web_ban_banh.Service.User_Service;

import com.example.web_ban_banh.DTO.Register_DTO.RegisterUserRequest_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserPublic_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserSecret_DTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface User_ServiceIn {
    public Page<UserSecret_DTO>getAllUser(Pageable pageable);
    public UserPublic_DTO findById(int id);
    public Page<UserSecret_DTO> findByFullName(String fullName, Pageable pageable);
    public UserPublic_DTO findByUsername(String username);

    public UserPublic_DTO register(RegisterUserRequest_DTO request);

    public UserSecret_DTO updateUser(int id,RegisterUserRequest_DTO update);

    public void deleteUser(int id);

    public void sendPasswordResetEmail(String email);
    public void resetPassword(String token, String newPassword);
}
