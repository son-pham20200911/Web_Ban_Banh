package com.example.web_ban_banh.Service.Order_Service;

import com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest.CheckOutRequestDTO;
import com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese.CheckOutResponseDTO;
import com.example.web_ban_banh.DTO.Order_DTO.Get.OrderDTO;
import com.example.web_ban_banh.DTO.Order_Detail_DTO.Get.Order_Details_DTO;
import com.example.web_ban_banh.Entity.*;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Cart.Cart_RepoIn;
import com.example.web_ban_banh.Repository.Cart_details.Cart_details_RepoIn;
import com.example.web_ban_banh.Repository.Discount_code.Discount_code_RepoIn;
import com.example.web_ban_banh.Repository.Order.Order_RepoIn;
import com.example.web_ban_banh.Repository.Order_details.Order_details_RepoIn;
import com.example.web_ban_banh.Repository.Product.Product_RepoIn;
import com.example.web_ban_banh.Repository.Product_size.Product_size_RepoIn;
import com.example.web_ban_banh.Repository.User.User_RepoIn;
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class Order_Service implements Order_ServicenIn {
    private Cart_RepoIn cartRepo;
    private Cart_details_RepoIn cartDetailsRepo;
    private Order_RepoIn orderRepo;
    private Order_details_RepoIn orderDetailRepo;
    private Product_RepoIn productRepo;
    private Product_size_RepoIn productSizeRepo;
    private User_RepoIn userRepo;
    private Discount_code_RepoIn discountCodeRepo;
    private ModelMapper modelMapper;

    @Autowired
    public Order_Service(Cart_RepoIn cartRepo, Cart_details_RepoIn cartDetailsRepo, Order_RepoIn orderRepo, Order_details_RepoIn orderDetailRepo, Product_RepoIn productRepo, Product_size_RepoIn productSizeRepo, User_RepoIn userRepo, Discount_code_RepoIn discountCodeRepo,ModelMapper modelMapper) {
        this.cartRepo = cartRepo;
        this.cartDetailsRepo = cartDetailsRepo;
        this.orderRepo = orderRepo;
        this.orderDetailRepo = orderDetailRepo;
        this.productRepo = productRepo;
        this.productSizeRepo = productSizeRepo;
        this.userRepo = userRepo;
        this.discountCodeRepo = discountCodeRepo;
        this.modelMapper=modelMapper;
    }

    @Override
    @Transactional
    public CheckOutResponseDTO processCheckout(int userId, CheckOutRequestDTO request) {
        //Kiểm tra user có tồn tại không
        Optional<User> u = userRepo.findById(userId);
        if (u.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tim thấy User có Id: " + userId);
        }
        User user = u.get();

        //Kiểm tra cart có tồn tại không
        Optional<Cart> c = cartRepo.findById(request.getCartId());
        if (c.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Giỏ Hàng có Id: " + request.getCartId());
        }
        Cart cart = c.get();

        //Kiểm tra xem Cart này có thuộc User này không
        if (!Objects.equals(userId, cart.getUser().getId())) {
            throw new BadRequestExceptionCustom("Giỏ Hàng này không thuộc về User này");
        }

        //Kiêm tra trạng thái Cart
        if (cart.getStatus() != CartStatus.ACTICE) {
            throw new BadRequestExceptionCustom("Giỏ Hàng này không ở trạng thái hoạt động");
        }

        //Kiểm tra trong Cart có Cart_Detail không
        if (cart.getCartDetails().isEmpty()) {
            throw new BadRequestExceptionCustom("Giỏ hàng trống, không thể thanh toán");
        }

        //Vallidate Stock và cập nhật số lượng
        List<Cart_details> cartDetails = cartDetailsRepo.findAllByCart(request.getCartId());
        for (Cart_details cartDetail : cartDetails) {
            validateAndUpdateStock(cartDetail);
        }

        //Tạo Order mới
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setStatus(Status.PENDING);
        order.setOriginalPrice(cart.getOriginalPrice());
        order.setPromotionalPrice(cart.getPromotionalPrice());

        //Kiểm tra mã giảm giá có tồn tại không và thời gian áp dụng mã có đúng không
        List<Discount_code> validDiscountCodes = new ArrayList<>();
        double discountAmount = 0.0;

        //Nếu người dùng CÓ NHẬP mã giẩm giá
        if (request.getDiscountCodes() != null && !request.getDiscountCodes().isEmpty()) {
            for (String discountCodeStr : request.getDiscountCodes()) {
                Optional<Discount_code> discountOpt = discountCodeRepo.findByCodeAndActivsted(discountCodeStr, true);
                //Nếu mã giảm giá KHÔNG tồn tại
                if (discountOpt.isEmpty()) {
                    throw new NotFoundExceptionCustom("Không tìm thấy mã giảm giá mà bạn đang tìm");
                }
                //Nếu mã giảm giá tồn tại
                Discount_code discountCode = discountOpt.get();
                //Kiểm tra thời gian của mã giảm giá
                Date now = new Date(System.currentTimeMillis());
                if (now.after(discountCode.getStartDate()) && now.before(discountCode.getEndDate())) {
                    validDiscountCodes.add(discountCode);
                    discountAmount += discountCode.getValue();
                } else {
                    throw new BadRequestExceptionCustom("Thời gian áp mã chưa đến hoặc đã hết ");
                }
            }
        }

        //Tính tổng tiền cuối cùng
        double finalAmount = cart.getPromotionalPrice() - discountAmount;
        if (finalAmount < 0) {
            throw new BadRequestExceptionCustom("Tổng tiền không thể âm. Mã giảm giá vượt quá giá trị đơn hàng");
        }
        order.setTotalAmount(finalAmount);
        order.setDiscountCodes(validDiscountCodes);

        //Lưu Order
        Order savedOrder = orderRepo.save(order);

        //Tạo OrderDetail
        List<Order_Details_DTO> orderItems = new ArrayList<>();
        for (Cart_details cartDetail : cartDetails) {
            Order_details orderDetail = new Order_details();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(cartDetail.getProduct());
            orderDetail.setProductSize(cartDetail.getProductSize());
            orderDetail.setQuantity(cartDetail.getProductQuantity());
            orderDetail.setOriginalPrice(cartDetail.getOriginalPrice());
            orderDetail.setPromotionalPrice(cartDetail.getPromotionalPrice());

            orderDetailRepo.save(orderDetail);

            //Tạo OrderItemDTO cho CheckoutResponse

            Order_Details_DTO orderItem = new Order_Details_DTO();
            orderItem.setProductId(cartDetail.getProduct().getId());
            orderItem.setProductName(cartDetail.getProduct().getProductname());
            if (cartDetail.getProductSize() != null) {
                orderItem.setProductSizeId(cartDetail.getProductSize().getId());
                orderItem.setProductLabel(cartDetail.getProductSize().getLabel());
            }
            orderItem.setQuantity(cartDetail.getProductQuantity());
            orderItem.setOriginalPrice(cartDetail.getOriginalPrice());
            orderItem.setPromotionalPrice(cartDetail.getPromotionalPrice());

            orderItems.add(orderItem);
        }
        //Cập nhật trạng thái Cart thành ORDERED
        cart.setStatus(CartStatus.ORDERED);
        cartRepo.save(cart);

        //Tạo CheckoutRespone
        CheckOutResponseDTO respone=new CheckOutResponseDTO();
        respone.setOrderId(savedOrder.getId());
        respone.setOrderDate(savedOrder.getOrderDate());
        respone.setStatus(savedOrder.getStatus());
        respone.setOriginalPrice(savedOrder.getOriginalPrice());
        respone.setPromotionalPrice(savedOrder.getPromotionalPrice());
        respone.setDiscountAmount(discountAmount);
        respone.setTotalAmount(savedOrder.getTotalAmount());
        respone.setItems(orderItems);
        respone.setDeliveryAddress(request.getDeliveryAddress());
        respone.setPaymentMethod(request.getPaymentMethod());
        respone.setNote(request.getNote());

        return respone;
    }

    //Hàm KIỂM TRA số lượng sản phẩm mà khách nhập vào có nhiều hơn số lượng sản phẩm trong kho không (Nếu không thì cập nhật lại kho)
    private void validateAndUpdateStock(Cart_details cartDetail) {
        Product product = cartDetail.getProduct();
        Product_size productSize = cartDetail.getProductSize();
        int requestedQuantity = cartDetail.getProductQuantity(); //Đây là số lượng sản phẩm của User trong Cart

        //Kiểm tra và cập nhật Stock của productSize
        if (productSize != null && productSize.getQuantity() != null) {
            if (productSize.getQuantity() < requestedQuantity) {
                throw new BadRequestExceptionCustom("Số lượng sản phẩm " + product.getProductname() + "( " + productSize.getLabel() + " ) " + " chỉ còn " + productSize.getQuantity() + " trong kho");
            }
            // Cập nhật stock
            productSize.setQuantity(productSize.getQuantity() - requestedQuantity);
            productSizeRepo.save(productSize);
        } else if (product.getQuantity() != null) {
            // Fallback về stock của product
            if (product.getQuantity() < requestedQuantity) {
                throw new BadRequestExceptionCustom("Sản phẩm " + product.getProductname() + " chỉ còn " + product.getQuantity() + " sản phẩm trong kho");
            }
            // Cập nhật stock
            product.setQuantity(product.getQuantity() - requestedQuantity);
            productRepo.save(product);
        } else {
            throw new BadRequestExceptionCustom("Sản phẩm " + product.getProductname() + " đã hết hàng");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrder(Pageable pageable) {
        Page<Order>orders=orderRepo.findAll(pageable);
        return orders.map(order->{
            OrderDTO dto=modelMapper.map(order,OrderDTO.class);
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findById(int id) {
        Optional<Order>o=orderRepo.findById(id);
        if(o.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy đơn hàng có id "+id);
        }
        Order order=o.get();
        OrderDTO dto=modelMapper.map(order,OrderDTO.class);
        return dto;
    }

    //Phương thức tìm Order theo ba tiêu chí: Status, orderDate, totalAmount
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> filter(Pageable pageable, Date orderDate, Status status, Double totalAmount) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orderDate != null) {
                predicates.add(cb.equal(cb.function("DATE", Date.class, root.get("orderDate")), orderDate));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (totalAmount != null) {
                predicates.add(cb.equal(root.get("totalAmount"), totalAmount));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Order>orders=orderRepo.findAll(spec,pageable);
        return orders.map(order->modelMapper.map(order,OrderDTO.class));
    }
}
