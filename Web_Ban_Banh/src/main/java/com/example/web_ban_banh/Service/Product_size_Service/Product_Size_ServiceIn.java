package com.example.web_ban_banh.Service.Product_size_Service;

import com.example.web_ban_banh.DTO.Product_DTO.Update.Update_ProductDTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Create.Create_Product_size_DTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Get.Product_size_DTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Update.Update_Product_size_DTO;

import java.util.List;

public interface Product_Size_ServiceIn {
    public List<Product_size_DTO> getAllProductSize();
    public Product_size_DTO findProductSizeById(int id);
    public List<Product_size_DTO>findProductSizeByLabel(String label);

    public Create_Product_size_DTO createProductSize(Create_Product_size_DTO dto);

    public Update_Product_size_DTO updateProductSize(int id, Update_Product_size_DTO dto);

    public void deleteProductSize(int id);
}
