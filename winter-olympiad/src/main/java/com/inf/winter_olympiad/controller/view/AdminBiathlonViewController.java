package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.biathlon.BiathlonDnfRequest;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultEntryRequest;
import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.service.BiathlonService;
import com.inf.winter_olympiad.service.CompetitionService;
import com.inf.winter_olympiad.service.RegistrationService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/biathlon")
@RequiredArgsConstructor
public class AdminBiathlonViewController {

    private final BiathlonService biathlonService;
    private final CompetitionService competitionService;
    private final RegistrationService registrationService;

    @GetMapping("/{competitionId}")
    public String getBiathlonResultsPage(@PathVariable Long competitionId, Model model, RedirectAttributes redirectAttributes) {
        try {
            CompetitionDetailsResponse competition = competitionService.getCompetitionById(competitionId);
            if (!"BiathlonCompetition".equals(competition.competitionType())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected competition is not biathlon.");
                return "redirect:/admin/competitions";
            }

            model.addAttribute("competition", competition);
            model.addAttribute("registrations", registrationService.getRegistrationsByCompetition(competitionId));
            model.addAttribute("ranking", biathlonService.getFinalRanking(competitionId));
            return "admin/biathlon/results";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/competitions";
        }
    }

    @PostMapping("/{competitionId}/results")
    public String enterResult(
            @PathVariable Long competitionId,
            @RequestParam Long registrationId,
            @RequestParam BigDecimal skiTime,
            @RequestParam Integer shootingMisses,
            RedirectAttributes redirectAttributes) {
        try {
            biathlonService.enterBiathlonResult(
                    competitionId,
                    new BiathlonResultEntryRequest(registrationId, skiTime, shootingMisses));
            redirectAttributes.addFlashAttribute("successMessage", "Biathlon result entered successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/biathlon/" + competitionId;
    }

    @PostMapping("/{competitionId}/dnf")
    public String markDnf(
            @PathVariable Long competitionId,
            @RequestParam Long registrationId,
            RedirectAttributes redirectAttributes) {
        try {
            biathlonService.markDnf(competitionId, new BiathlonDnfRequest(registrationId));
            redirectAttributes.addFlashAttribute("successMessage", "Participant marked as DNF.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/biathlon/" + competitionId;
    }

    @PostMapping("/{competitionId}/ranking/calculate")
    public String calculateRanking(@PathVariable Long competitionId, RedirectAttributes redirectAttributes) {
        try {
            biathlonService.calculateFinalRanking(competitionId);
            redirectAttributes.addFlashAttribute("successMessage", "Biathlon ranking calculated successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/biathlon/" + competitionId;
    }
}

