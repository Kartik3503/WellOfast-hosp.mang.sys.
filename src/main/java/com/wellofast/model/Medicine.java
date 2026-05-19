package com.wellofast.model;

import lombok.Data;

@Data
public class Medicine {
    private String name;
    private String dosage;        // e.g. "500mg"
    private String frequency;     // e.g. "Twice a day"
    private String duration;      // e.g. "7 days"
    /** Tablet, Capsule, Syrup, Injection, Ointment, Drops */
    private String type;
    private String instructions;  // e.g. "After meals"
}
