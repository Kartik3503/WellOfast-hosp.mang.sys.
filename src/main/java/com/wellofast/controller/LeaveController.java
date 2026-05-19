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
@RequestMapping("/leaves")
public class LeaveController {
    @Autowired private HospitalService hs;
    @Autowired private UserService us;

    @GetMapping
    public String list(Model m, Authentication auth) {
        User user = us.findByUsername(auth.getName()).orElseThrow();
        m.addAttribute("user", user);
        if ("ADMIN".equals(user.getRole())) {
            m.addAttribute("leaves", hs.allLeaves());
        } else {
            m.addAttribute("leaves", hs.leavesByUser(user.getId()));
        }
        m.addAttribute("pendingCount", hs.countPendingLeaves());
        return "leave-list";
    }

    @GetMapping("/new")
    public String newForm(Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("leaveRequest", new LeaveRequest());
        return "leave-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute LeaveRequest lr, Authentication auth, RedirectAttributes ra) {
        User user = us.findByUsername(auth.getName()).orElseThrow();
        lr.setUserId(user.getId());
        lr.setUserName(user.getFullName());
        lr.setDepartment(user.getDepartment());
        lr.setStatus("PENDING");
        hs.saveLeave(lr);
        ra.addFlashAttribute("success", "Leave request submitted!");
        return "redirect:/leaves";
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable String id, Authentication auth, RedirectAttributes ra) {
        User user = us.findByUsername(auth.getName()).orElseThrow();
        hs.leaveById(id).ifPresent(l -> {
            l.setStatus("APPROVED"); l.setApprovedBy(user.getFullName()); hs.saveLeave(l);
        });
        ra.addFlashAttribute("success", "Leave approved.");
        return "redirect:/leaves";
    }

    @GetMapping("/reject/{id}")
    public String reject(@PathVariable String id, RedirectAttributes ra) {
        hs.leaveById(id).ifPresent(l -> { l.setStatus("REJECTED"); hs.saveLeave(l); });
        ra.addFlashAttribute("success", "Leave rejected.");
        return "redirect:/leaves";
    }
}
