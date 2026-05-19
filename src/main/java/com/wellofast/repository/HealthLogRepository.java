package com.wellofast.repository;

import com.wellofast.model.HealthLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HealthLogRepository extends MongoRepository<HealthLog, String> {
    List<HealthLog> findByPatientUserIdOrderByDateDesc(String patientUserId);
    List<HealthLog> findByPatientUserIdAndDateBetweenOrderByDateAsc(String patientUserId, LocalDate start, LocalDate end);
    Optional<HealthLog> findByPatientUserIdAndDate(String patientUserId, LocalDate date);
    long countByPatientUserId(String patientUserId);
}
