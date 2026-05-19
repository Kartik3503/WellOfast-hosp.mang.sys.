package com.wellofast.repository;

import com.wellofast.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    List<Prescription> findByPatientUserIdOrderByDateDesc(String patientUserId);
    List<Prescription> findByPatientIdOrderByDateDesc(String patientId);
    List<Prescription> findByDoctorIdOrderByDateDesc(String doctorId);
    List<Prescription> findAllByOrderByCreatedAtDesc();
    long countByPatientUserId(String patientUserId);
}
