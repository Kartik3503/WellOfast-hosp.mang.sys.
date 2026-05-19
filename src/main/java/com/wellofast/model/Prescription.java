package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "prescriptions")
public class Prescription {
    @Id
    private String id;

    private String patientId;       // Patient collection id
    private String patientUserId;   // User id if patient has a portal account
    private String patientName;

    private String doctorId;
    private String doctorName;
    private String department;

    private String diagnosis;
    private String instructions;    // General instructions

    private List<Medicine> medicines = new ArrayList<>();

    private LocalDate date;
    private LocalDateTime createdAt;
}
