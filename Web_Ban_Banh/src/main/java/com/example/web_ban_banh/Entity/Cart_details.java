package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="cart_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart_details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="product_quantity",columnDefinition = "INT UNSIGNED",nullable = false)
    private int productQuantity;
    @Column(name="original_price",nullable = false,columnDefinition ="DECIMAL(10,3) CHECK (original_price >= 0)")
    private Double originalPrice;
    @Column(name="promotional_price",nullable = false,columnDefinition = "DECIMAL(10,3) CHECK (promotional_price >= 0)")
    private Double promotionalPrice;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch =FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch =FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinColumn(name="product_size_id")
    private Product_size productSize;
}
