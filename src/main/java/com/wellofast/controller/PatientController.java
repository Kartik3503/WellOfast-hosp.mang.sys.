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
@RequestMapping("/patients")
public class PatientController {
    @Autowired private HospitalService hs;
    @Autowired private UserService us;

    @GetMapping
    public String list(@RequestParam(value="search",required=false) String q, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("patients", q != null && !q.isEmpty() ? hs.searchPatients(q) : hs.allPatients());
        m.addAttribute("search", q);
        m.addAttribute("doctors", us.findActiveDoctors());
        return "patient-list";
    }

    @GetMapping("/new")
    public String newForm(Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("patient", new Patient());
        m.addAttribute("doctors", us.findActiveDoctors());
        return "patient-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Patient patient, RedirectAttributes ra) {
        if (patient.getAssignedDoctorId() != null && !patient.getAssignedDoctorId().isEmpty()) {
            us.findById(patient.getAssignedDoctorId()).ifPresent(d -> patient.setAssignedDoctorName(d.getFullName()));
        }
        hs.savePatient(patient);
        ra.addFlashAttribute("success", "Patient record saved!");
        return "redirect:/patients";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable String id, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("patient", hs.patientById(id).orElseThrow());
        return "patient-detail";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("patient", hs.patientById(id).orElseThrow());
        m.addAttribute("doctors", us.findActiveDoctors());
        return "patient-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        hs.deletePatient(id);
        ra.addFlashAttribute("success", "Patient record deleted.");
        return "redirect:/patients";
    }
}
