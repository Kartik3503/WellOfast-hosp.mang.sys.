package com.wellofast.controller;

import com.wellofast.model.*;
import com.wellofast.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    @Autowired private HospitalService hs;
    @Autowired private UserService us;

    @GetMapping
    public String list(Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("appointments", hs.allAppointments());
        m.addAttribute("doctors", us.findActiveDoctors());
        return "appointment-list";
    }

    @GetMapping("/new")
    public String newForm(Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("appointment", new Appointment());
        m.addAttribute("doctors", us.findActiveDoctors());
        m.addAttribute("patients", hs.allPatients());
        return "appointment-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Appointment appt, RedirectAttributes ra) {
        if (appt.getDoctorId() != null && !appt.getDoctorId().isEmpty())
            us.findById(appt.getDoctorId()).ifPresent(d -> appt.setDoctorName(d.getFullName()));
        hs.saveAppointment(appt);
        ra.addFlashAttribute("success", "Appointment saved!");
        return "redirect:/appointments";
    }

    @GetMapping("/complete/{id}")
    public String complete(@PathVariable String id, RedirectAttributes ra) {
        hs.apptById(id).ifPresent(a -> { a.setStatus("COMPLETED"); hs.saveAppointment(a); });
        ra.addFlashAttribute("success", "Appointment marked complete.");
        return "redirect:/appointments";
    }

    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable String id, RedirectAttributes ra) {
        hs.apptById(id).ifPresent(a -> { a.setStatus("CANCELLED"); hs.saveAppointment(a); });
        return "redirect:/appointments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        hs.deleteAppointment(id);
        ra.addFlashAttribute("success", "Appointment deleted.");
        return "redirect:/appointments";
    }
}
