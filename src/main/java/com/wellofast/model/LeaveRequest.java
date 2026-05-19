package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "leave_requests")
public class LeaveRequest {
    @Id
    private String id;

    private String userId;
    private String userName;
    private String department;

    @NotBlank private String leaveType; // Sick, Casual, Earned, Maternity
    @NotNull private LocalDate fromDate;
    @NotNull private LocalDate toDate;
    private int totalDays;
    private String reason;

    /** PENDING, APPROVED, REJECTED */
    private String status = "PENDING";
    private String approvedBy;
    private String rejectionReason;

    private LocalDateTime createdAt;
}
