package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.service.OlympicGamesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class OlympicGamesViewController {

    private final OlympicGamesService olympicGamesService;

    @GetMapping("/olympics")
    public String getOlympicsList(Model model) {
        model.addAttribute("olympics", olympicGamesService.getAllOlympics());
        return "olympics/list";
    }

    @GetMapping("/olympics/{id}")
    public String getOlympicsDetails(@PathVariable Long id, Model model) {
        OlympicGamesResponse olympics = olympicGamesService.getOlympicsById(id);
        model.addAttribute("olympics", olympics);
        return "olympics/details";
    }
}

