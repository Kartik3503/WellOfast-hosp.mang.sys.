package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "health_logs")
public class HealthLog {
    @Id
    private String id;

    private String patientUserId;
    private LocalDate date;

    // Vitals
    private Integer bpSystolic;      // e.g. 120
    private Integer bpDiastolic;     // e.g. 80
    private Integer heartRate;       // bpm
    private Double bloodSugar;       // mg/dL (fasting or random)
    private String sugarType;        // FASTING, POST_MEAL, RANDOM
    private Double weight;           // kg
    private Double temperature;      // °F
    private Integer oxygenSaturation; // SpO2 %

    private String mood;             // GREAT, GOOD, OKAY, BAD, TERRIBLE
    private String notes;

    private LocalDateTime createdAt;
}
