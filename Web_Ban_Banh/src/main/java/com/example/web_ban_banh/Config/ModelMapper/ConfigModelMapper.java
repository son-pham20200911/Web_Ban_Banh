package com.example.web_ban_banh.Config.ModelMapper;

import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductDTO;
import com.example.web_ban_banh.Entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigModelMapper {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.typeMap(Product.class, ProductDTO.class)
                .addMappings(m->m.map(Product::getProductSizes,ProductDTO::setProductSizes));

        return modelMapper;
    }
}
