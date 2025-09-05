package com.example.web_ban_banh.Service.User_Service;

import com.example.web_ban_banh.DTO.Register_DTO.RegisterUserRequest_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.User_DTO;

public interface User_ServiceIn {
    public User_DTO register(RegisterUserRequest_DTO request);
}
