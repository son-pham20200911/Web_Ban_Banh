package com.example.web_ban_banh.Service.Cart_Details_Service;

import com.example.web_ban_banh.Entity.Cart;
import com.example.web_ban_banh.Entity.Cart_details;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Entity.Product_size;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Cart.Cart_RepoIn;
import com.example.web_ban_banh.Repository.Cart_details.Cart_details_RepoIn;
import com.example.web_ban_banh.Repository.Product.Product_RepoIn;
import com.example.web_ban_banh.Repository.Product_size.Product_size_RepoIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class Cart_Details_Service implements Cart_Details_ServiceIn {
    private Cart_details_RepoIn cartDetailsRepo;
    private Cart_RepoIn cartRepo;
    private Product_RepoIn productRepo;
    private Product_size_RepoIn productSizeRepo;

    @Autowired
    public Cart_Details_Service(Cart_details_RepoIn cartDetailsRepo, Cart_RepoIn cartRepo,Product_RepoIn productRepo,Product_size_RepoIn productSizeRepo) {
        this.cartDetailsRepo = cartDetailsRepo;
        this.cartRepo = cartRepo;
        this.productRepo=productRepo;
        this.productSizeRepo=productSizeRepo;
    }


    @Override
    @Transactional
    public void deleteCartDetail(int id) {
        Optional<Cart_details> cd = cartDetailsRepo.findById(id);
        if (cd.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Chi Tiết Giỏ Hàng có ID: " + id);
        }
        Cart_details cartDetails = cd.get();

        //Cắt kết nối từ phía Cart
        if (cartDetails.getCart() != null) {
            Cart cart = cartDetails.getCart();
            if (cart.getCartDetails() != null && !cart.getCartDetails().isEmpty()) {
                cart.getCartDetails().remove(cd);
                cartRepo.save(cart);
            }
            cartDetails.setCart(null);
        }

        //Cắt kết nối từ phía Product
        if (cartDetails.getProduct() != null) {
            Product product = cartDetails.getProduct();
            if (product.getCartDetails() != null && !product.getCartDetails().isEmpty()) {
                product.getCartDetails().remove(cd);
                productRepo.save(product);
            }
            cartDetails.setProduct(null);
        }

        //Cắt kết nối từ phía ProductSize
        if (cartDetails.getProductSize() != null) {
            Product_size productSize = cartDetails.getProductSize();
            if (productSize.getCartDetails() != null && !productSize.getCartDetails().isEmpty()) {
                productSize.getCartDetails().remove(cd);
                productSizeRepo.save(productSize);
            }
            cartDetails.setProductSize(null);
        }

      cartDetailsRepo.saveAndFlush(cartDetails);

      cartDetailsRepo.delete(cartDetails);
    }
}
