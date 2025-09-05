package com.example.web_ban_banh.Repository.Cart_details;

import com.example.web_ban_banh.Entity.Cart;
import com.example.web_ban_banh.Entity.Cart_details;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Entity.Product_size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface Cart_details_RepoIn extends JpaRepository<Cart_details,Integer> {
    @Query("SELECT cd FROM Cart_details cd WHERE cd.cart.id=:cartId AND cd.product.id=:productId AND cd.productSize.id=:productSizeId")
    Optional<Cart_details>findByCartAndProductAndProductSize(@Param("cartId") int cartid,@Param("productId") int productid, @Param("productSizeId") int productSizeid);

    @Query("SELECT cd FROM Cart_details cd WHERE cd.cart.id=:cartId AND cd.product.id=:productId")
    Optional<Cart_details>findByCartAndProductAndProductSizeIsNull(@Param("cartId")int cartId, @Param("productId")int productId);

    @Query("SELECT cd FROM Cart_details cd WHERE cd.cart.id=:cartId")
    List<Cart_details>findAllByCart(@Param("cartId") int cartId);
}
