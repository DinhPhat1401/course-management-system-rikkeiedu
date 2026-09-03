package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.request.LoginRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.AuthResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công", 200));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyToken() {
        // Nếu vào được đây nghĩa là filter JWT đã pass
        return ResponseEntity.ok(ApiResponse.success(true, "Token hợp lệ", 200));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto userDto = authService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(userDto, "Lấy thông tin thành công", 200));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // Phía client chỉ cần xóa token, server stateless không cần thao tác nhiều trừ khi thiết lập blacklist token
        return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công", 200));
    }
}
