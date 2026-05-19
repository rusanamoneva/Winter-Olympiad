package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.service.CompetitionService;
import com.inf.winter_olympiad.service.MedalService;
import com.inf.winter_olympiad.service.OlympicGamesService;
import com.inf.winter_olympiad.service.StatisticsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class StatisticsViewController {

    private final StatisticsService statisticsService;
    private final MedalService medalService;
    private final CompetitionService competitionService;
    private final OlympicGamesService olympicGamesService;

    @GetMapping("/statistics")
    public String getStatistics(
            @RequestParam(required = false) Long olympiadId,
            @RequestParam(required = false) Long competitionId,
            Model model) {
        List<OlympicGamesResponse> olympics = olympicGamesService.getAllOlympics();
        if (olympics.isEmpty()) {
            model.addAttribute("statsMessage", "No Olympic Games available yet.");
            model.addAttribute("olympics", olympics);
            model.addAttribute("competitions", List.of());
            return "statistics/index";
        }

        Long selectedOlympiadId = olympiadId;
        if (selectedOlympiadId == null && competitionId != null) {
            CompetitionDetailsResponse competition = competitionService.getCompetitionById(competitionId);
            selectedOlympiadId = competition.olympicGamesId();
        }
        if (selectedOlympiadId == null) {
            selectedOlympiadId = olympics.getFirst().id();
        }

        model.addAttribute("olympics", olympics);
        model.addAttribute("selectedOlympiadId", selectedOlympiadId);
        model.addAttribute("medalsByCountry", statisticsService.getMedalsByCountry(selectedOlympiadId));
        model.addAttribute("averageAge", statisticsService.calculateAverageAge(selectedOlympiadId));
        model.addAttribute("competitions", competitionService.getCompetitionsByOlympics(selectedOlympiadId));

        try {
            model.addAttribute("youngestMedalist", statisticsService.getYoungestMedalist(selectedOlympiadId));
            model.addAttribute("oldestMedalist", statisticsService.getOldestMedalist(selectedOlympiadId));
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("statsMessage", ex.getMessage());
        }

        if (competitionId != null) {
            model.addAttribute("selectedCompetitionId", competitionId);
            model.addAttribute("competitionSummary", statisticsService.getCompetitionSummary(competitionId));
            model.addAttribute("competitionMedals", medalService.getCompetitionMedals(competitionId));
        }

        return "statistics/index";
    }

    @PostMapping("/admin/medals/{competitionId}/assign")
    public String assignMedals(@PathVariable Long competitionId, RedirectAttributes redirectAttributes) {
        try {
            medalService.assignMedalsForCompetition(competitionId);
            redirectAttributes.addFlashAttribute("successMessage", "Medals assigned successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        Long olympiadId = competitionService.getCompetitionById(competitionId).olympicGamesId();
        return "redirect:/statistics?olympiadId=" + olympiadId + "&competitionId=" + competitionId;
    }
}

