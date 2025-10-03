package com.example.web_ban_banh.Controller.Order_Controller;

import com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest.CheckOutRequestDTO;
import com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese.CheckOutResponseDTO;
import com.example.web_ban_banh.DTO.Order_DTO.Get.OrderDTO;
import com.example.web_ban_banh.Entity.Status;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Service.Order_Service.Order_Service;
import com.example.web_ban_banh.Service.Order_Service.Order_ServicenIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Validated
public class CheckOut {
    private Order_ServicenIn orderService;

    @Autowired
    public CheckOut(Order_ServicenIn orderService) {
        this.orderService = orderService;
    }

    // Phương thức helper để tạo Pageable
    private Pageable createPageable(int page, int size, String sort) {
        if (sort != null) {
            // sort format: "field,asc" or "field,desc"
            String[] sortParts = sort.split(",");
            Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        }
        return PageRequest.of(page, size);
    }

    @GetMapping("/")
    public ResponseEntity<?>getAll(@RequestParam(defaultValue = "0")int page,
                                   @RequestParam(defaultValue = "15")int size,
                                   @RequestParam (required = false)String soft){
        Pageable pageable=createPageable(page,size,soft);
        Page<OrderDTO>dto=orderService.getAllOrder(pageable);
        if(dto.isEmpty()){
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>findById(@PathVariable int id){
        OrderDTO dto= orderService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> findOrderByFilter(@RequestParam(defaultValue = "0")int page,
                                               @RequestParam(defaultValue = "15")int size,
                                               @RequestParam (required = false)String soft,
                                               @RequestParam (required = false)Status status,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date orderDate,
                                               @RequestParam(required = false)Double totalAmount){
        Pageable pageable=createPageable(page,size,soft);
        Page<OrderDTO>dto=orderService.filter(pageable,orderDate,status,totalAmount);
        if(dto.isEmpty()){
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/checkout/{id}")
    public ResponseEntity<?> checkout(@RequestBody @Valid CheckOutRequestDTO request,
                                      @PathVariable @Min(value = 1, message = "ID phải lớn hơn 0") int id,
                                      HttpServletRequest httpServletRequest) {
        try {
            CheckOutResponseDTO dto = orderService.processCheckout(id, request,httpServletRequest);
            if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
                // Frontend sẽ redirect đến dto.getPaymentUrl()
                return ResponseEntity.ok(Map.of(
                        "message", "Tạo đơn hàng thành công. Chuyển hướng đến VNPay...",
                        "orderInfo", dto,
                        "paymentUrl", dto.getPaymentUrl()
                ));
            } else {
                // COD như cũ
                return ResponseEntity.ok(Map.of(
                        "message", "Đơn hàng được tạo thành công",
                        "orderInfo", dto
                ));
            }
        } catch (Exception e) {
            throw new BadRequestExceptionCustom("Không thể tạo đơn hàng. Vui lòng thử lại sau. " + e.getMessage());
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            System.out.println("=== VNPay Return Endpoint Hit ===");
            System.out.println("Parameters received: " + params.size());

            orderService.handleVNPayCallback(params, true); // true = đây là return URL

            String orderIdStr = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Thanh toán thành công",
                        "orderId", orderIdStr != null ? orderIdStr : "N/A"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "failed",
                        "message", "Thanh toán thất bại - Mã lỗi: " + responseCode,
                        "orderId", orderIdStr != null ? orderIdStr : "N/A",
                        "responseCode", responseCode != null ? responseCode : "Unknown"
                ));
            }
        } catch (Exception e) {
            System.err.println("=== Error in vnpay-return endpoint ===");
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", e.getMessage(),
                    "orderId", params.get("vnp_TxnRef") != null ? params.get("vnp_TxnRef") : "N/A"
            ));
        }
    }

    // Endpoint cho VNPay IPN (server-to-server, optional - dùng để confirm payment)
    @PostMapping("/vnpay-ipn")
    public ResponseEntity<?> vnpayIpn(@RequestBody Map<String, String> params) {
        try {
            orderService.handleVNPayCallback(params, false);  // false = đây là ipn
            return ResponseEntity.ok("OK");  // VNPay expect "OK" response
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR");
        }
    }

}
