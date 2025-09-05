package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",columnDefinition = "INT UNSIGNED")
    private int id;
    @Column(name="product_name",nullable = false,length = 255)
    private String productname;
    @Column(name="description",nullable = false,length = 1000)
    private String description;
    @Column(name="quantity",columnDefinition = "INT UNSIGNED")
    private Integer quantity;
    @Column(name="original_price",columnDefinition ="DECIMAL(10,3) CHECK (original_price >= 0)")
    private Double originalPrice;
    @Column(name="promotional_price",columnDefinition = "DECIMAL(10,3) CHECK (promotional_price >= 0)")
    private Double promotionalPrice;
    @Column(name="img")
    private String img;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Cart_details>cartDetails=new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Order_details>orderDetails=new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToMany(mappedBy = "products",cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.LAZY)
    private List<Product_size>productSizes=new ArrayList<>();

}
