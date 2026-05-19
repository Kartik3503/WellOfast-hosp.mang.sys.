package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "medical_records")
public class MedicalRecord {
    @Id
    private String id;

    private String patientId;       // Patient collection id
    private String patientUserId;   // User id if patient has a portal account
    private String patientName;

    private String doctorId;
    private String doctorName;
    private String department;

    /** Checkup, Surgery, Lab Test, Procedure, Emergency, Follow-up */
    private String type;

    private String diagnosis;
    private String treatment;
    private String notes;

    // Vitals
    private String bloodPressure;
    private String heartRate;
    private String temperature;
    private String weight;
    private String oxygenSaturation;

    // Procedures & injections
    private List<String> procedures = new ArrayList<>();
    private List<String> injections = new ArrayList<>();

    // Lab results
    private String labTestName;
    private String labResult;
    private String labNormalRange;
    /** Normal, Abnormal, Critical */
    private String labStatus;

    private LocalDate date;
    private LocalDateTime createdAt;
}
