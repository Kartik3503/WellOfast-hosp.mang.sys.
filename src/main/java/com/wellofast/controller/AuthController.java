package com.wellofast.controller;

import com.wellofast.model.User;
import com.wellofast.service.UserService;
import com.wellofast.repository.DepartmentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @Autowired private UserService userService;
    @Autowired private DepartmentRepository deptRepo;

    @GetMapping("/") public String home() { return "redirect:/login"; }

    @GetMapping("/login") public String login() { return "login"; }

    @GetMapping("/register")
    public String registerForm(Model m) {
        m.addAttribute("user", new User());
        m.addAttribute("departments", deptRepo.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
                            Model m, RedirectAttributes ra) {
        if (result.hasErrors()) { m.addAttribute("departments", deptRepo.findAll()); return "register"; }
        try {
            if (user.getRole() == null || user.getRole().isEmpty()) user.setRole("EMPLOYEE");
            userService.register(user);
            ra.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            m.addAttribute("error", e.getMessage());
            m.addAttribute("departments", deptRepo.findAll());
            return "register";
        }
    }
}
