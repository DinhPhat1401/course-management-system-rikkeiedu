package edu.rk.camel.coursemanagementsystem.service.impl;

import edu.rk.camel.coursemanagementsystem.exception.ApiException;
import edu.rk.camel.coursemanagementsystem.exception.ResourceNotFoundException;
import edu.rk.camel.coursemanagementsystem.model.dto.request.NotificationCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.NotificationDto;
import edu.rk.camel.coursemanagementsystem.model.entity.Notification;
import edu.rk.camel.coursemanagementsystem.model.entity.User;
import edu.rk.camel.coursemanagementsystem.repository.NotificationRepository;
import edu.rk.camel.coursemanagementsystem.repository.UserRepository;
import edu.rk.camel.coursemanagementsystem.security.CustomUserDetails;
import edu.rk.camel.coursemanagementsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public List<NotificationDto> getMyNotifications() {
        User currentUser = getCurrentUser();
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(currentUser.getUserId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        
        User currentUser = getCurrentUser();
        if (!notification.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new ApiException("ACCESS_DENIED", "Bạn không có quyền thao tác", 403);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public NotificationDto createNotification(NotificationCreateRequest request) {
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Notification notification = Notification.builder()
                .user(targetUser)
                .message(request.getMessage())
                .type(request.getType())
                .targetUrl(request.getTargetUrl())
                .isRead(false)
                .build();

        return mapToDto(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void deleteNotification(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        notificationRepository.delete(notification);
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser().getUserId())
                .message(notification.getMessage())
                .type(notification.getType())
                .targetUrl(notification.getTargetUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
