package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "lab_bookings")
public class LabBooking {
    @Id
    private String id;

    private String patientUserId;
    private String patientName;

    private String testId;
    private String testName;
    private String testCategory;
    private double price;

    private LocalDate scheduledDate;
    private String status = "BOOKED";  // BOOKED, SAMPLE_COLLECTED, PROCESSING, COMPLETED, CANCELLED

    // Results (filled by staff/doctor)
    private String resultValue;
    private String normalRange;
    private String resultStatus;   // NORMAL, ABNORMAL, CRITICAL
    private String doctorNotes;
    private String reportedBy;

    private LocalDateTime createdAt;
    private LocalDateTime resultDate;
}
