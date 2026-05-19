package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomDnfRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomQualificationRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomRun1EntryRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomRun2EntryRequest;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.service.CompetitionService;
import com.inf.winter_olympiad.service.RegistrationService;
import com.inf.winter_olympiad.service.SlalomService;
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
@RequestMapping("/admin/slalom")
@RequiredArgsConstructor
public class AdminSlalomViewController {

    private final SlalomService slalomService;
    private final CompetitionService competitionService;
    private final RegistrationService registrationService;

    @GetMapping("/{competitionId}")
    public String getSlalomResultsPage(@PathVariable Long competitionId, Model model, RedirectAttributes redirectAttributes) {
        try {
            CompetitionDetailsResponse competition = competitionService.getCompetitionById(competitionId);
            if (!"SlalomCompetition".equals(competition.competitionType())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected competition is not slalom.");
                return "redirect:/admin/competitions";
            }

            model.addAttribute("competition", competition);
            model.addAttribute("registrations", registrationService.getRegistrationsByCompetition(competitionId));
            model.addAttribute("qualified", slalomService.getQualifiedForRun2(competitionId));
            model.addAttribute("run2StartOrder", slalomService.getRun2StartOrder(competitionId));
            model.addAttribute("ranking", slalomService.getFinalRanking(competitionId));
            return "admin/slalom/results";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/competitions";
        }
    }

    @PostMapping("/{competitionId}/run1")
    public String enterRun1(
            @PathVariable Long competitionId,
            @RequestParam Long registrationId,
            @RequestParam BigDecimal run1Time,
            RedirectAttributes redirectAttributes) {
        try {
            slalomService.enterRun1Time(competitionId, new SlalomRun1EntryRequest(registrationId, run1Time));
            redirectAttributes.addFlashAttribute("successMessage", "Run 1 time entered successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/slalom/" + competitionId;
    }

    @PostMapping("/{competitionId}/run1/dnf")
    public String markRun1Dnf(
            @PathVariable Long competitionId,
            @RequestParam Long registrationId,
            RedirectAttributes redirectAttributes) {
        try {
            slalomService.markRun1Dnf(competitionId, new SlalomDnfRequest(registrationId));
            redirectAttributes.addFlashAttribute("successMessage", "Run 1 DNF marked successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/slalom/" + competitionId;
    }

    @PostMapping("/{competitionId}/run2/qualify")
    public String qualifyRun2(
            @PathVariable Long competitionId,
            @RequestParam Integer topN,
            RedirectAttributes redirectAttributes) {
        try {
            slalomService.determineQualifiedForRun2(competitionId, new SlalomQualificationRequest(topN));
            redirectAttributes.addFlashAttribute("successMessage", "Run 2 qualification updated successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/slalom/" + competitionId;
    }

    @PostMapping("/{competitionId}/run2")
    public String enterRun2(
            @PathVariable Long competitionId,
            @RequestParam Long registrationId,
            @RequestParam BigDecimal run2Time,
            RedirectAttributes redirectAttributes) {
        try {
            slalomService.enterRun2Time(competitionId, new SlalomRun2EntryRequest(registrationId, run2Time));
            redirectAttributes.addFlashAttribute("successMessage", "Run 2 time entered successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/slalom/" + competitionId;
    }

    @PostMapping("/{competitionId}/run2/dnf")
    public String markRun2Dnf(
            @PathVariable Long competitionId,
            @RequestParam Long registrationId,
            RedirectAttributes redirectAttributes) {
        try {
            slalomService.markRun2Dnf(competitionId, new SlalomDnfRequest(registrationId));
            redirectAttributes.addFlashAttribute("successMessage", "Run 2 DNF marked successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/slalom/" + competitionId;
    }

    @PostMapping("/{competitionId}/ranking/calculate")
    public String calculateRanking(@PathVariable Long competitionId, RedirectAttributes redirectAttributes) {
        try {
            slalomService.calculateFinalRanking(competitionId);
            redirectAttributes.addFlashAttribute("successMessage", "Slalom final ranking calculated successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/slalom/" + competitionId;
    }
}

