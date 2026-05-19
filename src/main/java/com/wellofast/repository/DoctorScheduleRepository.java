package com.wellofast.repository;

import com.wellofast.model.DoctorSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository extends MongoRepository<DoctorSchedule, String> {
    Optional<DoctorSchedule> findByDoctorId(String doctorId);
    List<DoctorSchedule> findByActiveTrue();
    List<DoctorSchedule> findByDepartment(String department);
    List<DoctorSchedule> findBySpecializationContainingIgnoreCase(String specialization);
}
