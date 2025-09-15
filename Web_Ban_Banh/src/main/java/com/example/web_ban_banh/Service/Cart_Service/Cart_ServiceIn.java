package com.example.web_ban_banh.Service.Cart_Service;

import com.example.web_ban_banh.DTO.Cart_DTO.Create.Create_CartDTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Cart_DTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Display_Cart_DTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Cart_ServiceIn {
    public Cart_DTO addItem(int userId, Create_CartDTO req);
    public void deleteCart(int id);

    public Page<Display_Cart_DTO>getAllCart(Pageable pageable);
    public Display_Cart_DTO findCartById(int id);
}
