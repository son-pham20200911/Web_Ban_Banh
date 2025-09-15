package com.example.web_ban_banh.DTO.Order_DTO.Get;

import com.example.web_ban_banh.DTO.Discount_Code_DTO.Get.Discount_Code_OrderDTO;
import com.example.web_ban_banh.DTO.Order_Detail_DTO.Get.Order_Details_DTO;
import com.example.web_ban_banh.DTO.Order_Detail_DTO.Get.Order_Details_Order_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.User_Order_DTO;
import com.example.web_ban_banh.Entity.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private int orderId;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date orderDate;
    private Status status;
    private double totalAmount;
    private Discount_Code_OrderDTO discountCodes;
    private List<Order_Details_Order_DTO>orderDetails;
    private User_Order_DTO user;
    private String deliveryAddress;
    private String note;
}
