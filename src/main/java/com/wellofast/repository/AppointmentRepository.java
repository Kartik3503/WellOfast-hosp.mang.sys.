package com.wellofast.repository;

import com.wellofast.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByDoctorIdOrderByDateDescTimeDesc(String doctorId);
    List<Appointment> findByDateOrderByTimeAsc(LocalDate date);
    List<Appointment> findByStatusOrderByDateDesc(String status);
    List<Appointment> findAllByOrderByDateDescTimeDesc();
    List<Appointment> findByBookedByPatientUserIdOrderByDateDescTimeDesc(String patientUserId);
    List<Appointment> findByDoctorIdAndDateAndStatusNot(String doctorId, LocalDate date, String status);
    long countByStatus(String status);
    long countByDate(LocalDate date);
    long countByBookedByPatientUserId(String patientUserId);
}
