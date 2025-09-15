package com.example.web_ban_banh.Service.BlacklistService;

import com.example.web_ban_banh.Config.Security.JwtUtil;
import com.example.web_ban_banh.Entity.BlacklistedToken;
import com.example.web_ban_banh.Repository.Blacklist.BlackList_RepositoryIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenBlacklistService {
private BlackList_RepositoryIn blackListRepository;
private JwtUtil jwtUtil;

@Autowired
    public TokenBlacklistService(BlackList_RepositoryIn blackListRepository,JwtUtil jwtUtil) {
        this.blackListRepository = blackListRepository;
        this.jwtUtil=jwtUtil;
    }

    public void addToBlacklist(String token) {
        Date expiryDate = jwtUtil.extractExpiration(token); // Cần thêm method này
        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiryDate);
        blackListRepository.save(blacklistedToken);
    }

    public boolean isBlacklisted(String token) {
        return blackListRepository.existsById(token);
    }

    // Xóa token đã hết hạn định kỳ
    @Scheduled(fixedRate = 3600000) // mỗi giờ
    public void cleanupExpiredTokens() {
        blackListRepository.deleteAllExpiryDate(new Date());
    }
}
