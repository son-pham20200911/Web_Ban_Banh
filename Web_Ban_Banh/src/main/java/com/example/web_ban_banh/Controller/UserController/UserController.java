package com.example.web_ban_banh.Controller.UserController;

import com.example.web_ban_banh.DTO.Register_DTO.RegisterUserRequest_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserPublic_DTO;
import com.example.web_ban_banh.DTO.User_DTO.Get.UserSecret_DTO;
import com.example.web_ban_banh.Service.User_Service.User_ServiceIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private User_ServiceIn userService;

    @Autowired
    public UserController(User_ServiceIn userService) {
        this.userService = userService;
    }

    // Phương thức helper để tạo Pageable
    private Pageable createPageable(int page, int size, String sort) {
        if (sort != null) {
            // sort format: "field,asc" or "field,desc"
            String[] sortParts = sort.split(",");
            Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        }
        return PageRequest.of(page, size);
    }

    @GetMapping("/")
    public ResponseEntity<?>getAllUser(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20")int size,
                                       @RequestParam(required = false)String soft){
        Pageable pageable=createPageable(page,size,soft);
        Page<UserSecret_DTO>dtos=userService.getAllUser(pageable);
        if(dtos.isEmpty()){
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>getUserById(@PathVariable int id){
        UserPublic_DTO dto= userService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-full-name")
    public ResponseEntity<?>getUserByLastNameAndFirstName(@RequestParam String fullName,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20")int size,
                                                          @RequestParam(required = false)String soft){
        Pageable pageable=createPageable(page,size,soft);
        Page<UserSecret_DTO>dtos=userService.findByFullName(fullName,pageable);
        if(dtos.isEmpty()){
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateUser(@PathVariable int id, @RequestBody @Valid RegisterUserRequest_DTO update){
        UserSecret_DTO userSecretDto= userService.updateUser(id,update);
        return ResponseEntity.ok("Cập nhật thành công user "+update.getFullName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteUsser(@PathVariable int id){
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa thành công User có Id "+id);
    }
}
