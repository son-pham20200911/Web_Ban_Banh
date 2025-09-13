package com.example.web_ban_banh.Service.Product_size_Service;

import com.example.web_ban_banh.DTO.Product_DTO.Update.Update_ProductDTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Create.Create_Product_size_DTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Get.Product_size_DTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Update.Update_Product_size_DTO;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Entity.Product_size;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Product_size.Product_size_RepoIn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Product_Size_Service implements Product_Size_ServiceIn{
    private Product_size_RepoIn productSizeRepo;
    private ModelMapper modelMapper;

    @Autowired
    public Product_Size_Service(Product_size_RepoIn productSizeRepo,ModelMapper modelMapper) {
        this.productSizeRepo = productSizeRepo;
        this.modelMapper=modelMapper;
    }

    //Hiển thị toàn bộ Product Size
    @Override
    @Transactional(readOnly = true)
    public List<Product_size_DTO> getAllProductSize() {
        List<Product_size> productSizes=productSizeRepo.findAll();

        List<Product_size_DTO>productSizeDTOs=new ArrayList<>();
        for (Product_size pro:productSizes) {
            Product_size_DTO productSizeDTO=modelMapper.map(pro,Product_size_DTO.class);
            productSizeDTOs.add(productSizeDTO);
        }

        return productSizeDTOs;
    }

    //Tìm ProductSize theo id
    @Override
    @Transactional(readOnly = true)
    public Product_size_DTO findProductSizeById(int id) {
        Optional<Product_size> productSize=productSizeRepo.findById(id);
        if(productSize.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy Kích Thước Sản Phẩm có ID: "+id);
        }
        Product_size pz=productSize.get();
        Product_size_DTO productSizeDTO=modelMapper.map(pz,Product_size_DTO.class);

        return productSizeDTO;
    }

    //Tìm ProductSize theo label
    @Override
    @Transactional(readOnly = true)
    public List<Product_size_DTO> findProductSizeByLabel(String label) {
        List<Product_size>productSizes=productSizeRepo.findProductSizeByLabel(label);
        if(productSizes.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy bất cứ Kích Thước sản phẩm nào có tên: "+label);
        }

        List<Product_size_DTO>productSizeDTO=new ArrayList<>();
        for (Product_size productSize:productSizes) {
            Product_size_DTO dto=modelMapper.map(productSize,Product_size_DTO.class);
            productSizeDTO.add(dto);
        }
        return productSizeDTO;
    }

    //Phương thức tạo ProductSize
    @Override
    @Transactional
    public Create_Product_size_DTO createProductSize(Create_Product_size_DTO dto) {

        Product_size productSize=new Product_size();
        productSize.setLabel(dto.getLabel());
        productSize.setProductName(dto.getProductName());
        productSize.setOriginalPrice(dto.getOriginalPrice());
        productSize.setPromotionalPrice(dto.getPromotionalPrice());
        productSize.setQuantity(dto.getQuantity());

        Product_size create=productSizeRepo.save(productSize);

        Create_Product_size_DTO productSizeDTO=modelMapper.map(create,Create_Product_size_DTO.class);

        return productSizeDTO;
    }

    //Phương thức Cập Nhật ProductSize
    @Override
    @Transactional
    public Update_Product_size_DTO updateProductSize(int id, Update_Product_size_DTO dto) {
        Optional<Product_size> productSize=productSizeRepo.findById(id);
        if(productSize.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy Kích Thước sản phẩm có ID: "+id);
        }
        Product_size pz=productSize.get();
        pz.setLabel(dto.getLabel());
        pz.setProductName(dto.getProductName());
        pz.setOriginalPrice(dto.getOriginalPrice());
        pz.setPromotionalPrice(dto.getPromotionalPrice());
        pz.setQuantity(dto.getQuantity());

        Product_size update=productSizeRepo.saveAndFlush(pz);
        Update_Product_size_DTO productSizeDTO=modelMapper.map(update,Update_Product_size_DTO.class);

        return productSizeDTO;
    }

    //Phương thức Xóa ProductSize
    @Override
    @Transactional
    public void deleteProductSize(int id) {
        Optional<Product_size> productSize=productSizeRepo.findById(id);
        if(productSize.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy Kích Thước sản phẩm có ID: "+id);
        }
        Product_size pz= productSize.get();
        productSizeRepo.delete(pz);
    }


}
