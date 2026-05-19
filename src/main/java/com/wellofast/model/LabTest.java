package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "lab_tests")
public class LabTest {
    @Id
    private String id;

    private String name;          // e.g. "Complete Blood Count (CBC)"
    private String category;      // BLOOD, URINE, IMAGING, CARDIAC, THYROID, etc.
    private String description;
    private double price;
    private String sampleType;    // Blood, Urine, Swab, etc.
    private String turnaroundTime; // "24 hours", "2-3 days"
    private boolean fasting;      // requires fasting?
    private boolean active = true;
    private LocalDateTime createdAt;
}
