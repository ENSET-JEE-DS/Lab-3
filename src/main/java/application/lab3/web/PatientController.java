package application.lab3.web;

import application.lab3.entities.Patient;
import application.lab3.repositories.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String addPatient(@ModelAttribute Patient patient) {
        System.out.println("Patient to be added:" + patient);
        patientRepository.save(patient);
        System.out.println("Patient added successfully:" + patient);
        return "patientForm";
    }

}
