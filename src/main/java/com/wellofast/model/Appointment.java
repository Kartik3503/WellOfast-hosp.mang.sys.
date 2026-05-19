package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;

    @NotBlank private String patientName;
    private String patientId;
    @NotBlank private String doctorName;
    private String doctorId;
    private String department;

    @NotNull private LocalDate date;
    @NotNull private LocalTime time;

    /** SCHEDULED, COMPLETED, CANCELLED */
    private String status = "SCHEDULED";
    private String notes;
    private String type; // Checkup, Surgery, Follow-up, Emergency

    // Patient portal linkage
    private String bookedByPatientUserId;  // User ID of the patient who booked via portal
    private double fee;                     // Consultation fee

    private LocalDateTime createdAt;
}
