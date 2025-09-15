package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="Black_listed_Token ")
public class BlacklistedToken {
    @Id
    private String token;
    private Date expiryDate;

    public BlacklistedToken() {
    }

    public BlacklistedToken(String token, Date expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
