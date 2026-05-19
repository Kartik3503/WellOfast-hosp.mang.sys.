package com.wellofast.controller;

import com.wellofast.model.User;
import com.wellofast.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @Autowired private UserService userService;
    @Autowired private HospitalService hs;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model m) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();

        // Redirect patients to their dedicated portal
        if ("PATIENT".equals(user.getRole())) {
            return "redirect:/portal/dashboard";
        }

        // Redirect doctors to their dedicated dashboard
        if ("DOCTOR".equals(user.getRole())) {
            return "redirect:/doctor/dashboard";
        }

        m.addAttribute("user", user);
        m.addAttribute("totalPatients", hs.countPatients());
        m.addAttribute("admittedPatients", hs.countPatientsByStatus("ADMITTED"));
        m.addAttribute("totalDoctors", userService.countByRole("DOCTOR"));
        m.addAttribute("totalEmployees", userService.countByRole("EMPLOYEE"));
        m.addAttribute("todayAppointments", hs.countTodayAppts());
        m.addAttribute("pendingLeaves", hs.countPendingLeaves());
        m.addAttribute("totalCertificates", hs.countCerts());
        m.addAttribute("recentAppointments", hs.todayAppointments());
        m.addAttribute("recentPatients", hs.allPatients().stream().limit(5).toList());
        m.addAttribute("unreadNotifs", hs.unreadNotifCount(user.getId()));
        return "dashboard";
    }
}
