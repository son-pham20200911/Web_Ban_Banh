package com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequestDTO {
    @NotNull(message = "Không đuược để trống Id giỏ hàng")
    @Min(value = 1,message = "Id giỏ hàng phải lớn hơn 0")
    private int cartId;
    @NotBlank(message = "Không được để trống địa chỉ nhận hàng")
    @Length(max = 500,message = "Địa chỉ nhận hàng không được quá 500 ký tự")
    private String deliveryAddress; // Địa chỉ nhận hàng
    @NotBlank(message = "Hãy chọn phương thức thanh toán")
    private String paymentMethod; // "COD", "BANK_TRANSFER", "CREDIT_CARD", etc.
    private List<String> discountCodes; // Danh sách mã giảm giá
    @Length(max = 500,message = "Ghi chú không được quá 500 ký tự")
    private String note; // Ghi chú đơn hàng
}
