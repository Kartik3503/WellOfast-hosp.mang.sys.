package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Document(collection = "birth_certificates")
public class BirthCertificate {
    @Id private String id;
    @Indexed(unique = true) private String certificateNumber;
    private String issuedByUserId;
    private String hospitalName;
    private String hospitalRegistrationNumber;

    @NotBlank private String childName;
    @NotNull private String gender;
    @NotNull private LocalDate dateOfBirth;
    @NotNull private LocalTime timeOfBirth;
    @NotBlank private String placeOfBirth;
    private String birthWeight;
    private String birthHeight;
    private String bloodGroup;

    @NotBlank private String motherName;
    private String motherAge;
    private String motherNationality;
    private String motherReligion;
    private String motherOccupation;
    private String motherAadhaarNumber;
    private String motherAddress;

    @NotBlank private String fatherName;
    private String fatherAge;
    private String fatherNationality;
    private String fatherReligion;
    private String fatherOccupation;
    private String fatherAadhaarNumber;
    private String fatherAddress;

    private String permanentAddress;
    private String permanentCity;
    private String permanentState;
    private String permanentPincode;

    private String attendingDoctorName;
    private String attendingDoctorRegistrationNumber;
    private String sanctionedByName;
    private String sanctionedByDesignation;
    private String sanctionedByLicenseNumber;
    private String informantName;
    private String informantRelation;
    private String informantAddress;

    private String status = "ISSUED";
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String remarks;
}
