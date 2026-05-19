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

@Controller
@RequestMapping("/certificates")
public class CertificateController {
    @Autowired private HospitalService hs;
    @Autowired private UserService us;
    @Autowired private PdfGeneratorService pdf;

    @GetMapping
    public String list(@RequestParam(value="search",required=false) String q, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("certificates", q != null && !q.isEmpty() ? hs.searchCerts(q) : hs.allCerts());
        m.addAttribute("search", q);
        return "certificate-list";
    }

    @GetMapping("/new")
    public String newForm(Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("certificate", new BirthCertificate());
        return "certificate-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BirthCertificate cert, Authentication auth, RedirectAttributes ra) {
        User user = us.findByUsername(auth.getName()).orElseThrow();
        BirthCertificate saved = hs.saveCert(cert, user);
        ra.addFlashAttribute("success", "Birth certificate " + saved.getCertificateNumber() + " created!");
        return "redirect:/certificates/view/" + saved.getId();
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable String id, Model m, Authentication auth) {
        m.addAttribute("user", us.findByUsername(auth.getName()).orElseThrow());
        m.addAttribute("certificate", hs.certById(id).orElseThrow());
        return "certificate-view";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        BirthCertificate cert = hs.certById(id).orElseThrow();
        try {
            byte[] bytes = pdf.generate(cert);
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_PDF);
            h.setContentDispositionFormData("attachment", "BirthCert_" + cert.getCertificateNumber() + ".pdf");
            return new ResponseEntity<>(bytes, h, HttpStatus.OK);
        } catch (Exception e) { return ResponseEntity.status(500).build(); }
    }
}
