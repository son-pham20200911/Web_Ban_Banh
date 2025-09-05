package com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequestDTO {
    private int cartId;
    private String deliveryAddress; // Địa chỉ nhận hàng
    private String paymentMethod; // "COD", "BANK_TRANSFER", "CREDIT_CARD", etc.
    private List<String> discountCodes; // Danh sách mã giảm giá
    private String note; // Ghi chú đơn hàng
}
