package com.example.web_ban_banh.Service.Order_Service;

import com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest.CheckOutRequestDTO;
import com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese.CheckOutResponseDTO;
import com.example.web_ban_banh.DTO.Order_DTO.Get.OrderDTO;
import com.example.web_ban_banh.Entity.Status;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Map;

public interface Order_ServicenIn {
    public CheckOutResponseDTO processCheckout(int userId, CheckOutRequestDTO request, HttpServletRequest httpServletRequest);

    public Page<OrderDTO> getAllOrder(Pageable pageable);
    public OrderDTO findById(int id);
    public Page<OrderDTO> filter(Pageable pageable, Date orderDate,Status status,Double totalAmount);
    public void handleVNPayCallback(Map<String, String> params, boolean isReturnUrl);

}
