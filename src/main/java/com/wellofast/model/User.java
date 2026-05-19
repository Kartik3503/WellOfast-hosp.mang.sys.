package com.wellofast.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank @Indexed(unique = true)
    private String username;

    @NotBlank @Size(min = 6)
    private String password;

    @Email
    private String email;

    /** ADMIN, DOCTOR, EMPLOYEE */
    @NotBlank
    private String role;

    private String department;
    private String designation;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    private String employeeId;
    private String specialization;   // for doctors
    private String licenseNumber;    // for doctors
    private double salary;
    private boolean active = true;
    private String profileImage;

    // Hospital org info (for birth-cert authority)
    private String hospitalName;
    private String hospitalRegNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
