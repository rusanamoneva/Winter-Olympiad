package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.service.OlympicGamesService;
import com.inf.winter_olympiad.service.BiathlonService;
import com.inf.winter_olympiad.service.SlalomService;
import com.inf.winter_olympiad.service.RegistrationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.inf.winter_olympiad.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CompetitionViewController {

    private final CompetitionService competitionService;
    private final RegistrationService registrationService;
    private final BiathlonService biathlonService;
    private final SlalomService slalomService;
    private final OlympicGamesService olympicGamesService;

    @GetMapping("/competitions")
    public String getCompetitions(
            @RequestParam(name = "type", defaultValue = "all") String type,
            Model model) {
        String normalizedType = type == null ? "all" : type.toLowerCase();

        List<CompetitionResponse> competitions = switch (normalizedType) {
            case "slalom" -> competitionService.getSlalomCompetitions();
            case "biathlon" -> competitionService.getBiathlonCompetitions();
            default -> {
                normalizedType = "all";
                yield competitionService.getAllCompetitions();
            }
        };

        boolean isAthlete = isAuthenticatedAthlete();
        Set<Long> registeredCompetitionIds = Set.of();
        if (isAthlete) {
            registeredCompetitionIds = registrationService.getCurrentAthleteRegistrations().stream()
                    .map(CompetitionRegistrationResponse::competitionId)
                    .collect(java.util.stream.Collectors.toSet());
        }

        Map<Long, String> olympicsById = olympicGamesService.getAllOlympics().stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse::id,
                        com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse::name,
                        (existing, replacement) -> existing));

        model.addAttribute("selectedType", normalizedType);
        model.addAttribute("isAthlete", isAthlete);
        model.addAttribute("registeredCompetitionIds", registeredCompetitionIds);
        model.addAttribute("olympicsById", olympicsById);
        model.addAttribute("competitions", competitions);
        return "competitions/list";
    }

    @GetMapping("/competitions/{id}")
    public String getCompetitionDetails(@PathVariable Long id, Model model) {
        CompetitionDetailsResponse competition = competitionService.getCompetitionById(id);

        boolean isAthlete = isAuthenticatedAthlete();
        boolean isRegistered = false;
        if (isAthlete) {
            isRegistered = registrationService.isCurrentAthleteRegisteredForCompetition(id);
        }

        model.addAttribute("competition", competition);
        model.addAttribute("isAthlete", isAthlete);
        model.addAttribute("isRegistered", isRegistered);
        return "competitions/details";
    }

    @GetMapping("/competitions/{id}/biathlon-ranking")
    public String getBiathlonRanking(@PathVariable Long id, Model model) {
        CompetitionDetailsResponse competition = competitionService.getCompetitionById(id);
        if (!"BiathlonCompetition".equals(competition.competitionType())) {
            return "redirect:/competitions/" + id;
        }

        model.addAttribute("competition", competition);
        model.addAttribute("ranking", biathlonService.getFinalRanking(id));
        return "competitions/biathlon-ranking";
    }

    @GetMapping("/competitions/{id}/slalom-ranking")
    public String getSlalomRanking(@PathVariable Long id, Model model) {
        CompetitionDetailsResponse competition = competitionService.getCompetitionById(id);
        if (!"SlalomCompetition".equals(competition.competitionType())) {
            return "redirect:/competitions/" + id;
        }

        model.addAttribute("competition", competition);
        model.addAttribute("ranking", slalomService.getFinalRanking(id));
        return "competitions/slalom-ranking";
    }

    private boolean isAuthenticatedAthlete() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ATHLETE".equals(authority.getAuthority()));
    }
}

