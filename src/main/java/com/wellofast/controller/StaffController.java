package com.wellofast.controller;

import com.wellofast.model.*;
import com.wellofast.service.*;
import com.wellofast.repository.DepartmentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/staff")
public class StaffController {
    @Autowired private UserService us;
    @Autowired private HospitalService hs;
    @Autowired private DepartmentRepository deptRepo;

    @GetMapping
    public String list(@RequestParam(value="role",required=false) String role, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("staff", role != null && !role.isEmpty() ? us.findByRole(role) : us.findAll());
        m.addAttribute("filterRole", role);
        return "staff-list";
    }

    @GetMapping("/new")
    public String newForm(Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("staffMember", new User());
        m.addAttribute("departments", deptRepo.findAll());
        m.addAttribute("schedule", new DoctorSchedule());
        return "staff-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("staffMember") User staff,
                       @RequestParam(required = false) Double consultationFee,
                       @RequestParam(required = false) String startTime,
                       @RequestParam(required = false) String endTime,
                       @RequestParam(required = false) Integer maxPatientsPerDay,
                       @RequestParam(required = false) List<String> availableDays,
                       @RequestParam(required = false) String qualification,
                       RedirectAttributes ra) {
        try {
            boolean isNew = (staff.getId() == null || staff.getId().isEmpty());
            if (isNew) {
                if (staff.getPassword() == null || staff.getPassword().isEmpty()) staff.setPassword("welcome123");
                if ("".equals(staff.getId())) staff.setId(null);
                staff = us.register(staff);
            } else {
                User existing = us.findById(staff.getId()).orElseThrow();
                staff.setPassword(existing.getPassword());
                staff.setCreatedAt(existing.getCreatedAt());
                staff = us.save(staff);
            }

            // ── Auto-create / update DoctorSchedule when role is DOCTOR ──
            if ("DOCTOR".equals(staff.getRole())) {
                Optional<DoctorSchedule> existingSched = hs.scheduleByDoctorId(staff.getId());
                DoctorSchedule sched = existingSched.orElse(new DoctorSchedule());

                sched.setDoctorId(staff.getId());
                sched.setDoctorName(staff.getFullName());
                sched.setSpecialization(staff.getSpecialization() != null ? staff.getSpecialization() : staff.getDepartment());
                sched.setDepartment(staff.getDepartment());
                sched.setQualification(qualification != null ? qualification : sched.getQualification());
                sched.setActive(staff.isActive());

                // Fee
                if (consultationFee != null && consultationFee > 0) {
                    sched.setConsultationFee(consultationFee);
                } else if (sched.getConsultationFee() <= 0) {
                    sched.setConsultationFee(500); // default
                }

                // Timings
                if (startTime != null && !startTime.isEmpty()) {
                    sched.setStartTime(LocalTime.parse(startTime));
                } else if (sched.getStartTime() == null) {
                    sched.setStartTime(LocalTime.of(9, 0));
                }
                if (endTime != null && !endTime.isEmpty()) {
                    sched.setEndTime(LocalTime.parse(endTime));
                } else if (sched.getEndTime() == null) {
                    sched.setEndTime(LocalTime.of(17, 0));
                }

                // Available days
                if (availableDays != null && !availableDays.isEmpty()) {
                    sched.setAvailableDays(availableDays);
                } else if (sched.getAvailableDays() == null || sched.getAvailableDays().isEmpty()) {
                    sched.setAvailableDays(Arrays.asList("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"));
                }

                // Max patients
                if (maxPatientsPerDay != null && maxPatientsPerDay > 0) {
                    sched.setMaxPatientsPerDay(maxPatientsPerDay);
                }

                hs.saveSchedule(sched);
            }

            ra.addFlashAttribute("success", "Staff member saved successfully!");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/staff";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable String id, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        User staffMember = us.findById(id).orElseThrow();
        m.addAttribute("staffMember", staffMember);
        if ("DOCTOR".equals(staffMember.getRole())) {
            hs.scheduleByDoctorId(id).ifPresent(s -> m.addAttribute("schedule", s));
        }
        return "staff-detail";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        User staffMember = us.findById(id).orElseThrow();
        m.addAttribute("staffMember", staffMember);
        m.addAttribute("departments", deptRepo.findAll());
        // Load existing schedule for doctors
        if ("DOCTOR".equals(staffMember.getRole())) {
            m.addAttribute("schedule", hs.scheduleByDoctorId(id).orElse(new DoctorSchedule()));
        } else {
            m.addAttribute("schedule", new DoctorSchedule());
        }
        return "staff-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        us.delete(id);
        ra.addFlashAttribute("success", "Staff member removed.");
        return "redirect:/staff";
    }
}
