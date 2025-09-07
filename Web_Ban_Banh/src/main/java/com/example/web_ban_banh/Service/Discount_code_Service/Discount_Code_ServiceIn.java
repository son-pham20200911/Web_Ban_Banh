package com.example.web_ban_banh.Service.Discount_code_Service;

import com.example.web_ban_banh.DTO.Discount_Code_DTO.Create.Create_Discount_Code_DTO;
import com.example.web_ban_banh.DTO.Discount_Code_DTO.Get.Discount_CodeDTO;
import com.example.web_ban_banh.DTO.Discount_Code_DTO.Update.Update_Discount_Code_DTO;
import com.example.web_ban_banh.Entity.Discount_code;

import java.util.List;

public interface Discount_Code_ServiceIn {
    public List<Discount_CodeDTO> getAllDiscountCode();
    public Discount_CodeDTO getDiscountCodeByCode(String code);
    public Discount_CodeDTO createDiscountCode(Create_Discount_Code_DTO create);
    public Discount_CodeDTO updateDiscountCode(int id,Update_Discount_Code_DTO update);
    public void deleteDiscountCode(int id);
}
