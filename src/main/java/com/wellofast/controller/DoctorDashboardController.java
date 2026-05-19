package com.wellofast.controller;

import com.wellofast.model.*;
import com.wellofast.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {

    @Autowired private UserService userService;
    @Autowired private HospitalService hs;

    private User getDoctor(Authentication auth) {
        return userService.findByUsername(auth.getName()).orElseThrow();
    }

    // ══════════════════════════════════════════
    //  DOCTOR DASHBOARD
    // ══════════════════════════════════════════
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model m) {
        User doctor = getDoctor(auth);
        m.addAttribute("user", doctor);

        // Today's appointments
        List<Appointment> todayAppts = hs.todayApptsByDoctor(doctor.getId());
        m.addAttribute("todayAppointments", todayAppts);
        m.addAttribute("todayCount", todayAppts.size());

        // Scheduled today (not completed/cancelled)
        long scheduledToday = todayAppts.stream().filter(a -> "SCHEDULED".equals(a.getStatus())).count();
        m.addAttribute("scheduledToday", scheduledToday);

        // All doctor appointments
        List<Appointment> allAppts = hs.apptsByDoctor(doctor.getId());
        m.addAttribute("totalAppointments", allAppts.size());
        m.addAttribute("completedAppointments", allAppts.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count());

        // Earnings
        m.addAttribute("totalEarnings", hs.totalEarningsByDoctor(doctor.getId()));
        m.addAttribute("monthlyEarnings", hs.monthlyEarningsByDoctor(doctor.getId()));

        // Prescriptions & records counts
        List<Prescription> myPrescriptions = hs.prescriptionsByDoctor(doctor.getId());
        m.addAttribute("totalPrescriptions", myPrescriptions.size());

        List<MedicalRecord> myRecords = hs.medicalRecordsByDoctor(doctor.getId());
        m.addAttribute("totalRecords", myRecords.size());
        m.addAttribute("recentRecords", myRecords.stream().limit(5).toList());

        // Upcoming appointments (next 7 days)
        List<Appointment> upcoming = allAppts.stream()
                .filter(a -> "SCHEDULED".equals(a.getStatus()) && !a.getDate().isBefore(LocalDate.now()))
                .limit(10).toList();
        m.addAttribute("upcomingAppointments", upcoming);

        // Notifications & chat
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(doctor.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(doctor.getId()));

        return "doctor/doctor-dashboard";
    }

    // ══════════════════════════════════════════
    //  COMPLETE APPOINTMENT
    // ══════════════════════════════════════════
    @GetMapping("/complete/{id}")
    public String completeAppointment(@PathVariable String id, Authentication auth, RedirectAttributes ra) {
        User doctor = getDoctor(auth);
        hs.apptById(id).ifPresent(a -> {
            if (doctor.getId().equals(a.getDoctorId())) {
                a.setStatus("COMPLETED");
                hs.saveAppointment(a);
                // Notify patient
                if (a.getBookedByPatientUserId() != null) {
                    hs.createNotification(a.getBookedByPatientUserId(), "APPOINTMENT",
                            "Appointment Completed", "Your appointment with Dr. " + doctor.getFullName() + " has been marked as completed.",
                            "/portal/appointments", "✅");
                }
                ra.addFlashAttribute("success", "Appointment marked as completed.");
            }
        });
        return "redirect:/doctor/dashboard";
    }

    // ══════════════════════════════════════════
    //  DOCTOR CHAT PAGE
    // ══════════════════════════════════════════
    @GetMapping("/chat")
    public String chatPage(Authentication auth, Model m) {
        User doctor = getDoctor(auth);
        m.addAttribute("user", doctor);
        m.addAttribute("conversations", hs.getUserConversations(doctor.getId()));
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(doctor.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(doctor.getId()));
        return "doctor/doctor-chat";
    }

    @GetMapping("/chat/{patientId}")
    public String chatWithPatient(@PathVariable String patientId, Authentication auth, Model m) {
        User doctor = getDoctor(auth);
        m.addAttribute("user", doctor);
        User patient = userService.findById(patientId).orElse(null);
        if (patient == null) return "redirect:/doctor/chat";
        m.addAttribute("otherUser", patient);

        // Mark messages as read
        String convId = HospitalService.makeConversationId(doctor.getId(), patientId);
        hs.markConversationRead(convId, doctor.getId());

        m.addAttribute("messages", hs.getConversation(doctor.getId(), patientId));
        m.addAttribute("conversations", hs.getUserConversations(doctor.getId()));
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(doctor.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(doctor.getId()));
        return "doctor/doctor-chat";
    }

    // ══════════════════════════════════════════
    //  DOCTOR NOTIFICATIONS
    // ══════════════════════════════════════════
    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model m) {
        User doctor = getDoctor(auth);
        m.addAttribute("user", doctor);
        m.addAttribute("notifications", hs.notificationsByUser(doctor.getId()));
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(doctor.getId()));
        m.addAttribute("unreadChats", hs.unreadChatCount(doctor.getId()));
        return "doctor/doctor-notifications";
    }
}
