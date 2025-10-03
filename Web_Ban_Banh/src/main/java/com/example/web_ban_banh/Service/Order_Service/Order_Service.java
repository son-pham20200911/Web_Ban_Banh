package com.example.web_ban_banh.Service.Order_Service;

import com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest.CheckOutRequestDTO;
import com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese.CheckOutResponseDTO;
import com.example.web_ban_banh.DTO.Order_DTO.Get.OrderDTO;
import com.example.web_ban_banh.DTO.Order_Detail_DTO.Get.Order_Details_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserSecret_DTO;
import com.example.web_ban_banh.Entity.*;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.Internal_Server_ErrorEX_500.InternalServerErrorExceptionCustom;
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
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Hex;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ObjectInputFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
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
    @Value("${vnpay.url}")
    private String vnpayUrl;
    @Value("${vnpay.tmnCode}")
    private String tmnCode;
    @Value("${vnpay.hashSecret}")
    private String hashSecret;
    @Value("${vnpay.returnUrl}")
    private String returnUrl;
    @Value("${vnpay.ipnUrl}")
    private String ipnUrl;

    @Autowired
    public Order_Service(Cart_RepoIn cartRepo, Cart_details_RepoIn cartDetailsRepo, Order_RepoIn orderRepo, Order_details_RepoIn orderDetailRepo, Product_RepoIn productRepo, Product_size_RepoIn productSizeRepo, User_RepoIn userRepo, Discount_code_RepoIn discountCodeRepo, ModelMapper modelMapper) {
        this.cartRepo = cartRepo;
        this.cartDetailsRepo = cartDetailsRepo;
        this.orderRepo = orderRepo;
        this.orderDetailRepo = orderDetailRepo;
        this.productRepo = productRepo;
        this.productSizeRepo = productSizeRepo;
        this.userRepo = userRepo;
        this.discountCodeRepo = discountCodeRepo;
        this.modelMapper = modelMapper;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Nếu có nhiều IP (qua proxy), lấy IP đầu tiên
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    @Transactional
    public CheckOutResponseDTO processCheckout(int userId, CheckOutRequestDTO request, HttpServletRequest httpServletRequest) {
        //Kiểm tra user có tồn tại không
        Optional<User> u = userRepo.findById(userId);
        if (u.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tim thấy User có Id: " + userId);
        }
        User user = u.get();
        UserSecret_DTO userDTO=modelMapper.map(user,UserSecret_DTO.class);

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
        order.setPaymentMethod(request.getPaymentMethod());
        order.setDeliveryAddress(request.getDeliveryAddress());

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
        long finalAmount = (long)(cart.getPromotionalPrice() - discountAmount);
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
        CheckOutResponseDTO respone = new CheckOutResponseDTO();
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
        respone.setUser(userDTO);

        // Nếu là VNPAY, generate payment URL
        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            String paymentUrl = createVNPayPaymentUrl(savedOrder.getId(), respone.getTotalAmount(),httpServletRequest);
            respone.setPaymentUrl(paymentUrl);
            // Optional: Update order status tạm thời để PENDING_PAYMENT hoặc giữ PENDING
        }

        return respone;
    }


    private String createVNPayPaymentUrl(int orderId, double amount, HttpServletRequest request) {
        try {
            System.out.println("=== VNPAY Payment URL Generation ===");

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String orderType = "food";
            long vnpAmount = (long)(amount * 100);

            String vnp_TxnRef = String.valueOf(orderId);
            String vnp_IpAddr = getClientIp(request);
            String vnp_TmnCode = tmnCode;

            // Tạo timestamp theo chuẩn VNPay
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());

            System.out.println("Order ID: " + orderId);
            System.out.println("Amount: " + vnpAmount);
            System.out.println("Create Date: " + vnp_CreateDate);
            System.out.println("IP Address: " + vnp_IpAddr);

            // Tạo Map parameters - KHÔNG dùng TreeMap
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(vnpAmount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Sắp xếp field names theo alphabet - GIỐNG HỆT CODE MẪU
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            System.out.println("=== Sorted Parameters ===");
            for (String field : fieldNames) {
                System.out.println(field + "=" + vnp_Params.get(field));
            }

            // Tạo hashData và query - THEO ĐÚNG CODE MẪU
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data - ENCODE với US_ASCII
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    // Build query - ENCODE với US_ASCII
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String hashDataStr = hashData.toString();

            System.out.println("Hash Data: " + hashDataStr);
            System.out.println("Hash Secret: " + hashSecret);

            // Tạo secure hash - THỨ TỰ PARAMETER: secretKey, hashData
            String vnp_SecureHash = hmacSHA512(hashSecret, hashDataStr);
            System.out.println("Generated Hash: " + vnp_SecureHash);

            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnpayUrl + "?" + queryUrl;

            System.out.println("Final URL: " + paymentUrl);
            return paymentUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorExceptionCustom("Lỗi tạo URL VNPay: " + e.getMessage());
        }
    }


    // 2. METHOD XỬ LÝ CALLBACK VỚI URL DECODE
    @Transactional
    public void handleVNPayCallback(Map<String, String> params, boolean isReturnUrl) {
        System.out.println("=== VNPay Callback Handler START ===");
        System.out.println("Is Return URL: " + isReturnUrl);
        System.out.println("Total parameters received: " + params.size());

        // In ra tất cả parameters nhận được
        System.out.println("=== ALL RECEIVED PARAMETERS ===");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println("'" + entry.getKey() + "' = '" + entry.getValue() + "'");
        }

        // Lấy secure hash từ VNPay
        String vnp_SecureHash = params.get("vnp_SecureHash");
        System.out.println("Received Hash from VNPay: " + vnp_SecureHash);

        if (vnp_SecureHash == null || vnp_SecureHash.trim().isEmpty()) {
            throw new BadRequestExceptionCustom("Không tìm thấy chữ ký trong callback");
        }

        // Tạo Map để verify - URL decode các giá trị nếu cần
        Map<String, String> verifyParams = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!"vnp_SecureHash".equals(key) &&
                    !"vnp_SecureHashType".equals(key) &&
                    value != null && !value.trim().isEmpty()) {

                // Thử URL decode giá trị
                try {
                    String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
                    verifyParams.put(key, decodedValue);
                    if (!value.equals(decodedValue)) {
                        System.out.println("URL Decoded: '" + key + "' from '" + value + "' to '" + decodedValue + "'");
                    }
                } catch (Exception e) {
                    // Nếu decode lỗi, dùng giá trị gốc
                    verifyParams.put(key, value);
                    System.out.println("Keep original: '" + key + "' = '" + value + "'");
                }
            }
        }

        System.out.println("=== PARAMETERS FOR VERIFICATION (after decode) ===");
        for (Map.Entry<String, String> entry : verifyParams.entrySet()) {
            System.out.println("'" + entry.getKey() + "' = '" + entry.getValue() + "'");
        }

        // Sắp xếp parameters theo alphabet
        List<String> fieldNames = new ArrayList<>(verifyParams.keySet());
        Collections.sort(fieldNames);

        System.out.println("=== SORTED FIELD NAMES ===");
        for (String field : fieldNames) {
            System.out.println(field + "=" + verifyParams.get(field));
        }

        // Tạo chuỗi để verify - KHÔNG encode
        StringBuilder signDataBuilder = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = verifyParams.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                if (signDataBuilder.length() > 0) {
                    signDataBuilder.append('&');
                }
                signDataBuilder.append(fieldName);
                signDataBuilder.append('=');
                signDataBuilder.append(fieldValue); // KHÔNG encode
            }
        }

        String signData = signDataBuilder.toString();
        System.out.println("Sign Data String: " + signData);

        // Tính hash
        String calculatedHash = hmacSHA512(hashSecret, signData);
        System.out.println("Calculated Hash: " + calculatedHash);
        System.out.println("Received Hash: " + vnp_SecureHash);
        System.out.println("Hash Match: " + calculatedHash.equalsIgnoreCase(vnp_SecureHash));

        // Verify signature
        if (!calculatedHash.equalsIgnoreCase(vnp_SecureHash)) {
            System.err.println("=== HASH VERIFICATION FAILED ===");

            // THỬ CÁCH KHÁC: Encode lại signData với US_ASCII
            try {
                StringBuilder encodedSignDataBuilder = new StringBuilder();
                for (int i = 0; i < fieldNames.size(); i++) {
                    String fieldName = fieldNames.get(i);
                    String fieldValue = verifyParams.get(fieldName);
                    if (fieldValue != null && fieldValue.length() > 0) {
                        if (encodedSignDataBuilder.length() > 0) {
                            encodedSignDataBuilder.append('&');
                        }
                        encodedSignDataBuilder.append(fieldName);
                        encodedSignDataBuilder.append('=');
                        encodedSignDataBuilder.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    }
                }

                String encodedSignData = encodedSignDataBuilder.toString();
                String calculatedHashEncoded = hmacSHA512(hashSecret, encodedSignData);

                System.err.println("=== TRYING WITH ENCODED SIGN DATA ===");
                System.err.println("Encoded Sign Data: " + encodedSignData);
                System.err.println("Hash with encoded: " + calculatedHashEncoded);
                System.err.println("Match with encoded: " + calculatedHashEncoded.equalsIgnoreCase(vnp_SecureHash));

                if (calculatedHashEncoded.equalsIgnoreCase(vnp_SecureHash)) {
                    System.out.println("✓ Hash verification successful with encoded data!");
                } else {
                    throw new BadRequestExceptionCustom("Chữ ký không hợp lệ!");
                }

            } catch (Exception encodeEx) {
                System.err.println("Error in encoded verification: " + encodeEx.getMessage());
                throw new BadRequestExceptionCustom("Chữ ký không hợp lệ!");
            }
        } else {
            System.out.println("✓ Hash verification successful!");
        }

        // Xử lý order status
        String orderIdStr = params.get("vnp_TxnRef");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            throw new BadRequestExceptionCustom("Không tìm thấy Order ID trong callback");
        }

        int orderId = Integer.parseInt(orderIdStr.trim());
        Optional<Order> orderOpt = orderRepo.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy Order " + orderId);
        }
        Order order = orderOpt.get();

        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            order.setStatus(Status.PAID);
            String transactionNo = params.get("vnp_TransactionNo");
            if (transactionNo != null && !transactionNo.isEmpty()) {
                order.setPaymentTransactionId(transactionNo);
            }
            System.out.println("✓ Payment successful for Order: " + orderId);
        } else {
            order.setStatus(Status.FAILED);
            System.out.println("✗ Payment failed for Order: " + orderId);
        }

        orderRepo.save(order);
        System.out.println("✓ Order status updated successfully");
    }

    // Method hashAllFields - COPY TỪNG DÒNG TỪ CODE MẪU
    public String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(hashSecret, sb.toString());
    }

    // Method hmacSHA512 - THỨ TỰ PARAMETER: key, data
    private String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();  // KHÔNG chỉ định charset
            final SecretKeySpec secretKeySpec = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }


    //Hàm KIỂM TRA số lượng sản phẩm mà khách nhập vào có nhiều hơn số lượng sản phẩm trong kho không (Nếu không thì cập nhật lại kho)
    private void validateAndUpdateStock (Cart_details cartDetail){
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
    public Page<OrderDTO> getAllOrder (Pageable pageable){
        Page<Order> orders = orderRepo.findAll(pageable);
        return orders.map(order -> {
            OrderDTO dto = modelMapper.map(order, OrderDTO.class);
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findById ( int id){
        Optional<Order> o = orderRepo.findById(id);
        if (o.isEmpty()) {
            throw new NotFoundExceptionCustom("Không tìm thấy đơn hàng có id " + id);
        }
        Order order = o.get();
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        return dto;
    }

    //Phương thức tìm Order theo ba tiêu chí: Status, orderDate, totalAmount
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> filter (Pageable pageable, Date orderDate, Status status, Double totalAmount){
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

        Page<Order> orders = orderRepo.findAll(spec, pageable);
        return orders.map(order -> modelMapper.map(order, OrderDTO.class));
    }

}
