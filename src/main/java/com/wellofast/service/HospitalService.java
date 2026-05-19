package com.wellofast.service;

import com.wellofast.model.*;
import com.wellofast.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    @Autowired private PatientRepository patientRepo;
    @Autowired private AppointmentRepository apptRepo;
    @Autowired private DepartmentRepository deptRepo;
    @Autowired private LeaveRequestRepository leaveRepo;
    @Autowired private BirthCertificateRepository certRepo;
    @Autowired private PrescriptionRepository prescRepo;
    @Autowired private MedicalRecordRepository recordRepo;
    @Autowired private DoctorScheduleRepository scheduleRepo;
    @Autowired private NotificationRepository notifRepo;
    @Autowired private ChatMessageRepository chatRepo;
    @Autowired private LabTestRepository labTestRepo;
    @Autowired private LabBookingRepository labBookingRepo;
    @Autowired private HealthLogRepository healthLogRepo;
    @Autowired private EmergencyAlertRepository emergencyRepo;

    // ── Patients ──
    public Patient savePatient(Patient p) {
        if (p.getCreatedAt() == null) p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return patientRepo.save(p);
    }
    public List<Patient> allPatients() { return patientRepo.findAllByOrderByCreatedAtDesc(); }
    public Optional<Patient> patientById(String id) { return patientRepo.findById(id); }
    public List<Patient> searchPatients(String q) { return patientRepo.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(q); }
    public List<Patient> patientsByDoctor(String docId) { return patientRepo.findByAssignedDoctorIdOrderByCreatedAtDesc(docId); }
    public long countPatientsByStatus(String s) { return patientRepo.countByStatus(s); }
    public long countPatients() { return patientRepo.count(); }
    public void deletePatient(String id) { patientRepo.deleteById(id); }

    // ── Appointments ──
    public Appointment saveAppointment(Appointment a) {
        if (a.getCreatedAt() == null) a.setCreatedAt(LocalDateTime.now());
        return apptRepo.save(a);
    }
    public List<Appointment> allAppointments() { return apptRepo.findAllByOrderByDateDescTimeDesc(); }
    public Optional<Appointment> apptById(String id) { return apptRepo.findById(id); }
    public List<Appointment> apptsByDoctor(String docId) { return apptRepo.findByDoctorIdOrderByDateDescTimeDesc(docId); }
    public List<Appointment> todayAppointments() { return apptRepo.findByDateOrderByTimeAsc(LocalDate.now()); }
    public long countApptsByStatus(String s) { return apptRepo.countByStatus(s); }
    public long countTodayAppts() { return apptRepo.countByDate(LocalDate.now()); }
    public void deleteAppointment(String id) { apptRepo.deleteById(id); }

    public List<Appointment> apptsByPatientUserId(String userId) {
        return apptRepo.findByBookedByPatientUserIdOrderByDateDescTimeDesc(userId);
    }
    public long countApptsByPatientUserId(String userId) {
        return apptRepo.countByBookedByPatientUserId(userId);
    }
    public int countDoctorApptsOnDate(String doctorId, LocalDate date) {
        return apptRepo.findByDoctorIdAndDateAndStatusNot(doctorId, date, "CANCELLED").size();
    }

    public List<Appointment> todayApptsByDoctor(String docId) {
        return apptRepo.findByDateOrderByTimeAsc(LocalDate.now()).stream()
                .filter(a -> docId.equals(a.getDoctorId())).toList();
    }
    public double totalEarningsByDoctor(String docId) {
        return apptsByDoctor(docId).stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .mapToDouble(Appointment::getFee).sum();
    }
    public double monthlyEarningsByDoctor(String docId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        return apptsByDoctor(docId).stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()) && !a.getDate().isBefore(startOfMonth))
                .mapToDouble(Appointment::getFee).sum();
    }

    // ── Departments ──
    public Department saveDept(Department d) {
        if (d.getCreatedAt() == null) d.setCreatedAt(LocalDateTime.now());
        return deptRepo.save(d);
    }
    public List<Department> allDepts() { return deptRepo.findAll(); }
    public Optional<Department> deptById(String id) { return deptRepo.findById(id); }
    public void deleteDept(String id) { deptRepo.deleteById(id); }

    // ── Leave Requests ──
    public LeaveRequest saveLeave(LeaveRequest lr) {
        if (lr.getCreatedAt() == null) lr.setCreatedAt(LocalDateTime.now());
        return leaveRepo.save(lr);
    }
    public List<LeaveRequest> allLeaves() { return leaveRepo.findAllByOrderByCreatedAtDesc(); }
    public List<LeaveRequest> leavesByUser(String userId) { return leaveRepo.findByUserIdOrderByCreatedAtDesc(userId); }
    public List<LeaveRequest> pendingLeaves() { return leaveRepo.findByStatusOrderByCreatedAtDesc("PENDING"); }
    public Optional<LeaveRequest> leaveById(String id) { return leaveRepo.findById(id); }
    public long countPendingLeaves() { return leaveRepo.countByStatus("PENDING"); }

    // ── Birth Certificates ──
    public BirthCertificate saveCert(BirthCertificate c, User u) {
        String d = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        c.setCertificateNumber("BC-" + d + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        c.setIssuedByUserId(u.getId());
        c.setHospitalName(u.getHospitalName() != null ? u.getHospitalName() : "WellOfast Hospital");
        c.setHospitalRegistrationNumber(u.getHospitalRegNumber() != null ? u.getHospitalRegNumber() : "N/A");
        c.setSanctionedByName(u.getFullName());
        c.setSanctionedByDesignation(u.getDesignation());
        c.setSanctionedByLicenseNumber(u.getLicenseNumber());
        c.setIssuedAt(LocalDateTime.now());
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        c.setStatus("ISSUED");
        return certRepo.save(c);
    }
    public List<BirthCertificate> allCerts() { return certRepo.findAllByOrderByCreatedAtDesc(); }
    public Optional<BirthCertificate> certById(String id) { return certRepo.findById(id); }
    public List<BirthCertificate> searchCerts(String name) { return certRepo.findByChildNameContainingIgnoreCaseOrderByCreatedAtDesc(name); }
    public long countCerts() { return certRepo.count(); }

    // ── Prescriptions ──
    public Prescription savePrescription(Prescription p) {
        if (p.getCreatedAt() == null) p.setCreatedAt(LocalDateTime.now());
        return prescRepo.save(p);
    }
    public List<Prescription> allPrescriptions() { return prescRepo.findAllByOrderByCreatedAtDesc(); }
    public Optional<Prescription> prescriptionById(String id) { return prescRepo.findById(id); }
    public List<Prescription> prescriptionsByPatientUserId(String userId) { return prescRepo.findByPatientUserIdOrderByDateDesc(userId); }
    public List<Prescription> prescriptionsByDoctor(String docId) { return prescRepo.findByDoctorIdOrderByDateDesc(docId); }
    public long countPrescriptionsByPatientUserId(String userId) { return prescRepo.countByPatientUserId(userId); }

    // ── Medical Records ──
    public MedicalRecord saveMedicalRecord(MedicalRecord r) {
        if (r.getCreatedAt() == null) r.setCreatedAt(LocalDateTime.now());
        return recordRepo.save(r);
    }
    public List<MedicalRecord> allMedicalRecords() { return recordRepo.findAllByOrderByCreatedAtDesc(); }
    public Optional<MedicalRecord> medicalRecordById(String id) { return recordRepo.findById(id); }
    public List<MedicalRecord> medicalRecordsByPatientUserId(String userId) { return recordRepo.findByPatientUserIdOrderByDateDesc(userId); }
    public List<MedicalRecord> medicalRecordsByDoctor(String docId) { return recordRepo.findByDoctorIdOrderByDateDesc(docId); }
    public long countRecordsByPatientUserId(String userId) { return recordRepo.countByPatientUserId(userId); }

    // ── Doctor Schedules ──
    public DoctorSchedule saveSchedule(DoctorSchedule s) { return scheduleRepo.save(s); }
    public List<DoctorSchedule> allActiveSchedules() { return scheduleRepo.findByActiveTrue(); }
    public Optional<DoctorSchedule> scheduleByDoctorId(String doctorId) { return scheduleRepo.findByDoctorId(doctorId); }
    public List<DoctorSchedule> schedulesByDepartment(String dept) { return scheduleRepo.findByDepartment(dept); }
    public List<DoctorSchedule> searchSchedules(String q) { return scheduleRepo.findBySpecializationContainingIgnoreCase(q); }

    // ══════════════════════════════════════════
    //  NOTIFICATIONS
    // ══════════════════════════════════════════
    public Notification createNotification(String userId, String type, String title, String message, String link, String icon) {
        Notification n = new Notification();
        n.setUserId(userId); n.setType(type); n.setTitle(title); n.setMessage(message);
        n.setLink(link); n.setIcon(icon); n.setRead(false); n.setCreatedAt(LocalDateTime.now());
        return notifRepo.save(n);
    }
    public List<Notification> notificationsByUser(String userId) { return notifRepo.findByUserIdOrderByCreatedAtDesc(userId); }
    public List<Notification> unreadNotifications(String userId) { return notifRepo.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId); }
    public long unreadNotifCount(String userId) { return notifRepo.countByUserIdAndReadFalse(userId); }
    public void markNotifRead(String id) { notifRepo.findById(id).ifPresent(n -> { n.setRead(true); notifRepo.save(n); }); }
    public void markAllNotifsRead(String userId) {
        notifRepo.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).forEach(n -> { n.setRead(true); notifRepo.save(n); });
    }

    // ══════════════════════════════════════════
    //  CHAT MESSAGES
    // ══════════════════════════════════════════
    public static String makeConversationId(String id1, String id2) {
        return id1.compareTo(id2) < 0 ? id1 + "_" + id2 : id2 + "_" + id1;
    }
    public ChatMessage sendMessage(String senderId, String senderName, String senderRole,
                                    String receiverId, String receiverName, String message) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(senderId); msg.setSenderName(senderName); msg.setSenderRole(senderRole);
        msg.setReceiverId(receiverId); msg.setReceiverName(receiverName);
        msg.setConversationId(makeConversationId(senderId, receiverId));
        msg.setMessage(message); msg.setRead(false); msg.setTimestamp(LocalDateTime.now());
        return chatRepo.save(msg);
    }
    public List<ChatMessage> getConversation(String userId1, String userId2) {
        return chatRepo.findByConversationIdOrderByTimestampAsc(makeConversationId(userId1, userId2));
    }
    public long unreadChatCount(String userId) { return chatRepo.countByReceiverIdAndReadFalse(userId); }
    public void markConversationRead(String conversationId, String readerId) {
        chatRepo.findByConversationIdOrderByTimestampAsc(conversationId).stream()
                .filter(m -> readerId.equals(m.getReceiverId()) && !m.isRead())
                .forEach(m -> { m.setRead(true); chatRepo.save(m); });
    }
    public List<Map<String, Object>> getUserConversations(String userId) {
        List<ChatMessage> allMsgs = chatRepo.findBySenderIdOrReceiverIdOrderByTimestampDesc(userId, userId);
        Map<String, ChatMessage> latest = new LinkedHashMap<>();
        for (ChatMessage m : allMsgs) latest.putIfAbsent(m.getConversationId(), m);
        List<Map<String, Object>> convs = new ArrayList<>();
        for (ChatMessage m : latest.values()) {
            Map<String, Object> conv = new HashMap<>();
            conv.put("conversationId", m.getConversationId());
            conv.put("lastMessage", m.getMessage());
            conv.put("lastTimestamp", m.getTimestamp());
            conv.put("otherUserId", userId.equals(m.getSenderId()) ? m.getReceiverId() : m.getSenderId());
            conv.put("otherUserName", userId.equals(m.getSenderId()) ? m.getReceiverName() : m.getSenderName());
            conv.put("otherUserRole", userId.equals(m.getSenderId()) ?
                    (m.getSenderRole().equals("PATIENT") ? "DOCTOR" : "PATIENT") : m.getSenderRole());
            long unread = chatRepo.findByConversationIdOrderByTimestampAsc(m.getConversationId()).stream()
                    .filter(msg -> userId.equals(msg.getReceiverId()) && !msg.isRead()).count();
            conv.put("unreadCount", unread);
            convs.add(conv);
        }
        return convs;
    }

    // ══════════════════════════════════════════
    //  LAB TESTS & BOOKINGS
    // ══════════════════════════════════════════
    public LabTest saveLabTest(LabTest t) { if (t.getCreatedAt() == null) t.setCreatedAt(LocalDateTime.now()); return labTestRepo.save(t); }
    public List<LabTest> allActiveLabTests() { return labTestRepo.findByActiveTrueOrderByNameAsc(); }
    public List<LabTest> labTestsByCategory(String cat) { return labTestRepo.findByCategoryAndActiveTrueOrderByNameAsc(cat); }
    public Optional<LabTest> labTestById(String id) { return labTestRepo.findById(id); }
    public LabBooking saveLabBooking(LabBooking b) { if (b.getCreatedAt() == null) b.setCreatedAt(LocalDateTime.now()); return labBookingRepo.save(b); }
    public List<LabBooking> labBookingsByPatient(String userId) { return labBookingRepo.findByPatientUserIdOrderByCreatedAtDesc(userId); }
    public Optional<LabBooking> labBookingById(String id) { return labBookingRepo.findById(id); }
    public List<LabBooking> allLabBookings() { return labBookingRepo.findAllByOrderByCreatedAtDesc(); }
    public long countLabBookingsByPatient(String userId) { return labBookingRepo.countByPatientUserId(userId); }

    // ══════════════════════════════════════════
    //  HEALTH TRACKER
    // ══════════════════════════════════════════
    public HealthLog saveHealthLog(HealthLog log) { if (log.getCreatedAt() == null) log.setCreatedAt(LocalDateTime.now()); return healthLogRepo.save(log); }
    public List<HealthLog> healthLogsByPatient(String userId) { return healthLogRepo.findByPatientUserIdOrderByDateDesc(userId); }
    public List<HealthLog> healthLogsBetween(String userId, LocalDate start, LocalDate end) {
        return healthLogRepo.findByPatientUserIdAndDateBetweenOrderByDateAsc(userId, start, end);
    }
    public Optional<HealthLog> healthLogByDate(String userId, LocalDate date) { return healthLogRepo.findByPatientUserIdAndDate(userId, date); }
    public long countHealthLogs(String userId) { return healthLogRepo.countByPatientUserId(userId); }

    // ══════════════════════════════════════════
    //  EMERGENCY ALERTS
    // ══════════════════════════════════════════
    public EmergencyAlert createEmergencyAlert(EmergencyAlert a) {
        a.setStatus("ACTIVE"); a.setCreatedAt(LocalDateTime.now()); return emergencyRepo.save(a);
    }
    public List<EmergencyAlert> emergencyAlertsByPatient(String userId) { return emergencyRepo.findByPatientUserIdOrderByCreatedAtDesc(userId); }
    public List<EmergencyAlert> activeEmergencyAlerts() { return emergencyRepo.findByStatusInOrderByCreatedAtDesc(List.of("ACTIVE", "RESPONDING")); }
    public Optional<EmergencyAlert> emergencyAlertById(String id) { return emergencyRepo.findById(id); }
    public long countActiveEmergencies() { return emergencyRepo.countByStatus("ACTIVE"); }
    public EmergencyAlert respondToAlert(String alertId, String doctorId, String doctorName, String notes) {
        EmergencyAlert a = emergencyRepo.findById(alertId).orElseThrow();
        a.setStatus("RESPONDING"); a.setRespondedByDoctorId(doctorId);
        a.setRespondedByDoctorName(doctorName); a.setResponseNotes(notes); a.setRespondedAt(LocalDateTime.now());
        return emergencyRepo.save(a);
    }
    public EmergencyAlert resolveAlert(String alertId) {
        EmergencyAlert a = emergencyRepo.findById(alertId).orElseThrow();
        a.setStatus("RESOLVED"); a.setResolvedAt(LocalDateTime.now());
        return emergencyRepo.save(a);
    }
}
