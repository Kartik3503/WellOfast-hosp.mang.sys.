package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "patients")
public class Patient {
    @Id
    private String id;

    @NotBlank private String name;
    private int age;
    @NotBlank private String gender;
    private String phone;
    private String email;
    private String bloodGroup;
    private String address;
    private String city;
    private String state;

    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String diagnosis;
    private String assignedDoctorId;
    private String assignedDoctorName;
    private String department;
    /** ADMITTED, DISCHARGED, OPD */
    private String status = "OPD";
    private String roomNumber;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
