package com.example.web_ban_banh.Repository.Product_size;

import com.example.web_ban_banh.Entity.Product_size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface Product_size_RepoIn extends JpaRepository<Product_size,Integer> {
    public List<Product_size> findByLabelIn(List<String> label);

    @Query(value = "SELECT * FROM product_size  WHERE label LIKE CONCAT ( '%',?1,'%')",nativeQuery = true)
    public List<Product_size> findProductSizeByLabel(String label);

}
