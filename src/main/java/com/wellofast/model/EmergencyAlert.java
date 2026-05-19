package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "emergency_alerts")
public class EmergencyAlert {
    @Id
    private String id;

    private String patientUserId;
    private String patientName;
    private String patientPhone;
    private String patientAddress;

    private String emergencyType;  // MEDICAL, ACCIDENT, CARDIAC, BREATHING, OTHER
    private String description;
    private String severity;       // CRITICAL, HIGH, MEDIUM

    private String status = "ACTIVE";  // ACTIVE, RESPONDING, RESOLVED
    private String respondedByDoctorId;
    private String respondedByDoctorName;
    private String responseNotes;

    private double latitude;
    private double longitude;

    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private LocalDateTime resolvedAt;
}
