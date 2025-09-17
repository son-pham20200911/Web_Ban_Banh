package com.example.web_ban_banh.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String token;  // Token ngẫu nhiên (ví dụ: UUID)

    @Column(nullable = false)
    private Date expiryDate;  // Thời hạn hết hạn

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken() {
    }

    public PasswordResetToken( String token, Date expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //Method isExpired() để check token còn hạn không.
    public boolean isExpired() {
        return new Date().after(expiryDate);
    }
}
