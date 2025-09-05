package com.example.web_ban_banh.Service.Order_Service;

import com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest.CheckOutRequestDTO;
import com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese.CheckOutResponseDTO;

public interface Order_ServicenIn {
    public CheckOutResponseDTO processCheckout(int userId, CheckOutRequestDTO request);
}
