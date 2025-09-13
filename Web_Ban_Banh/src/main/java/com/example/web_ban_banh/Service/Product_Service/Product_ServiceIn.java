package com.example.web_ban_banh.Service.Product_Service;

import com.example.web_ban_banh.DTO.Product_DTO.Create.Create_ProductDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductHideProductSizeDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Update.Update_ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface Product_ServiceIn {
      public Page<ProductDTO> getAllProduct(Pageable pageable);
      public ProductDTO findProductById(int id);
      public Page<ProductDTO> findProductByProductName(String productname,Pageable pageable);
      public List<ProductDTO> findProductBetweenPriceImprove(double a, double b);
      public List<ProductDTO>zToA();
      public List<ProductDTO>atoZ();
      public List<ProductDTO>lowToHight();
      public List<ProductDTO>highToLow();

      public ProductDTO createProduct(Create_ProductDTO dto);

      public ProductDTO updateProduct(int id,Update_ProductDTO dto) throws IOException;

      public void deleteProduct(int id);

}
