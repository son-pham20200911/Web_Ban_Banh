package com.example.web_ban_banh.Service.User_Service;

import com.example.web_ban_banh.DTO.Register_DTO.RegisterUserRequest_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.User_DTO;
import com.example.web_ban_banh.Entity.Role;
import com.example.web_ban_banh.Entity.User;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Repository.User.User_RepoIn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class User_Service implements User_ServiceIn {
    private User_RepoIn userRepo;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public User_Service(User_RepoIn userRepo, ModelMapper modelMapper,PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    @Transactional
    public User_DTO register(RegisterUserRequest_DTO request) {
        boolean existsUsername = userRepo.existsByUserName(request.getUserName());
        if (existsUsername) {
            throw new BadRequestExceptionCustom("Username đã tồn tại");
        }

        boolean existEmail = userRepo.existsByEmail(request.getEmail());
        if (existEmail) {
            throw new BadRequestExceptionCustom("Email đã tồn tại");
        }

        boolean existPhoneNumber = userRepo.existsByPhoneNumber(request.getPhoneNumber());
        if (existPhoneNumber) {
            throw new BadRequestExceptionCustom("Số điện thoại đã tồn tại");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setRole(Role.USER);
        user.setEmail(request.getEmail());
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User create=userRepo.save(user);
        User_DTO dto=modelMapper.map(create,User_DTO.class);

        return dto;
    }
}
