package com.example.web_ban_banh.Service.Category_Service;

import com.example.web_ban_banh.DTO.Category_DTO.Create.Create_Category_DTO;
import com.example.web_ban_banh.DTO.Category_DTO.Get.Category_DTO;
import com.example.web_ban_banh.DTO.Category_DTO.Update.Update_Category_DTO;
import com.example.web_ban_banh.Entity.Category;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Category.Category_RepoIn;
import com.example.web_ban_banh.Repository.Product.Product_RepoIn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Category_Service implements Category_ServiceIn {
    private Category_RepoIn categoryRepo;
    private Product_RepoIn productRepo;
    private ModelMapper modelMapper;

    @Autowired
    public Category_Service(Category_RepoIn categoryRepo, Product_RepoIn productRepo, ModelMapper modelMapper) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Category_DTO> getAllCategory() {
        List<Category> categorys = categoryRepo.findAll();
        List<Category_DTO> dtos = new ArrayList<>();
        for (Category c : categorys) {
            Category_DTO cateDTO = modelMapper.map(c, Category_DTO.class);
            dtos.add(cateDTO);
        }
        return dtos;
    }

    @Override
    @Transactional
    public Category_DTO createCategory(Create_Category_DTO dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        Category cate = categoryRepo.save(category);
        Category_DTO categoryDTO = modelMapper.map(cate, Category_DTO.class);
        return categoryDTO;
    }


    @Override
    @Transactional
    public Category_DTO updateCategory(int id, Update_Category_DTO dto) {
        Optional<Category> c = categoryRepo.findById(id);
        if (c.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Thể Loại có Id: " + id);
        }
        Category category = c.get();
        category.setCategoryName(dto.getCategoryName());

        if (dto.getProducts() != null && !dto.getProducts().isEmpty()) {
            List<Product> products = productRepo.findByProductnameIn(dto.getProducts());
            if (products != null && !products.isEmpty()) {
                if (products.size() != dto.getProducts().size()) {
                    throw new BadRequestExceptionCustom("Có một hoặc nhiều sản phẩm không tồn tại");
                }
            } else {
                throw new NotFoundExceptionCustom("Không tìm thấy sản phẩm nào cả");
            }
            for (Product product : products) {
                product.setCategory(category);
            }
            category.setProducts(products);
        }

        Category_DTO categoryDto = modelMapper.map(category, Category_DTO.class);

        return categoryDto;
    }

    @Override
    @Transactional
    public void deleteCategory(int id) {
        Optional<Category> c = categoryRepo.findById(id);
        if (c.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy thể loại có Id " + id);
        }
        Category category = c.get();

        List<Product> products = category.getProducts();
        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                product.setCategory(null);
                productRepo.save(product);
            }
            category.getProducts().clear();
        }
        categoryRepo.saveAndFlush(category);
        categoryRepo.delete(category);
    }

}

