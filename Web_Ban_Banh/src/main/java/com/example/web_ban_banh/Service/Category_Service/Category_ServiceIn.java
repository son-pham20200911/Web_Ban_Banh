package com.example.web_ban_banh.Service.Category_Service;

import com.example.web_ban_banh.DTO.Category_DTO.Create.Create_Category_DTO;
import com.example.web_ban_banh.DTO.Category_DTO.Get.Category_DTO;
import com.example.web_ban_banh.DTO.Category_DTO.Update.Update_Category_DTO;

import java.util.List;

public interface Category_ServiceIn {
    public List<Category_DTO> getAllCategory();
    public Category_DTO createCategory( Create_Category_DTO dto);
    public Category_DTO updateCategory(int id, Update_Category_DTO dto);
    public void deleteCategory(int id);
}
