package com.wellofast.repository;

import com.wellofast.model.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPatientUserIdOrderByDateDesc(String patientUserId);
    List<MedicalRecord> findByPatientIdOrderByDateDesc(String patientId);
    List<MedicalRecord> findByDoctorIdOrderByDateDesc(String doctorId);
    List<MedicalRecord> findAllByOrderByCreatedAtDesc();
    long countByPatientUserId(String patientUserId);
}
