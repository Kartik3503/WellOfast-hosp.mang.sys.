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
@RequestMapping("/departments")
public class DepartmentController {
    @Autowired private HospitalService hs;
    @Autowired private UserService us;

    @GetMapping
    public String list(Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("departments", hs.allDepts());
        return "department-list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Department dept, RedirectAttributes ra) {
        hs.saveDept(dept);
        ra.addFlashAttribute("success", "Department saved!");
        return "redirect:/departments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        hs.deleteDept(id);
        ra.addFlashAttribute("success", "Department deleted.");
        return "redirect:/departments";
    }
}
