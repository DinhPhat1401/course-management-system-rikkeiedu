package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.request.ChangePasswordRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.UserCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.UserUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.Role;
import edu.rk.camel.coursemanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(@RequestParam(required = false) Boolean status) {
        List<UserDto> users = userService.getAllUsers(status);
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách người dùng thành công", 200));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Integer userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy thông tin người dùng thành công", 200));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserDto createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser, "Tạo người dùng thành công", 201));
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateRole(
            @PathVariable Integer userId, 
            @RequestBody Map<String, String> payload) {
        
        Role role = Role.valueOf(payload.get("role"));
        UserDto updatedUser = userService.updateUserRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Cập nhật quyền thành công", 200));
    }

    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateStatus(
            @PathVariable Integer userId, 
            @RequestBody Map<String, Boolean> payload) {
        
        Boolean status = payload.get("is_active");
        UserDto updatedUser = userService.updateUserStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Cập nhật trạng thái thành công", 200));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa người dùng thành công", 200));
    }

    @PutMapping("/{userId}")
    // Check owner or admin is done inside the service layer
    public ResponseEntity<ApiResponse<UserDto>> updateUserInfo(
            @PathVariable Integer userId, 
            @Valid @RequestBody UserUpdateRequest request) {
        
        UserDto updatedUser = userService.updateUserInfo(userId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Cập nhật thông tin thành công", 200));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Integer userId, 
            @Valid @RequestBody ChangePasswordRequest request) {
        
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công", 200));
    }
}
