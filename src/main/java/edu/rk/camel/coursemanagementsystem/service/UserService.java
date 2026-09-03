package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.request.ChangePasswordRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.UserCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.request.UserUpdateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.UserDto;
import edu.rk.camel.coursemanagementsystem.model.entity.Role;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(Boolean status);
    UserDto getUserById(Integer userId);
    UserDto createUser(UserCreateRequest request);
    UserDto updateUserRole(Integer userId, Role role);
    UserDto updateUserStatus(Integer userId, Boolean status);
    void deleteUser(Integer userId);
    UserDto updateUserInfo(Integer userId, UserUpdateRequest request);
    void changePassword(Integer userId, ChangePasswordRequest request);
}
