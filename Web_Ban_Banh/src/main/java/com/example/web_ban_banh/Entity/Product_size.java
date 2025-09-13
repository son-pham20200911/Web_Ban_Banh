package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="product_size")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product_size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",columnDefinition = "INT UNSIGNED")
    private int id;
    @Column(name="product_name")
    private String productName;
    @Column(name="label",nullable = false,length = 255)
    private String label;
    @Column(name="original_price",nullable = false,columnDefinition ="DECIMAL(10,3) CHECK (original_price >= 0)")
    private Double originalPrice;
    @Column(name="promotional_price",columnDefinition ="DECIMAL(10,3) CHECK (promotional_price >= 0)")
    private Double promotionalPrice;
    @Column(name="quantity",nullable = false,columnDefinition = "INT UNSIGNED")
    private Integer quantity;

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(name="product_productsize_id",
            joinColumns = @JoinColumn(name="product_size_id"),
            inverseJoinColumns = @JoinColumn(name="product_id"))
    private List<Product>products=new ArrayList<>();

    @OneToMany(mappedBy = "productSize",cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REMOVE},fetch = FetchType.LAZY)
    private List<Order_details>orderDetails=new ArrayList<>();

    @OneToMany(mappedBy = "productSize",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Cart_details>cartDetails=new ArrayList<>();

}
