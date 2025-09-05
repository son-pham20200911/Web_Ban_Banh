package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order_details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",columnDefinition = "INT UNSIGNED")
    private int id;
    @Column(name="quantity",nullable = false,columnDefinition = "INT UNSIGNED")
    private int quantity;
    @Column(name="original_price",nullable = false,columnDefinition ="DECIMAL(10,3) CHECK (original_price >= 0)")
    private Double originalPrice;
    @Column(name="promotional_price",nullable = false,columnDefinition = "DECIMAL(10,3) CHECK (promotional_price >= 0)")
    private Double promotionalPrice;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch =FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REMOVE},fetch = FetchType.LAZY)
    @JoinColumn(name="product_size_id")
    private Product_size productSize;

}
