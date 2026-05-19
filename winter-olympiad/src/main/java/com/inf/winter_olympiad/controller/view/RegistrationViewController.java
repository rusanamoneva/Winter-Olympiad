package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationRequest;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegistrationViewController {

    private final RegistrationService registrationService;

    @GetMapping("/athlete/registrations")
    public String getMyRegistrations(Model model) {
        model.addAttribute("registrations", registrationService.getCurrentAthleteRegistrations());
        return "athlete/registrations";
    }

    @PostMapping("/athlete/registrations/{competitionId}")
    public String registerForCompetition(@PathVariable Long competitionId, RedirectAttributes redirectAttributes) {
        try {
            registrationService.registerCurrentAthleteForCompetition(competitionId, new CompetitionRegistrationRequest());
            redirectAttributes.addFlashAttribute("successMessage", "Successfully registered for competition.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/competitions/" + competitionId;
    }

    @PostMapping("/athlete/registrations/{competitionId}/cancel")
    public String cancelRegistration(@PathVariable Long competitionId, RedirectAttributes redirectAttributes) {
        try {
            registrationService.cancelRegistration(competitionId);
            redirectAttributes.addFlashAttribute("successMessage", "Registration cancelled successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/competitions/" + competitionId;
    }
}

