package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="discount_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount_code {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",columnDefinition = "INT UNSIGNED")
    private int id;
    @Column(name="value",nullable = false,columnDefinition = "DECIMAL(10,3) CHECK (value >= 0)")
    private Double value;
    @Column(name="code",nullable = false,length = 255)
    private String code;
    @Column(name="start_date",nullable = false)
    private Date startDate;
    @Column(name="end_date",nullable = false)
    private Date endDate;
    @Column(name="activated")
    private boolean activated;

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(name = "discountCode_order_id",
    joinColumns = @JoinColumn(name="discount_code_id"),
    inverseJoinColumns = @JoinColumn(name="order_id"))
    private List<Order>orders=new ArrayList<>();
}
