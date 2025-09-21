package com.example.web_ban_banh.Service.Cart_Details_Service;

import com.example.web_ban_banh.DTO.Cart_Details_DTO.Get.Cart_DetailsDTO;
import com.example.web_ban_banh.DTO.Cart_Details_DTO.Get.Cart_Details_Display_DTO;
import com.example.web_ban_banh.DTO.Cart_Details_DTO.Update.UpdateCartDetaisl_DTO;
import com.example.web_ban_banh.Entity.Cart_details;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Cart_Details_ServiceIn {
    public Cart_Details_Display_DTO updateCartDetails(int id,UpdateCartDetaisl_DTO update);
    public void deleteCartDetail (int id);
}
