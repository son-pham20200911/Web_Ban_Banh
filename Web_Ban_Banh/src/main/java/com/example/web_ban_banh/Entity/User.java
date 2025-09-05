package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",columnDefinition = "INT UNSIGNED")
    private int id;
    @Column(name="last_name",nullable = false, length = 255)
    private String lastName;
    @Column(name="first_name",nullable = false,length = 255)
    private String firstName;
    @Column(name="address",nullable = false,length =500)
    private String address;
    @Column(name="date_of_birth",nullable = false)
    private Date dateOfBirth;
    @Column(name="phone_number",nullable = false,length = 10)
    private String phoneNumber;
    @Column(name="gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name="email",nullable = false,unique = true)
    private String email;
    @Column(name="role",nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name="user_name",nullable = false,unique = true,length = 500)
    private String userName;
    @Column(name="password",nullable = false,unique = true,length = 500)
    private String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Cart> carts=new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Order> orders=new ArrayList<>();
}
