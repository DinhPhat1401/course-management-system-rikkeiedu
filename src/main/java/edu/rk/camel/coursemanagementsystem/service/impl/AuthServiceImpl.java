package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.InvalidInputException;
import edu.rk.camel.coursemanagementsystem.model.dto.request.LoginRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.AuthResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.User;
import edu.rk.camel.coursemanagementsystem.security.CustomUserDetails;
import edu.rk.camel.coursemanagementsystem.security.JwtTokenProvider;
import edu.rk.camel.coursemanagementsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            if (!user.getIsActive()) {
                throw new InvalidInputException("Tài khoản của bạn đã bị vô hiệu hóa.");
            }

            UserDto userDto = mapToDto(user);
            return new AuthResponse(jwt, "Bearer", userDto);

        } catch (BadCredentialsException ex) {
            throw new edu.rk.camel.coursemanagementsystem.exception.ApiException(
                    "BAD_CREDENTIALS", "Sai thông tin đăng nhập", 401);
        }
    }

    @Override
    public UserDto getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new edu.rk.camel.coursemanagementsystem.exception.ApiException(
                    "UNAUTHORIZED", "Vui lòng đăng nhập để tiếp tục", 401);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return mapToDto(userDetails.getUser());
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
