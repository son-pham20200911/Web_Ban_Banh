package com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese;

import com.example.web_ban_banh.DTO.Order_Detail_DTO.Get.Order_Details_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserSecret_DTO;
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
public class CheckOutResponseDTO {
    private int orderId;
    private UserSecret_DTO user;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date orderDate;
    private Status status;
    private long totalAmount;
    private Double originalPrice;
    private Double promotionalPrice;
    private double discountAmount;
    private List<Order_Details_DTO>items;
    private String deliveryAddress;
    private String paymentMethod;
    private String paymentUrl;
    private String note;
}
