package com.wellofast.controller;

import com.wellofast.model.*;
import com.wellofast.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

@Controller
@RequestMapping("/portal")
public class PatientPortalController {

    @Autowired private UserService userService;
    @Autowired private HospitalService hs;
    @Autowired private PdfGeneratorService pdfService;

    private User getPatient(Authentication auth) {
        return userService.findByUsername(auth.getName()).orElseThrow();
    }

    // ══════════════════════════════════════════
    //  PATIENT DASHBOARD
    // ══════════════════════════════════════════
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);

        List<Appointment> allAppts = hs.apptsByPatientUserId(patient.getId());
        List<Appointment> upcoming = allAppts.stream()
                .filter(a -> !a.getDate().isBefore(LocalDate.now()) && "SCHEDULED".equals(a.getStatus()))
                .limit(5).toList();
        List<Appointment> past = allAppts.stream()
                .filter(a -> a.getDate().isBefore(LocalDate.now()) || !"SCHEDULED".equals(a.getStatus()))
                .limit(5).toList();

        m.addAttribute("upcomingAppointments", upcoming);
        m.addAttribute("pastAppointments", past);
        m.addAttribute("totalAppointments", allAppts.size());

        List<Prescription> prescriptions = hs.prescriptionsByPatientUserId(patient.getId());
        m.addAttribute("recentPrescriptions", prescriptions.stream().limit(3).toList());
        m.addAttribute("totalPrescriptions", prescriptions.size());

        List<MedicalRecord> records = hs.medicalRecordsByPatientUserId(patient.getId());
        m.addAttribute("recentRecords", records.stream().limit(3).toList());
        m.addAttribute("totalRecords", records.size());

        // Notifications
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));

        // Available doctors for the dashboard section
        List<DoctorSchedule> availableDoctors = hs.allActiveSchedules();
        m.addAttribute("availableDoctors", availableDoctors.stream().limit(6).toList());
        m.addAttribute("totalDoctors", availableDoctors.size());

        return "portal/patient-dashboard";
    }

    // ══════════════════════════════════════════
    //  DOCTOR DIRECTORY
    // ══════════════════════════════════════════
    @GetMapping("/doctors")
    public String doctorDirectory(@RequestParam(required = false) String dept,
                                   @RequestParam(required = false) String search,
                                   Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);

        List<DoctorSchedule> schedules;
        if (dept != null && !dept.isEmpty()) {
            schedules = hs.schedulesByDepartment(dept);
        } else if (search != null && !search.isEmpty()) {
            schedules = hs.searchSchedules(search);
        } else {
            schedules = hs.allActiveSchedules();
        }
        m.addAttribute("schedules", schedules);
        m.addAttribute("departments", hs.allDepts());
        m.addAttribute("selectedDept", dept);
        m.addAttribute("searchQuery", search);
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/doctor-directory";
    }

    // ══════════════════════════════════════════
    //  BOOK APPOINTMENT
    // ══════════════════════════════════════════
    @GetMapping("/book/{doctorId}")
    public String bookForm(@PathVariable String doctorId, Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);

        Optional<DoctorSchedule> schedOpt = hs.scheduleByDoctorId(doctorId);
        if (schedOpt.isEmpty()) return "redirect:/portal/doctors";

        DoctorSchedule schedule = schedOpt.get();
        Optional<User> doctorOpt = userService.findById(doctorId);
        if (doctorOpt.isEmpty()) return "redirect:/portal/doctors";

        m.addAttribute("doctor", doctorOpt.get());
        m.addAttribute("schedule", schedule);
        m.addAttribute("appointment", new Appointment());

        List<Map<String, Object>> availableDates = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
            if (schedule.getAvailableDays().contains(dayName)) {
                int bookedCount = hs.countDoctorApptsOnDate(doctorId, date);
                int slotsLeft = schedule.getMaxPatientsPerDay() - bookedCount;
                if (slotsLeft > 0) {
                    Map<String, Object> dateInfo = new HashMap<>();
                    dateInfo.put("date", date);
                    dateInfo.put("dayName", date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
                    dateInfo.put("slotsLeft", slotsLeft);
                    availableDates.add(dateInfo);
                }
            }
        }
        m.addAttribute("availableDates", availableDates);

        List<String> timeSlots = new ArrayList<>();
        if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
            LocalTime t = schedule.getStartTime();
            while (t.isBefore(schedule.getEndTime())) {
                timeSlots.add(t.toString());
                t = t.plusMinutes(30);
            }
        }
        m.addAttribute("timeSlots", timeSlots);
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));

        return "portal/book-appointment";
    }

    @PostMapping("/book")
    public String bookAppointment(@ModelAttribute Appointment appt,
                                  @RequestParam String doctorId,
                                  Authentication auth, RedirectAttributes ra) {
        User patient = getPatient(auth);

        Optional<User> doctorOpt = userService.findById(doctorId);
        Optional<DoctorSchedule> schedOpt = hs.scheduleByDoctorId(doctorId);
        if (doctorOpt.isEmpty() || schedOpt.isEmpty()) {
            ra.addFlashAttribute("error", "Doctor not available.");
            return "redirect:/portal/doctors";
        }

        User doctor = doctorOpt.get();
        DoctorSchedule schedule = schedOpt.get();

        int bookedCount = hs.countDoctorApptsOnDate(doctorId, appt.getDate());
        if (bookedCount >= schedule.getMaxPatientsPerDay()) {
            ra.addFlashAttribute("error", "No slots available for this date. Please choose another date.");
            return "redirect:/portal/book/" + doctorId;
        }

        appt.setPatientName(patient.getFullName());
        appt.setBookedByPatientUserId(patient.getId());
        appt.setDoctorId(doctor.getId());
        appt.setDoctorName(doctor.getFullName());
        appt.setDepartment(doctor.getDepartment());
        appt.setFee(schedule.getConsultationFee());
        appt.setStatus("SCHEDULED");
        appt.setCreatedAt(LocalDateTime.now());
        hs.saveAppointment(appt);

        // 🔔 Notify doctor about new appointment
        hs.createNotification(doctor.getId(), "APPOINTMENT",
                "New Appointment Booked", patient.getFullName() + " booked an appointment on " + appt.getDate() + " at " + appt.getTime(),
                "/doctor/dashboard", "📅");

        // 🔔 Confirm to patient
        hs.createNotification(patient.getId(), "APPOINTMENT",
                "Appointment Confirmed", "Your appointment with Dr. " + doctor.getFullName() + " is confirmed for " + appt.getDate(),
                "/portal/appointments", "✅");

        ra.addFlashAttribute("success", "🎉 Appointment booked successfully with Dr. " + doctor.getFullName() + "!");
        return "redirect:/portal/appointments";
    }

    // ══════════════════════════════════════════
    //  MY APPOINTMENTS
    // ══════════════════════════════════════════
    @GetMapping("/appointments")
    public String myAppointments(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);

        List<Appointment> all = hs.apptsByPatientUserId(patient.getId());
        m.addAttribute("upcoming", all.stream()
                .filter(a -> !a.getDate().isBefore(LocalDate.now()) && "SCHEDULED".equals(a.getStatus())).toList());
        m.addAttribute("past", all.stream()
                .filter(a -> a.getDate().isBefore(LocalDate.now()) || !"SCHEDULED".equals(a.getStatus())).toList());
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/my-appointments";
    }

    @GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable String id, Authentication auth, RedirectAttributes ra) {
        User patient = getPatient(auth);
        hs.apptById(id).ifPresent(a -> {
            if (patient.getId().equals(a.getBookedByPatientUserId())) {
                a.setStatus("CANCELLED");
                hs.saveAppointment(a);
                // Notify doctor
                hs.createNotification(a.getDoctorId(), "APPOINTMENT",
                        "Appointment Cancelled", patient.getFullName() + " cancelled the appointment on " + a.getDate(),
                        "/doctor/dashboard", "❌");
                ra.addFlashAttribute("success", "Appointment cancelled successfully.");
            }
        });
        return "redirect:/portal/appointments";
    }

    // ══════════════════════════════════════════
    //  MY MEDICAL RECORDS
    // ══════════════════════════════════════════
    @GetMapping("/records")
    public String myRecords(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        m.addAttribute("records", hs.medicalRecordsByPatientUserId(patient.getId()));
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/my-records";
    }

    @GetMapping("/records/{id}")
    public String recordDetail(@PathVariable String id, Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        Optional<MedicalRecord> record = hs.medicalRecordById(id);
        if (record.isEmpty() || !patient.getId().equals(record.get().getPatientUserId())) {
            return "redirect:/portal/records";
        }
        m.addAttribute("record", record.get());
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/record-detail";
    }

    // ══════════════════════════════════════════
    //  DOWNLOAD MEDICAL RECORD PDF
    // ══════════════════════════════════════════
    @GetMapping("/records/{id}/pdf")
    public ResponseEntity<byte[]> downloadRecordPdf(@PathVariable String id, Authentication auth) {
        User patient = getPatient(auth);
        Optional<MedicalRecord> recordOpt = hs.medicalRecordById(id);
        if (recordOpt.isEmpty() || !patient.getId().equals(recordOpt.get().getPatientUserId())) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] pdf = pdfService.generateMedicalRecordPdf(recordOpt.get(), patient);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"medical_record_" + id + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF).body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ══════════════════════════════════════════
    //  MY PRESCRIPTIONS
    // ══════════════════════════════════════════
    @GetMapping("/prescriptions")
    public String myPrescriptions(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        m.addAttribute("prescriptions", hs.prescriptionsByPatientUserId(patient.getId()));
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/my-prescriptions";
    }

    @GetMapping("/prescriptions/{id}")
    public String prescriptionDetail(@PathVariable String id, Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        Optional<Prescription> presc = hs.prescriptionById(id);
        if (presc.isEmpty() || !patient.getId().equals(presc.get().getPatientUserId())) {
            return "redirect:/portal/prescriptions";
        }
        m.addAttribute("prescription", presc.get());
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/prescription-detail";
    }

    // ══════════════════════════════════════════
    //  DOWNLOAD PRESCRIPTION PDF
    // ══════════════════════════════════════════
    @GetMapping("/prescriptions/{id}/pdf")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(@PathVariable String id, Authentication auth) {
        User patient = getPatient(auth);
        Optional<Prescription> prescOpt = hs.prescriptionById(id);
        if (prescOpt.isEmpty() || !patient.getId().equals(prescOpt.get().getPatientUserId())) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] pdf = pdfService.generatePrescriptionPdf(prescOpt.get(), patient);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"prescription_" + id + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF).body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ══════════════════════════════════════════
    //  CHAT
    // ══════════════════════════════════════════
    @GetMapping("/chat")
    public String chatPage(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        m.addAttribute("conversations", hs.getUserConversations(patient.getId()));
        // Get list of doctors this patient has had appointments with
        List<Appointment> appts = hs.apptsByPatientUserId(patient.getId());
        Set<String> doctorIds = new HashSet<>();
        List<Map<String, String>> myDoctors = new ArrayList<>();
        for (Appointment a : appts) {
            if (a.getDoctorId() != null && doctorIds.add(a.getDoctorId())) {
                myDoctors.add(Map.of("id", a.getDoctorId(), "name", a.getDoctorName()));
            }
        }
        m.addAttribute("myDoctors", myDoctors);
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/patient-chat";
    }

    @GetMapping("/chat/{doctorId}")
    public String chatWithDoctor(@PathVariable String doctorId, Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        User doctor = userService.findById(doctorId).orElse(null);
        if (doctor == null) return "redirect:/portal/chat";
        m.addAttribute("otherUser", doctor);

        String convId = HospitalService.makeConversationId(patient.getId(), doctorId);
        hs.markConversationRead(convId, patient.getId());

        m.addAttribute("messages", hs.getConversation(patient.getId(), doctorId));
        // Get list of doctors for sidebar
        List<Appointment> appts = hs.apptsByPatientUserId(patient.getId());
        Set<String> doctorIds = new HashSet<>();
        List<Map<String, String>> myDoctors = new ArrayList<>();
        for (Appointment a : appts) {
            if (a.getDoctorId() != null && doctorIds.add(a.getDoctorId())) {
                myDoctors.add(Map.of("id", a.getDoctorId(), "name", a.getDoctorName()));
            }
        }
        m.addAttribute("myDoctors", myDoctors);
        m.addAttribute("conversations", hs.getUserConversations(patient.getId()));
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/patient-chat";
    }

    // ══════════════════════════════════════════
    //  NOTIFICATIONS
    // ══════════════════════════════════════════
    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        m.addAttribute("notifications", hs.notificationsByUser(patient.getId()));
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/patient-notifications";
    }

    // ══════════════════════════════════════════
    //  MY PROFILE
    // ══════════════════════════════════════════
    @GetMapping("/profile")
    public String profile(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
        return "portal/my-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName, @RequestParam String email,
                                @RequestParam String phone, @RequestParam String gender,
                                @RequestParam String bloodGroup, @RequestParam String address,
                                @RequestParam String city, @RequestParam String state,
                                @RequestParam(required = false) String emergencyContact,
                                @RequestParam(required = false) String allergies,
                                Authentication auth, RedirectAttributes ra) {
        User patient = getPatient(auth);
        patient.setFullName(fullName);
        patient.setEmail(email);
        patient.setPhone(phone);
        patient.setGender(gender);
        patient.setAddress(address);
        patient.setCity(city);
        patient.setState(state);
        userService.save(patient);
        ra.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/portal/profile";
    }

    // ══════════════════════════════════════════
    //  FEATURE: VIDEO CONSULTATION
    // ══════════════════════════════════════════
    @GetMapping("/video-call/{appointmentId}")
    public String videoCall(@PathVariable String appointmentId, Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        Appointment appt = hs.apptById(appointmentId).orElseThrow();
        // Generate a unique Jitsi room name
        String roomName = "WellOfast-" + appointmentId.substring(0, Math.min(8, appointmentId.length()));
        m.addAttribute("appointment", appt);
        m.addAttribute("roomName", roomName);
        m.addAttribute("displayName", patient.getFullName());
        addBadgeCounts(m, patient);
        return "portal/video-call";
    }

    // ══════════════════════════════════════════
    //  FEATURE: LAB TEST BOOKING
    // ══════════════════════════════════════════
    @GetMapping("/lab-tests")
    public String labTests(@RequestParam(required = false) String category, Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        List<LabTest> tests = (category != null && !category.isEmpty())
                ? hs.labTestsByCategory(category)
                : hs.allActiveLabTests();
        m.addAttribute("tests", tests);
        m.addAttribute("selectedCategory", category);
        m.addAttribute("categories", List.of("BLOOD", "URINE", "IMAGING", "CARDIAC", "THYROID", "DIABETES", "LIVER", "KIDNEY"));
        addBadgeCounts(m, patient);
        return "portal/lab-tests";
    }

    @PostMapping("/lab-tests/book")
    public String bookLabTest(@RequestParam String testId, @RequestParam String scheduledDate,
                              Authentication auth, RedirectAttributes ra) {
        User patient = getPatient(auth);
        LabTest test = hs.labTestById(testId).orElseThrow();
        LabBooking booking = new LabBooking();
        booking.setPatientUserId(patient.getId());
        booking.setPatientName(patient.getFullName());
        booking.setTestId(test.getId());
        booking.setTestName(test.getName());
        booking.setTestCategory(test.getCategory());
        booking.setPrice(test.getPrice());
        booking.setScheduledDate(LocalDate.parse(scheduledDate));
        booking.setStatus("BOOKED");
        hs.saveLabBooking(booking);
        hs.createNotification(patient.getId(), "LAB_RESULT", "Lab Test Booked",
                test.getName() + " scheduled for " + scheduledDate, "/portal/lab-tests/my-bookings", "🩸");
        ra.addFlashAttribute("success", test.getName() + " booked for " + scheduledDate + "!");
        return "redirect:/portal/lab-tests/my-bookings";
    }

    @GetMapping("/lab-tests/my-bookings")
    public String myLabBookings(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        m.addAttribute("bookings", hs.labBookingsByPatient(patient.getId()));
        addBadgeCounts(m, patient);
        return "portal/lab-bookings";
    }

    // ══════════════════════════════════════════
    //  FEATURE: HEALTH TRACKER
    // ══════════════════════════════════════════
    @GetMapping("/health-tracker")
    public String healthTracker(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        List<HealthLog> logs = hs.healthLogsByPatient(patient.getId());
        m.addAttribute("logs", logs.stream().limit(30).toList());
        m.addAttribute("todayLog", hs.healthLogByDate(patient.getId(), LocalDate.now()).orElse(null));
        // Chart data — last 30 days
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);
        List<HealthLog> chartData = hs.healthLogsBetween(patient.getId(), start, end);
        m.addAttribute("chartData", chartData);
        addBadgeCounts(m, patient);
        return "portal/health-tracker";
    }

    @PostMapping("/health-tracker/save")
    public String saveHealthLog(@RequestParam(required = false) Integer bpSystolic,
                                @RequestParam(required = false) Integer bpDiastolic,
                                @RequestParam(required = false) Integer heartRate,
                                @RequestParam(required = false) Double bloodSugar,
                                @RequestParam(required = false) String sugarType,
                                @RequestParam(required = false) Double weight,
                                @RequestParam(required = false) Double temperature,
                                @RequestParam(required = false) Integer oxygenSaturation,
                                @RequestParam(required = false) String mood,
                                @RequestParam(required = false) String notes,
                                Authentication auth, RedirectAttributes ra) {
        User patient = getPatient(auth);
        // Update existing or create new for today
        HealthLog log = hs.healthLogByDate(patient.getId(), LocalDate.now()).orElse(new HealthLog());
        log.setPatientUserId(patient.getId());
        log.setDate(LocalDate.now());
        if (bpSystolic != null) log.setBpSystolic(bpSystolic);
        if (bpDiastolic != null) log.setBpDiastolic(bpDiastolic);
        if (heartRate != null) log.setHeartRate(heartRate);
        if (bloodSugar != null) log.setBloodSugar(bloodSugar);
        if (sugarType != null) log.setSugarType(sugarType);
        if (weight != null) log.setWeight(weight);
        if (temperature != null) log.setTemperature(temperature);
        if (oxygenSaturation != null) log.setOxygenSaturation(oxygenSaturation);
        if (mood != null) log.setMood(mood);
        if (notes != null) log.setNotes(notes);
        hs.saveHealthLog(log);
        ra.addFlashAttribute("success", "Today's health data saved!");
        return "redirect:/portal/health-tracker";
    }

    // ══════════════════════════════════════════
    //  FEATURE: EMERGENCY SOS
    // ══════════════════════════════════════════
    @GetMapping("/emergency")
    public String emergencyPage(Authentication auth, Model m) {
        User patient = getPatient(auth);
        m.addAttribute("user", patient);
        m.addAttribute("myAlerts", hs.emergencyAlertsByPatient(patient.getId()));
        addBadgeCounts(m, patient);
        return "portal/emergency";
    }

    @PostMapping("/emergency/sos")
    public String triggerSOS(@RequestParam String emergencyType,
                             @RequestParam(required = false) String description,
                             Authentication auth, RedirectAttributes ra) {
        User patient = getPatient(auth);
        EmergencyAlert alert = new EmergencyAlert();
        alert.setPatientUserId(patient.getId());
        alert.setPatientName(patient.getFullName());
        alert.setPatientPhone(patient.getPhone());
        alert.setPatientAddress((patient.getAddress() != null ? patient.getAddress() : "") +
                (patient.getCity() != null ? ", " + patient.getCity() : ""));
        alert.setEmergencyType(emergencyType);
        alert.setDescription(description);
        alert.setSeverity("MEDICAL".equals(emergencyType) || "CARDIAC".equals(emergencyType) ? "CRITICAL" : "HIGH");
        hs.createEmergencyAlert(alert);

        // Notify all doctors
        List<User> doctors = userService.findByRole("DOCTOR");
        for (User doc : doctors) {
            hs.createNotification(doc.getId(), "SYSTEM", "🚨 EMERGENCY ALERT",
                    patient.getFullName() + " has triggered an SOS — " + emergencyType,
                    "/doctor/emergencies", "🆘");
        }
        ra.addFlashAttribute("success", "Emergency alert sent! Help is on the way. Ambulance: 108");
        return "redirect:/portal/emergency";
    }

    // ── Helper: Add badge counts to every page ──
    private void addBadgeCounts(Model m, User patient) {
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(patient.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(patient.getId()));
    }
}
