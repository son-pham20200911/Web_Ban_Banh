package com.example.web_ban_banh.Service.Cart_Service;

import com.example.web_ban_banh.DTO.Cart_DTO.Create.Create_CartDTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Cart_DTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Display_Cart_All_DTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Display_Cart_DTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface Cart_ServiceIn {
    public Cart_DTO addItem(int userId, Create_CartDTO req);
    public void deleteCart(int id);

    public List<Display_Cart_All_DTO> getAllCartFromUser(int id);
    public Display_Cart_DTO findCartById(int id);
}
