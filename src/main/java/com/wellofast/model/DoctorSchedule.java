package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "doctor_schedules")
public class DoctorSchedule {
    @Id
    private String id;

    private String doctorId;        // User id of the doctor
    private String doctorName;
    private String specialization;
    private String department;
    private String qualification;

    private double consultationFee;

    /** e.g. ["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"] */
    private List<String> availableDays = new ArrayList<>();

    private LocalTime startTime;
    private LocalTime endTime;

    private int maxPatientsPerDay = 20;
    private boolean active = true;
}
