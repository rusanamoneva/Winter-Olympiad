package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.athlete.AthleteProfileForm;
import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.dto.athlete.AthleteUpdateRequest;
import com.inf.winter_olympiad.service.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AthleteViewController {

    private final AthleteService athleteService;

    @GetMapping("/athlete/dashboard")
    public String getDashboard() {
        return "athlete/dashboard";
    }

    @GetMapping("/athlete/profile")
    public String getProfile(Model model) {
        AthleteResponse athlete = athleteService.getCurrentAthlete();
        model.addAttribute("athlete", athlete);
        model.addAttribute("athleteForm", toForm(athlete));
        return "athlete/profile";
    }

    @PostMapping("/athlete/profile")
    public String updateProfile(
            @Valid @ModelAttribute("athleteForm") AthleteProfileForm athleteForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("athlete", athleteService.getCurrentAthlete());
            return "athlete/profile";
        }

        athleteService.updateCurrentAthlete(toRequest(athleteForm));
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/athlete/profile";
    }

    @PostMapping("/athlete/profile/deactivate")
    public String deactivateProfile(RedirectAttributes redirectAttributes) {
        athleteService.deleteCurrentAthlete();
        redirectAttributes.addFlashAttribute("successMessage", "Profile has been deactivated.");
        return "redirect:/login";
    }

    private AthleteProfileForm toForm(AthleteResponse athlete) {
        AthleteProfileForm form = new AthleteProfileForm();
        form.setFirstName(athlete.firstName());
        form.setLastName(athlete.lastName());
        form.setCountry(athlete.country());
        form.setGender(athlete.gender());
        form.setBirthDate(athlete.birthDate());
        return form;
    }

    private AthleteUpdateRequest toRequest(AthleteProfileForm form) {
        return new AthleteUpdateRequest(
                form.getFirstName(),
                form.getLastName(),
                form.getCountry(),
                form.getGender(),
                form.getBirthDate()
        );
    }
}
