package com.example.web_ban_banh.Service.Cart_Details_Service;

import com.example.web_ban_banh.DTO.Cart_Details_DTO.Get.Cart_Details_Display_DTO;
import com.example.web_ban_banh.DTO.Cart_Details_DTO.Update.UpdateCartDetaisl_DTO;
import com.example.web_ban_banh.Entity.Cart;
import com.example.web_ban_banh.Entity.Cart_details;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Entity.Product_size;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Cart.Cart_RepoIn;
import com.example.web_ban_banh.Repository.Cart_details.Cart_details_RepoIn;
import com.example.web_ban_banh.Repository.Product.Product_RepoIn;
import com.example.web_ban_banh.Repository.Product_size.Product_size_RepoIn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class Cart_Details_Service implements Cart_Details_ServiceIn {
    private Cart_details_RepoIn cartDetailsRepo;
    private Cart_RepoIn cartRepo;
    private Product_RepoIn productRepo;
    private Product_size_RepoIn productSizeRepo;
    private ModelMapper modelMapper;

    @Autowired
    public Cart_Details_Service(Cart_details_RepoIn cartDetailsRepo, Cart_RepoIn cartRepo,Product_RepoIn productRepo,Product_size_RepoIn productSizeRepo,ModelMapper modelMapper) {
        this.cartDetailsRepo = cartDetailsRepo;
        this.cartRepo = cartRepo;
        this.productRepo=productRepo;
        this.productSizeRepo=productSizeRepo;
        this.modelMapper=modelMapper;
    }

    @Override
    @Transactional
    public Cart_Details_Display_DTO updateCartDetails(int id,UpdateCartDetaisl_DTO update) {
        Optional<Cart_details> cd=cartDetailsRepo.findById(id);
        if(cd.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy chi tiết giỏ hàng có id "+id);
        }
        Cart_details cartDetails=cd.get();
        //Check sô lượng hàng trong kho và số lượng hàng mà khách yêu cầu
        int stock=0;
        if(cartDetails.getProductSize()!=null && cartDetails.getProductSize().getQuantity()!=0){
            stock=cartDetails.getProductSize().getQuantity();
        }else if(cartDetails.getProduct().getQuantity()!=null){
            stock=cartDetails.getProduct().getQuantity();
        }else{
            stock=0;
        }
        if(stock< update.getProductQuantity()){
            throw new BadRequestExceptionCustom("Số lượng hàng bạn yêu cầu đang lớn hơn số lượng hàng trong kho");
        }
        cartDetails.setProductQuantity(update.getProductQuantity());

        //Cập nhật lại OriginalPrice và PromotionalPrice của Cart_Details sau khi cập nhật số lượng
        Double original=0.0;
        Double promo=0.0;
        if(cartDetails.getProductSize()!=null){
            original=cartDetails.getProductSize().getOriginalPrice()* update.getProductQuantity();
            promo=cartDetails.getProductSize().getPromotionalPrice()* update.getProductQuantity();
        }else{
            original=cartDetails.getProduct().getOriginalPrice()*update.getProductQuantity();
            promo=cartDetails.getProduct().getPromotionalPrice()* update.getProductQuantity();
        }
        cartDetails.setOriginalPrice(original);
        cartDetails.setPromotionalPrice(promo);

        //Cập nhật lại TỔNG OriginalPrice và PromotionalPrice của Cart sau khi cập nhật số lượng của Cart_Details
        if(cartDetails.getCart().getCartDetails()!=null && !cartDetails.getCart().getCartDetails().isEmpty()){
            List<Cart_details>cds =cartDetails.getCart().getCartDetails();
            Double originalAllCartDetails=0.0;
            Double promoAllCarDetails=0.0;
            for (Cart_details cad:cds) {
                originalAllCartDetails+=cad.getOriginalPrice();
                promoAllCarDetails+=cad.getPromotionalPrice();
            }
            cartDetails.getCart().setOriginalPrice(originalAllCartDetails);
            cartDetails.getCart().setPromotionalPrice(promoAllCarDetails);
            cartRepo.saveAndFlush(cartDetails.getCart());
        }

        Cart_details updated= cartDetailsRepo.saveAndFlush(cartDetails);


        Cart_Details_Display_DTO dto=modelMapper.map(updated,Cart_Details_Display_DTO.class);
        return dto;
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
                cart.getCartDetails().remove(cartDetails);
                cartRepo.save(cart);
            }
            //Cập nhật lại TỔNG OriginalPrice và PromotionalPrice của Cart sau khi cập nhật số lượng của Cart_Details
            //Phải cạp nhật tổng tiền trước khi setCart vì nếu setCart=null rồi mới cập nhật lại giá thì sẽ bị NullPointException
                List<Cart_details>cds =cartDetails.getCart().getCartDetails();
                Double originalAllCartDetails=0.0;
                Double promoAllCarDetails=0.0;
                for (Cart_details cad:cds) {
                    originalAllCartDetails+=cad.getOriginalPrice();
                    promoAllCarDetails+=cad.getPromotionalPrice();
                }
                cartDetails.getCart().setOriginalPrice(originalAllCartDetails);
                cartDetails.getCart().setPromotionalPrice(promoAllCarDetails);
                cartRepo.saveAndFlush(cartDetails.getCart());

            cartDetails.setCart(null);
        }


        //Cắt kết nối từ phía Product
        if (cartDetails.getProduct() != null) {
            Product product = cartDetails.getProduct();
            if (product.getCartDetails() != null && !product.getCartDetails().isEmpty()) {
                product.getCartDetails().remove(cartDetails);
                productRepo.save(product);
            }
            cartDetails.setProduct(null);
        }

        //Cắt kết nối từ phía ProductSize
        if (cartDetails.getProductSize() != null) {
            Product_size productSize = cartDetails.getProductSize();
            if (productSize.getCartDetails() != null && !productSize.getCartDetails().isEmpty()) {
                productSize.getCartDetails().remove(cartDetails);
                productSizeRepo.save(productSize);
            }
            cartDetails.setProductSize(null);
        }

      cartDetailsRepo.saveAndFlush(cartDetails);

      cartDetailsRepo.delete(cartDetails);
    }
}
