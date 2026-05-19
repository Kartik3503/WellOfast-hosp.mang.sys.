package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    private String userId;          // who gets this notification
    private String type;            // APPOINTMENT, PRESCRIPTION, LAB_RESULT, REMINDER, CHAT, SYSTEM
    private String title;
    private String message;
    private String link;            // e.g. /portal/appointments or /doctor/dashboard
    private String icon;            // emoji icon
    private boolean read = false;

    private LocalDateTime createdAt;
}
