package com.example.web_ban_banh.Custom.CustomSecurity;


import com.example.web_ban_banh.Entity.User;
import com.example.web_ban_banh.Repository.User.User_RepoIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private User_RepoIn userRepo;

    @Autowired
    public CustomUserDetailsService(User_RepoIn userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Tìm trong bảng Học Sinh
       Optional<User> userOp=userRepo.findByUserName(username);
       if(userOp.isPresent()){
           User user=userOp.get();
           return org.springframework.security.core.userdetails.User.builder()
                   .username(user.getUserName())
                   .password(user.getPassword())
                   .roles(user.getRole().name())
                   .build();
       }

        // Nếu không tìm thấy ở bảng nào
        throw new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username);
    }

}
