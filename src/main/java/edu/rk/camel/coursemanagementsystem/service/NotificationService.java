package edu.rk.camel.coursemanagementsystem.service;

import edu.rk.camel.coursemanagementsystem.model.dto.request.NotificationCreateRequest;
import edu.rk.camel.coursemanagementsystem.model.dto.response.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getMyNotifications();
    void markAsRead(Integer notificationId);
    NotificationDto createNotification(NotificationCreateRequest request);
    void deleteNotification(Integer notificationId);
}
