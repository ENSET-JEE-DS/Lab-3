package application.lab3.web;

import application.lab3.entities.Patient;
import application.lab3.repositories.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    @GetMapping(path = "/index")
    public String patients(Model model) {
        List<Patient> patients = patientRepository.findAll();
        model.addAttribute("patientsList", patients);
        return "patients";
    }
}
