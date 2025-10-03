package com.example.web_ban_banh.Service.User_Service;

import com.example.web_ban_banh.DTO.Register_DTO.RegisterUserRequest_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserPublic_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserSecret_DTO;
import com.example.web_ban_banh.Entity.PasswordResetToken;
import com.example.web_ban_banh.Entity.Role;
import com.example.web_ban_banh.Entity.User;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Exception.NotFoundEx_404.NotFoundExceptionCustom;
import com.example.web_ban_banh.Repository.PasswordResetToken.PasswordResetToken_RePoIn;
import com.example.web_ban_banh.Repository.User.User_RepoIn;
import org.hibernate.sql.Update;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class User_Service implements User_ServiceIn {
    private User_RepoIn userRepo;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private JavaMailSender mailSender;
    @Value("${app.password-reset.expiration}")
    private long expiryDuration;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private PasswordResetToken_RePoIn passwordResetTokenRePo;

    @Autowired
    public User_Service(User_RepoIn userRepo, ModelMapper modelMapper,PasswordEncoder passwordEncoder,JavaMailSender mailSender,PasswordResetToken_RePoIn passwordResetTokenRePo) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.passwordEncoder=passwordEncoder;
        this.mailSender=mailSender;
        this.passwordResetTokenRePo=passwordResetTokenRePo;
    }

    //Phương thức lấy toàn bộ User (không có Username, Password)
    @Override
    @Transactional(readOnly = true)
    public Page<UserSecret_DTO> getAllUser(Pageable pageable) {
        Page<User>users=userRepo.findAll(pageable);
        return users.map(user->{
            UserSecret_DTO dto=modelMapper.map(user,UserSecret_DTO.class);
            return dto;
        });
    }

    //Phương thức hiển thị User theo ID (Có Username, Password)(Dùng để hiển thị phần thông tin cá nhân)
    @Override
    @Transactional(readOnly = true)
    public UserPublic_DTO findById(int id) {
        Optional<User> u=userRepo.findById(id);
        if(u.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy User có id "+id);
        }
        User user=u.get();
        UserPublic_DTO userPublicDto=modelMapper.map(user,UserPublic_DTO.class);
        return userPublicDto;
    }

    //Phương thức hiển thị User theo Fullname (không có Username, Password)
    @Override
    @Transactional(readOnly = true)
    public Page<UserSecret_DTO> findByFullName(String fullName, Pageable pageable) {
        Page<User> users=userRepo.findByFullName(fullName,pageable);
        return users.map(user->{
            UserSecret_DTO dto=modelMapper.map(user,UserSecret_DTO.class);
            return dto;
        });
    }

    @Override
    public UserPublic_DTO findByUsername(String username) {
        Optional<User> userOpt=userRepo.findByUserName(username);
        if(userOpt.isPresent()){
            User user=userOpt.get();
            UserPublic_DTO dto=modelMapper.map(user,UserPublic_DTO.class);
            return dto;
        }
        return null;
    }

    //Đăng ký
    @Override
    @Transactional
    public UserPublic_DTO register(RegisterUserRequest_DTO request) {
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
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setRole(Role.USER);
        user.setEmail(request.getEmail());
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User create=userRepo.save(user);
        UserPublic_DTO dto=modelMapper.map(create, UserPublic_DTO.class);

        return dto;
    }

    //Cập nhật
    @Override
    @Transactional
    public UserSecret_DTO updateUser(int id,RegisterUserRequest_DTO update) {
        Optional<User> u=userRepo.findById(id);
        if(u.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy User có id "+id);
        }

        User user=u.get();
        boolean existUsername=userRepo.existsByUserName(update.getUserName());
        if(existUsername && !Objects.equals(user.getUserName(),update.getUserName())){
            throw new BadRequestExceptionCustom("Username đã tồn tại");
        }
        user.setUserName(update.getUserName());

        boolean existEmail = userRepo.existsByEmail(update.getEmail());
        if (existEmail && !Objects.equals(user.getEmail(),update.getEmail())) {
            throw new BadRequestExceptionCustom("Email đã tồn tại");
        }
        user.setEmail(update.getEmail());

        boolean existPhoneNumber = userRepo.existsByPhoneNumber(update.getPhoneNumber());
        if (existPhoneNumber && !Objects.equals(user.getPhoneNumber(),update.getPhoneNumber())) {
            throw new BadRequestExceptionCustom("Số điện thoại đã tồn tại");
        }
        user.setPhoneNumber(update.getPhoneNumber());

        user.setRole(update.getRole());
        user.setGender(update.getGender());
        user.setDateOfBirth(update.getDateOfBirth());
        user.setAddress(update.getAddress());
        user.setPassword(passwordEncoder.encode(update.getPassword()));
        user.setFullName(update.getFullName());

        User up=userRepo.saveAndFlush(user);
        UserSecret_DTO userSecretDto=modelMapper.map(up,UserSecret_DTO.class);
        return userSecretDto;
    }

    //Xóa
    @Override
    @Transactional
    public void deleteUser(int id) {
        Optional<User> u=userRepo.findById(id);
        if(u.isEmpty()){
            throw new NotFoundExceptionCustom("Không tìm thấy User có id "+id);
        }
        User user=u.get();

        userRepo.delete(user);
    }

    //Quên mật khẩu
    @Override
    @Transactional
    public void sendPasswordResetEmail(String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);  // Thêm method findByEmail vào User_RepoIn nếu chưa có
        if (userOpt.isEmpty()) {
            throw new BadRequestExceptionCustom("Email không tồn tại");
        }
        User user = userOpt.get();

        // Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // Tạo expiry date
        Date expiryDate = new Date(System.currentTimeMillis() + expiryDuration);

        // Lưu token
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRePo.save(resetToken);

        // Gửi email
        String resetLink = "http://your-frontend.com/reset-password?token=" + token;  // Thay bằng URL frontend thật
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Gửi từ đâu (email của bạn)
        message.setTo(email);       // Gửi đến đâu(email mà User nhập vào)
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Click link để đặt lại mật khẩu: " + resetLink + "\nLink hết hạn sau 1 giờ.");
        mailSender.send(message);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRePo.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new BadRequestExceptionCustom("Token không hợp lệ");
        }
        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            throw new BadRequestExceptionCustom("Token đã hết hạn");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));  // Mã hóa password mới
        userRepo.save(user);

        // Xóa token sau khi dùng
        passwordResetTokenRePo.delete(resetToken);
    }


}
