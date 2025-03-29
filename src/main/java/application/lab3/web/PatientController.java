package application.lab3.web;

import application.lab3.entities.Patient;
import application.lab3.repositories.PatientRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    @GetMapping
    public String home() {
        return "redirect:/index";
    }

    @GetMapping(path = "/index")
    public String patients(
            Model model,
            @RequestParam(name = "p", defaultValue = "0") int page,
            @RequestParam(name = "s", defaultValue = "4") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword
    ) {
        Page<Patient> patients = patientRepository.findByNameContains(keyword, PageRequest.of(page, size));
        model.addAttribute("patientsList", patients.getContent());
        model.addAttribute("pagesNumber", new int[patients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deletePatient(Long id, @RequestParam(name = "p") int page, @RequestParam(required = false) String keyword) {
        patientRepository.deleteById(id);
        if (keyword != null) {
            return "redirect:/index?p=" + page + "&keyword=" + keyword;
        }
        return "redirect:/index?p=" + page;
    }

    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/addPatient")
    public String addPatient(Model model) {
        model.addAttribute("patient", new Patient());
        return "patientForm";
    }

    @PostMapping("/addPatient")
    public String addPatient(@ModelAttribute @Valid Patient patient, BindingResult bindingResult) {
        System.out.println("Adding Patient: " + patient);
        if (bindingResult.hasErrors()) {
            return "patientForm";
        }
        patientRepository.save(patient);
        System.out.println("Patient added successfully:" + patient);
        return "redirect:/index";
    }

    @GetMapping("/update")
    public String updatePatient(Long id, Model model) {
        Patient patient = patientRepository.findById(id).orElse(null);
        if (patient == null) return "redirect:/index";
        model.addAttribute("patient", patient);
        return "patientFormUpdate";
    }

    @PostMapping("/update")
    public String updatePatient(@ModelAttribute @Valid Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "patientFormUpdate";
        }
        patientRepository.save(patient);
        return "redirect:/index";
    }
}
