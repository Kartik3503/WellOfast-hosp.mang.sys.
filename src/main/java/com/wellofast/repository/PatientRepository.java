package com.wellofast.repository;

import com.wellofast.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PatientRepository extends MongoRepository<Patient, String> {
    List<Patient> findByStatusOrderByCreatedAtDesc(String status);
    List<Patient> findByAssignedDoctorIdOrderByCreatedAtDesc(String doctorId);
    List<Patient> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);
    List<Patient> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
