package com.example.web_ban_banh.Repository.Category;

import com.example.web_ban_banh.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Category_RepoIn extends JpaRepository<Category,Integer> {
    public Category findByCategoryName(String categoryName);
}
