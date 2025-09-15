package com.example.web_ban_banh.Repository.Order;

import com.example.web_ban_banh.DTO.Order_DTO.Get.OrderDTO;
import com.example.web_ban_banh.Entity.Order;
import com.example.web_ban_banh.Entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface Order_RepoIn extends JpaRepository<Order,Integer>, JpaSpecificationExecutor<Order> {

}
