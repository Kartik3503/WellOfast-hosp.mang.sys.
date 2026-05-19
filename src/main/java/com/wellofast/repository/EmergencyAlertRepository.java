package com.wellofast.repository;

import com.wellofast.model.EmergencyAlert;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EmergencyAlertRepository extends MongoRepository<EmergencyAlert, String> {
    List<EmergencyAlert> findByPatientUserIdOrderByCreatedAtDesc(String patientUserId);
    List<EmergencyAlert> findByStatusOrderByCreatedAtDesc(String status);
    List<EmergencyAlert> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
    List<EmergencyAlert> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
