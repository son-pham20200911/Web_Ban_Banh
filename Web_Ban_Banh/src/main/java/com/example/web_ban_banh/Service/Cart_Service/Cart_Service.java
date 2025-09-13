package com.example.web_ban_banh.Service.Cart_Service;

import com.example.web_ban_banh.DTO.Cart_DTO.Create.Create_CartDTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Cart_DTO;
import com.example.web_ban_banh.DTO.Cart_Details_DTO.Get.Cart_DetailsDTO;
import com.example.web_ban_banh.Entity.*;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Cart.Cart_RepoIn;
import com.example.web_ban_banh.Repository.Cart_details.Cart_details_RepoIn;
import com.example.web_ban_banh.Repository.Product.Product_RepoIn;
import com.example.web_ban_banh.Repository.Product_size.Product_size_RepoIn;
import com.example.web_ban_banh.Repository.User.User_RepoIn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class Cart_Service implements Cart_ServiceIn {
    private Cart_RepoIn cartRepo;
    private Cart_details_RepoIn cartDetailRepo;
    private Product_RepoIn productRepo;
    private Product_size_RepoIn productSizeRepo;
    private User_RepoIn userRepo;
    private ModelMapper modelMapper;

    @Autowired
    public Cart_Service(Cart_RepoIn cartRepo, Cart_details_RepoIn cartDetailRepo, Product_RepoIn productRepo, Product_size_RepoIn productSizeRepo, User_RepoIn userRepo, ModelMapper modelMapper) {
        this.cartRepo = cartRepo;
        this.cartDetailRepo = cartDetailRepo;
        this.productRepo = productRepo;
        this.productSizeRepo = productSizeRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Cart_DTO addItem(int userId, Create_CartDTO req) {

        //1 Lấy User và Giỏ Hàng hiện tại (tạo mới nếu chưa có Giỏ Hàng)
        // Tìm và xác thực User: Xác minh user tồn tại trong hệ thống hay không.
        Optional<User> u = userRepo.findById(userId);
        if (u.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy User có Id: " + userId);
        }
        User user = u.get();

        //Tìm giỏ hàng ACTIVE gần nhất của user (OrderByIdDesc = sắp xếp theo ID Giỏ Hàng giảm dần (Giỏ Hàng có ID lớn nhất (mới nhât)))
        //Nếu không có, tạo giỏ hàng mới với trạng thái ACTIVE

        //OrderByIdDesc: Nghĩa là sắp xếp giảm dần theo ID → tức là giỏ hàng nào mới nhất sẽ nằm đầu tiên.
        //findFirst... : Nghĩa là chỉ lấy 1 giỏ hàng mới nhất.
        Optional<Cart> c = cartRepo.findFirstByUserAndStatusOrderByIdDesc(userId, CartStatus.ACTICE);
        Cart cart;
        if (c.isPresent()) {
            // Nếu đã có giỏ thì lấy giỏ đó ra
            cart = c.get();
        } else {
            // Nếu chưa có giỏ thì tạo mới
            Cart cc = new Cart();
            cc.setUser(user);
            cc.setCartingDate(new Date(System.currentTimeMillis()));
            cc.setOriginalPrice(0.0);
            cc.setPromotionalPrice(0.0);
            cc.setStatus(CartStatus.ACTICE);

            // Lưu giỏ hàng mới vào DB
            cart = cartRepo.save(cc);
        }

        // 2) Validate product & size
        // Check Product và Product_Size mà User nhập vào có tồn tại không
        Optional<Product> p = productRepo.findById(req.getProductId());
        if (p.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Sản Phẩm có ID: " + req.getProductId());
        }
        Product product = p.get();

        Product_size productSize=null;
        if(req.getProductSizeId()!=null && req.getProductSizeId()>0) {
            Optional<Product_size> pz = productSizeRepo.findById(req.getProductSizeId());
            if (pz.isEmpty()) {
                throw new NotFoundExceptionCustom("Không tìm thấy Kích Thước Sản Phẩm có ID: " + req.getProductSizeId());
            }
            productSize = pz.get();

            //Check xem ProductSize có thuộc Product không
            boolean linked = false;
            for (Product ps : productSize.getProducts()) {
                if (ps.getId() == product.getId()) {        //Nếu Id của Product trong ProductSize mà GIỐNG ID Product mà User nhập vào
                    linked = true;                          //Thì ProductSize thuộc Product đó
                    break;
                }
            }
            if (!linked) {
                throw new BadRequestExceptionCustom("Kích Thước này không thuộc Sản phẩm này");
            }
        }

        //Check số lượng hàng trong sản phẩm có đáp ứng được số lượng khách mong muốn không
        Integer stock = 0;
        //Nếu có ProductSize và Số Lượng của ProductSize vẫn còn thì ta sẽ lấy số lượng của ProductSize đó
        if (productSize!=null && productSize.getQuantity() != null) {
            stock = productSize.getQuantity();
            //Nếu Số Lượng của ProductSize không còn thì ta sẽ chuyển sang Product
        } else {
            //Nếu Số Lượng của Product vẫn còn thì ta sẽ lấy số lượng của Product đó
            if (product.getQuantity() != null) {
                stock = product.getQuantity();
            } else {
                //Nếu Số Lượng của Product không còn thì sẽ là 0
                stock = 0;
            }
        }

        if (req.getQuantity() > stock) {
            throw new BadRequestExceptionCustom("Số lượng hàng bạn yêu cầu lớn hơn số lượng hàng có trong kho");
        }

        // 3) Tính đơn giá tại thời điểm thêm giỏ (chốt giá vào cart_details)
        //
        // Ưu tiên giá theo size; nếu ProductSize là null và nếu size không có giá, fallback về giá product.

        Double originalUnitPrice = (productSize!=null && productSize.getOriginalPrice() != null) ? productSize.getOriginalPrice()
                : ((product.getOriginalPrice() != null) ? product.getOriginalPrice() : 0.0);
        Double promoUnitPrice = (productSize!=null && productSize.getPromotionalPrice() != null) ? productSize.getPromotionalPrice()
                : ((product.getPromotionalPrice() != null) ? product.getPromotionalPrice() : 0.0);


        // 4) Gộp dòng nếu đã tồn tại (cart, product, size)
        // Check xem CartDetail đã tồn tại trong Cart chưa
        Cart_details item;
        if(productSize!=null){
            item = cartDetailRepo.findByCartAndProductAndProductSize(cart.getId(), product.getId(), productSize.getId()).orElse(null);
        }else{
            item=cartDetailRepo.findByCartAndProductAndProductSizeIsNull(cart.getId(),product.getId()).orElse(null);
        }
        //Nếu chưa thì tạo CartDetail mới
        if (item == null) {
            item = new Cart_details();
            item.setCart(cart);
            item.setProduct(product);
            item.setProductSize(productSize);
            item.setProductQuantity(req.getQuantity());
            item.setOriginalPrice(req.getQuantity() * originalUnitPrice);
            item.setPromotionalPrice(req.getQuantity() * promoUnitPrice);
        } else {
            Integer newQty = item.getProductQuantity() + req.getQuantity();
            item.setProductQuantity(newQty);
            item.setOriginalPrice(originalUnitPrice * newQty);
            item.setPromotionalPrice(promoUnitPrice * newQty);
        }
        cartDetailRepo.save(item);

        // 5) Re-calc tổng tiền giỏ
        CartTotalPrice(cart);

        // 6) Build response
        List<Cart_details> details = cartDetailRepo.findAllByCart(cart.getId());
        List<Cart_DetailsDTO> items = new ArrayList<>();
        for (Cart_details cd : details) {
            Cart_DetailsDTO respone = new Cart_DetailsDTO(
            cd.getId(),
            cd.getProduct().getId(),
            cd.getProductSize()!=null?cd.getProductSize().getId():0,
            cd.getProductQuantity(),
            cd.getOriginalPrice(),
            cd.getPromotionalPrice());
        items.add(respone);
        }

        return new Cart_DTO(cart.getId(),items,cart.getOriginalPrice(),cart.getPromotionalPrice());
    }

    //Hàm tổng giá của Cart_Detail để gán vào Cart
    public void CartTotalPrice(Cart cart) {
        List<Cart_details>cartDetails=cartDetailRepo.findAllByCart(cart.getId());
        Double origin = 0.0;
        Double promo = 0.0;
        for (Cart_details cd : cartDetails) {
            origin += (cd.getOriginalPrice() != null) ? cd.getOriginalPrice() : 0.0;
            promo += (cd.getPromotionalPrice() != null) ? cd.getPromotionalPrice() : 0.0;
        }
        cart.setOriginalPrice(origin);
        cart.setPromotionalPrice(promo);
        cartRepo.save(cart);
    }


    //Phương thức xóa Giỏ Hàng theo ID
    @Override
    @Transactional
    public void deleteCart(int id) {
      Optional<Cart>c=cartRepo.findById(id);
      if(c.isEmpty()){
          throw new NotFoundExceptionCustom("Không tìm thấy Giỏ Hàng có Id: "+id);
      }
      Cart cart=c.get();

      if(cart.getUser()!=null){
          User user=cart.getUser();
          if(user.getCarts()!=null&&!user.getCarts().isEmpty()){
              user.getCarts().remove(cart);
              userRepo.save(user);
          }
          cart.setUser(null);
      }

      cartRepo.saveAndFlush(cart);
      cartRepo.delete(cart);
    }
}
