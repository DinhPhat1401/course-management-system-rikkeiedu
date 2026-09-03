package edu.rk.camel.coursemanagementsystem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Integer notificationId;
    private Integer userId;
    private String message;
    private String type;
    private String targetUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
