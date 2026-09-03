package edu.rk.camel.coursemanagementsystem.controller;

import edu.rk.camel.coursemanagementsystem.model.dto.request.NotificationCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.NotificationDto;
import edu.rk.camel.coursemanagementsystem.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications() {
        List<NotificationDto> notifications = notificationService.getMyNotifications();
        return ResponseEntity.ok(ApiResponse.success(notifications, "Lấy danh sách thông báo thành công", 200));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "Đánh dấu đã đọc thành công", 200));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDto>> createNotification(@Valid @RequestBody NotificationCreateRequest request) {
        NotificationDto notification = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(notification, "Tạo thông báo thành công", 201));
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Integer notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa thông báo thành công", 200));
    }
}
