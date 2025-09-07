package com.example.web_ban_banh.Service.Discount_code_Service;

import com.example.web_ban_banh.DTO.Discount_Code_DTO.Create.Create_Discount_Code_DTO;
import com.example.web_ban_banh.DTO.Discount_Code_DTO.Get.Discount_CodeDTO;
import com.example.web_ban_banh.DTO.Discount_Code_DTO.Update.Update_Discount_Code_DTO;
import com.example.web_ban_banh.Entity.Discount_code;
import com.example.web_ban_banh.Entity.Order;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.Discount_code.Discount_code_RepoIn;
import com.example.web_ban_banh.Repository.Order.Order_RepoIn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Discount_Code_Service implements Discount_Code_ServiceIn{
    private Discount_code_RepoIn discountCodeRepo;
    private Order_RepoIn orderRepo;
    private ModelMapper modelMapper;

    @Autowired
    public Discount_Code_Service(Discount_code_RepoIn discountCodeRepo,Order_RepoIn orderRepo, ModelMapper modelMapper) {
        this.discountCodeRepo = discountCodeRepo;
        this.orderRepo=orderRepo;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Discount_CodeDTO> getAllDiscountCode() {
        List<Discount_code>discountCodeList=discountCodeRepo.findAll();
        List<Discount_CodeDTO>discountCodeDTOS=new ArrayList<>();
        for (Discount_code dc:discountCodeList) {
            Discount_CodeDTO discountCodeDTO=modelMapper.map(dc,Discount_CodeDTO.class);
            discountCodeDTOS.add(discountCodeDTO);
        }
        return discountCodeDTOS;
    }

    @Override
    @Transactional(readOnly = true)
    public Discount_CodeDTO getDiscountCodeByCode(String code) {
        Discount_code discountCode=discountCodeRepo.findByCode(code);
        if(discountCode==null){
            throw new NotFoundExceptionCustom("Không tìm thấy Mã Giảm Giá có mã là: "+code);
        }
        Discount_CodeDTO discountCodeDTO=modelMapper.map(discountCode,Discount_CodeDTO.class);

        return discountCodeDTO;
    }

    @Override
    @Transactional
    public Discount_CodeDTO createDiscountCode(Create_Discount_Code_DTO create) {
        Discount_code discountCode=new Discount_code();
        discountCode.setValue(create.getValue());
        discountCode.setCode(create.getCode());
        discountCode.setStartDate(create.getStartDate());
        if(create.getEndDate().before(create.getStartDate())){
            throw new BadRequestExceptionCustom("Ngày kết thúc phải bé hơn ngày bắt đầu");
        }
        discountCode.setEndDate(create.getEndDate());
        discountCode.setActivated(create.isActivated());

        Discount_code created=discountCodeRepo.save(discountCode);
        Discount_CodeDTO discountCodeDTO=modelMapper.map(created,Discount_CodeDTO.class);
        return discountCodeDTO;
    }

    @Override
    @Transactional
    public Discount_CodeDTO updateDiscountCode(int id,Update_Discount_Code_DTO update) {
        Optional<Discount_code>dc=discountCodeRepo.findById(id);
        if(dc.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy Mã Giảm Giá có ID: "+id);
        }
        Discount_code discountCode=dc.get();
        List<Discount_code>discountCodes=new ArrayList<>();
        discountCodes.add(discountCode);

        discountCode.setCode(update.getCode());
        discountCode.setValue(update.getValue());
        discountCode.setStartDate(update.getStartDate());
        if(update.getEndDate().before(update.getStartDate())){
            throw new BadRequestExceptionCustom("Ngày kết thúc phải bé hơn ngày bắt đầu");
        }
        discountCode.setEndDate(update.getEndDate());
        discountCode.setActivated(update.isActivated());
        if(update.getOrders()!=null && !update.getOrders().isEmpty()){
            List<Order>orders=orderRepo.findAllById(update.getOrders());
            if(!orders.isEmpty()){
                if(orders.size()!=update.getOrders().size()){
                    throw new BadRequestExceptionCustom("Có một hoặc nhiều đơn hàng không tồn tại");
                }
            }else{
                throw new NotFoundExceptionCustom("Không tìm thấy đơn hàng nào cả");
            }
            for (Order order:orders) {
                order.setDiscountCodes(discountCodes);
                orderRepo.save(order);
            }
            discountCode.setOrders(orders);
        }
        Discount_code updated=discountCodeRepo.saveAndFlush(discountCode);
        Discount_CodeDTO discountCodeDTO=modelMapper.map(updated,Discount_CodeDTO.class);
        return discountCodeDTO;
    }

    @Override
    @Transactional
    public void deleteDiscountCode(int id) {
        Optional<Discount_code>dc=discountCodeRepo.findById(id);
        if(dc.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy mã giảm giá có ID: "+id);
        }
        Discount_code discountCode=dc.get();
        if(discountCode.getOrders()!=null && !discountCode.getOrders().isEmpty()){
            for (Order order:discountCode.getOrders()) {
             if(order!=null){
                 order.getDiscountCodes().remove(discountCode);
             }
             orderRepo.save(order);
            }
            discountCode.getOrders().clear();
        }
        discountCodeRepo.saveAndFlush(discountCode);
        discountCodeRepo.delete(discountCode);
    }


}
