package com.example.web_ban_banh.Controller.Order_Controller;

import com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest.CheckOutRequestDTO;
import com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese.CheckOutResponseDTO;
import com.example.web_ban_banh.DTO.Order_DTO.Get.OrderDTO;
import com.example.web_ban_banh.Entity.Status;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Service.Order_Service.Order_ServicenIn;
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
    public ResponseEntity<?>checkout(@RequestBody @Valid CheckOutRequestDTO request, @PathVariable @Min(value = 1,message = "ID phải lớn hơn 0")int id){
        try {
            CheckOutResponseDTO dto=orderService.processCheckout(id,request);
            return ResponseEntity.ok(Map.of("message","Đơn hàng được tạo thành công",
                                            "Thông tin đơn hàng",dto));
        }catch(Exception e){
            throw new BadRequestExceptionCustom("Không thể tạo đơn hàng. Vui lòng thử lại sau. "+e.getMessage());
        }
    }

}
