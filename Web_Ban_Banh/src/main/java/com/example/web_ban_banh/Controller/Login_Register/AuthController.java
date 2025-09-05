//package com.example.web_ban_banh.Controller.Login_Register;
//
//import com.example.web_ban_banh.Config.Security.JwtUtil;
//import com.example.web_ban_banh.DTO.Login_DTO.AuthRequest;
//import com.example.web_ban_banh.DTO.Register_DTO.RegisterUserRequest_DTO;
//import com.example.web_ban_banh.DTO.User_DTO.Get.User_DTO;
//import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
//import com.example.web_ban_banh.Service.User_Service.User_ServiceIn;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtil jwtUltil;
//    private User_ServiceIn userService;
//
//    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUltil,User_ServiceIn userService) {
//        this.authenticationManager = authenticationManager;
//        this.jwtUltil = jwtUltil;
//        this.userService=userService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest request){
//        try{
//            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
//            String token=jwtUltil.generaToken(request.getUsername());
//            return ResponseEntity.ok(token);
//        }catch(AuthenticationException e){
//            return ResponseEntity.status(401).body(Map.of("error","Username hoặc Password không hợp lệ "));
//        }
//    }
//
//    @PostMapping("/regist")
//    public ResponseEntity<?>register(RegisterUserRequest_DTO request){
//        try {
//            User_DTO create=userService.register(request);
//            return ResponseEntity.ok("Đã đăng ký thành công");
//        }catch (HttpClientErrorException.BadRequest be){
//            return ResponseEntity.badRequest().body("Đăng ký thất bại "+be.getMessage());
//        }
//    }
//}
