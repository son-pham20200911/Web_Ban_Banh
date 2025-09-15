package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",columnDefinition = "INT UNSIGNED")
    private int id;
    @Column(name="order_date",nullable = false)
    private Date orderDate;
    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name="total_amount",nullable = false,columnDefinition = "DECIMAL(10,3) CHECK (total_amount >= 0)")
    private double totalAmount;
    @Column(name="original_price",nullable = false,columnDefinition ="DECIMAL(10,3) CHECK (original_price >= 0)")
    private Double originalPrice;
    @Column(name="promotional_price",nullable = false,columnDefinition = "DECIMAL(10,3) CHECK (promotional_price >= 0)")
    private Double promotionalPrice;
    @Column(name="delivery_address",length = 500,nullable = false)
    private String deliveryAddress;
    @Column(name="note",length = 500)
    private String note;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Order_details>orderDetails=new ArrayList<>();

    @ManyToMany(mappedBy = "orders",cascade ={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.LAZY )
    private List<Discount_code>discountCodes=new ArrayList<>();

}
