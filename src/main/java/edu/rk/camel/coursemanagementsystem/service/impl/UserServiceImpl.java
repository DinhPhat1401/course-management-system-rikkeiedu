package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.ApiException;
import edu.rk.camel.coursemanagementsystem.exception.InvalidInputException;
import edu.rk.camel.coursemanagementsystem.exception.ResourceNotFoundException;
import edu.rk.camel.coursemanagementsystem.model.dto.request.ChangePasswordRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.UserCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.UserUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.Role;
import edu.rk.camel.coursemanagementsystem.model.entity.User;
import edu.rk.camel.coursemanagementsystem.repository.UserRepository;
import edu.rk.camel.coursemanagementsystem.security.CustomUserDetails;
import edu.rk.camel.coursemanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers(Boolean status) {
        List<User> users = userRepository.findAll();
        if (status != null) {
            users = users.stream().filter(u -> u.getIsActive().equals(status)).collect(Collectors.toList());
        }
        return users.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException("DUPLICATE_RESOURCE", "Username đã tồn tại", 400);
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("DUPLICATE_RESOURCE", "Email đã tồn tại", 400);
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole() != null ? request.getRole() : Role.STUDENT)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserRole(Integer userId, Role role) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUserDetails = (CustomUserDetails) auth.getPrincipal();
        User currentUser = currentUserDetails.getUser();

        // Admin cannot change another Admin's role
        if (targetUser.getRole() == Role.ADMIN && currentUser.getUserId() != targetUser.getUserId()) {
             throw new ApiException("ACCESS_DENIED", "Bạn không có quyền sửa Role của Admin khác", 403);
        }

        targetUser.setRole(role);
        targetUser = userRepository.save(targetUser);
        return mapToDto(targetUser);
    }

    @Override
    @Transactional
    public UserDto updateUserStatus(Integer userId, Boolean status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(status);
        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDto updateUserInfo(Integer userId, UserUpdateRequest request) {
        checkOwnerOrAdmin(userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("DUPLICATE_RESOURCE", "Email đã tồn tại", 400);
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        checkOwnerOrAdmin(userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidInputException("Mật khẩu cũ không chính xác");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void checkOwnerOrAdmin(Integer userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUserDetails = (CustomUserDetails) auth.getPrincipal();
        User currentUser = currentUserDetails.getUser();

        if (currentUser.getRole() != Role.ADMIN && !currentUser.getUserId().equals(userId)) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền thực hiện chức năng này", 403);
        }
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
