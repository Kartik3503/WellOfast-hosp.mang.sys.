package com.wellofast.repository;

import com.wellofast.model.LabBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LabBookingRepository extends MongoRepository<LabBooking, String> {
    List<LabBooking> findByPatientUserIdOrderByCreatedAtDesc(String patientUserId);
    List<LabBooking> findByStatusOrderByCreatedAtDesc(String status);
    List<LabBooking> findAllByOrderByCreatedAtDesc();
    long countByPatientUserId(String patientUserId);
    long countByStatus(String status);
}
