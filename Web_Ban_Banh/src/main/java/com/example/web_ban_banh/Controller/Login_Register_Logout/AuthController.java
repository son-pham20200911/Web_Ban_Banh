package com.example.web_ban_banh.Controller.Login_Register_Logout;

import com.example.web_ban_banh.Config.Security.JwtUtil;
import com.example.web_ban_banh.DTO.Login_DTO.AuthRequest;
import com.example.web_ban_banh.DTO.PasswordResetToken_DTO.ForgotPasswordRequest_DTO;
import com.example.web_ban_banh.DTO.PasswordResetToken_DTO.ResetPasswordRequest_DTO;
import com.example.web_ban_banh.DTO.Register_DTO.RegisterUserRequest_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserPublic_DTO;
import com.example.web_ban_banh.Service.BlacklistService.TokenBlacklistService;
import com.example.web_ban_banh.Service.User_Service.User_ServiceIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUltil;
    private User_ServiceIn userService;
    private TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUltil,User_ServiceIn userService,TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtUltil = jwtUltil;
        this.userService=userService;
        this.tokenBlacklistService=tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try{
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
            String token=jwtUltil.generaToken(request.getUsername());
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("eror","Username hoặc Password không chính xác "+e.getMessage()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("eror","Xác thực thất bại "+e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("eror","Lỗi hệ thống "+e.getMessage()));
        }
    }

    @PostMapping("/regist")
    public ResponseEntity<?>register(@RequestBody @Valid RegisterUserRequest_DTO request){
            UserPublic_DTO create=userService.register(request);
            return ResponseEntity.ok("Đã đăng ký thành công User mới: "+create.getFullName());
    }

    @PostMapping("/logout")
    public ResponseEntity<?>logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.addToBlacklist(token); // Thêm token vào blacklist
        }
        return ResponseEntity.ok("Đăng xuất thành công. Token đã bị thu hồi.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest_DTO request) {
        try {
            userService.sendPasswordResetEmail(request.getEmail());
            return ResponseEntity.ok("Email reset đã được gửi. Hãy kiểm tra Gmail của bạn.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest_DTO request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
