package com.example.web_ban_banh.Repository.Cart;

import com.example.web_ban_banh.Entity.Cart;
import com.example.web_ban_banh.Entity.CartStatus;
import com.example.web_ban_banh.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface Cart_RepoIn extends JpaRepository<Cart,Integer> {
    @Query("SELECT c FROM Cart c WHERE c.user.id=:userId AND c.status=:status ORDER BY c.id desc")
    Optional<Cart>findFirstByUserAndStatusOrderByIdDesc(@Param("userId") int userID,@Param("status") CartStatus status);
}
