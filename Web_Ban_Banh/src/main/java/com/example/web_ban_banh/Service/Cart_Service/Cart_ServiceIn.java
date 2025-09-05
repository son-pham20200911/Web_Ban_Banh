package com.example.web_ban_banh.Service.Cart_Service;

import com.example.web_ban_banh.DTO.Cart_DTO.Create.Create_CartDTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Cart_DTO;

public interface Cart_ServiceIn {
    public Cart_DTO addItem(int userId, Create_CartDTO req);
    public void deleteCart(int id);
}
