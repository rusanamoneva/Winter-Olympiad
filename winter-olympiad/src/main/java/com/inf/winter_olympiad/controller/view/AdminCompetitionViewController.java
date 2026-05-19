package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.competition.AdminBiathlonCompetitionForm;
import com.inf.winter_olympiad.dto.competition.AdminSlalomCompetitionForm;
import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionUpdateRequest;
import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionStatusForm;
import com.inf.winter_olympiad.dto.competition.CompetitionStatusUpdateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionUpdateRequest;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.service.CompetitionService;
import com.inf.winter_olympiad.service.OlympicGamesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/competitions")
@RequiredArgsConstructor
public class AdminCompetitionViewController {

    private final CompetitionService competitionService;
    private final OlympicGamesService olympicGamesService;

    @GetMapping
    public String getCompetitionsManagement(Model model) {
        model.addAttribute("competitions", competitionService.getAllCompetitions());
        model.addAttribute("competitionStatuses", CompetitionStatus.values());
        return "admin/competitions/list";
    }

    @GetMapping("/new/slalom")
    public String getCreateSlalomForm(Model model) {
        model.addAttribute("competitionForm", new AdminSlalomCompetitionForm());
        populateSlalomFormModel(model, "Create Slalom Competition", "Create", "/admin/competitions/slalom", false);
        return "admin/competitions/slalom-form";
    }

    @PostMapping("/slalom")
    public String createSlalomCompetition(
            @Valid @ModelAttribute("competitionForm") AdminSlalomCompetitionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateSlalomFormModel(model, "Create Slalom Competition", "Create", "/admin/competitions/slalom", false);
            return "admin/competitions/slalom-form";
        }

        try {
            competitionService.createSlalomCompetition(toCreateSlalomRequest(form));
            redirectAttributes.addFlashAttribute("successMessage", "Slalom competition created successfully.");
            return "redirect:/admin/competitions";
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            bindingResult.reject("businessRule", ex.getMessage());
            populateSlalomFormModel(model, "Create Slalom Competition", "Create", "/admin/competitions/slalom", false);
            return "admin/competitions/slalom-form";
        }
    }

    @GetMapping("/new/biathlon")
    public String getCreateBiathlonForm(Model model) {
        model.addAttribute("competitionForm", new AdminBiathlonCompetitionForm());
        populateBiathlonFormModel(model, "Create Biathlon Competition", "Create", "/admin/competitions/biathlon", false);
        return "admin/competitions/biathlon-form";
    }

    @PostMapping("/biathlon")
    public String createBiathlonCompetition(
            @Valid @ModelAttribute("competitionForm") AdminBiathlonCompetitionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateBiathlonFormModel(model, "Create Biathlon Competition", "Create", "/admin/competitions/biathlon", false);
            return "admin/competitions/biathlon-form";
        }

        try {
            competitionService.createBiathlonCompetition(toCreateBiathlonRequest(form));
            redirectAttributes.addFlashAttribute("successMessage", "Biathlon competition created successfully.");
            return "redirect:/admin/competitions";
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            bindingResult.reject("businessRule", ex.getMessage());
            populateBiathlonFormModel(model, "Create Biathlon Competition", "Create", "/admin/competitions/biathlon", false);
            return "admin/competitions/biathlon-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String getEditCompetitionForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CompetitionDetailsResponse competition = competitionService.getCompetitionById(id);
            if ("SlalomCompetition".equals(competition.competitionType())) {
                model.addAttribute("competitionForm", toSlalomForm(competition));
                populateSlalomFormModel(model, "Edit Slalom Competition", "Update", "/admin/competitions/" + id + "/slalom", true);
                return "admin/competitions/slalom-form";
            }
            if ("BiathlonCompetition".equals(competition.competitionType())) {
                model.addAttribute("competitionForm", toBiathlonForm(competition));
                populateBiathlonFormModel(model, "Edit Biathlon Competition", "Update", "/admin/competitions/" + id + "/biathlon", true);
                return "admin/competitions/biathlon-form";
            }

            redirectAttributes.addFlashAttribute("errorMessage", "Unsupported competition type.");
            return "redirect:/admin/competitions";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/competitions";
        }
    }

    @PostMapping("/{id}/slalom")
    public String updateSlalomCompetition(
            @PathVariable Long id,
            @Valid @ModelAttribute("competitionForm") AdminSlalomCompetitionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateSlalomFormModel(model, "Edit Slalom Competition", "Update", "/admin/competitions/" + id + "/slalom", true);
            return "admin/competitions/slalom-form";
        }

        try {
            competitionService.updateSlalomCompetition(id, toUpdateSlalomRequest(form));
            redirectAttributes.addFlashAttribute("successMessage", "Slalom competition updated successfully.");
            return "redirect:/admin/competitions";
        } catch (BusinessRuleViolationException ex) {
            bindingResult.reject("businessRule", ex.getMessage());
            populateSlalomFormModel(model, "Edit Slalom Competition", "Update", "/admin/competitions/" + id + "/slalom", true);
            return "admin/competitions/slalom-form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/competitions";
        }
    }

    @PostMapping("/{id}/biathlon")
    public String updateBiathlonCompetition(
            @PathVariable Long id,
            @Valid @ModelAttribute("competitionForm") AdminBiathlonCompetitionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateBiathlonFormModel(model, "Edit Biathlon Competition", "Update", "/admin/competitions/" + id + "/biathlon", true);
            return "admin/competitions/biathlon-form";
        }

        try {
            competitionService.updateBiathlonCompetition(id, toUpdateBiathlonRequest(form));
            redirectAttributes.addFlashAttribute("successMessage", "Biathlon competition updated successfully.");
            return "redirect:/admin/competitions";
        } catch (BusinessRuleViolationException ex) {
            bindingResult.reject("businessRule", ex.getMessage());
            populateBiathlonFormModel(model, "Edit Biathlon Competition", "Update", "/admin/competitions/" + id + "/biathlon", true);
            return "admin/competitions/biathlon-form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/competitions";
        }
    }

    @PostMapping("/{id}/status")
    public String changeCompetitionStatus(
            @PathVariable Long id,
            @Valid @ModelAttribute CompetitionStatusForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Status is required.");
            return "redirect:/admin/competitions";
        }

        try {
            competitionService.changeCompetitionStatus(id, new CompetitionStatusUpdateRequest(form.getStatus()));
            redirectAttributes.addFlashAttribute("successMessage", "Competition status updated successfully.");
        } catch (BusinessRuleViolationException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/competitions";
    }

    @PostMapping("/{id}/delete")
    public String deleteCompetition(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            competitionService.deleteCompetition(id);
            redirectAttributes.addFlashAttribute("successMessage", "Competition deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/competitions";
    }

    private void populateSlalomFormModel(Model model, String pageTitle, String submitLabel, String formAction, boolean editMode) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("formAction", formAction);
        model.addAttribute("editMode", editMode);
        model.addAttribute("olympicsOptions", olympicGamesService.getAllOlympics());
    }

    private void populateBiathlonFormModel(Model model, String pageTitle, String submitLabel, String formAction, boolean editMode) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("formAction", formAction);
        model.addAttribute("editMode", editMode);
        model.addAttribute("olympicsOptions", olympicGamesService.getAllOlympics());
    }

    private SlalomCompetitionCreateRequest toCreateSlalomRequest(AdminSlalomCompetitionForm form) {
        return new SlalomCompetitionCreateRequest(
                form.getName(),
                form.getGenderCategory(),
                form.getMinimumAge(),
                form.getCompetitionDate(),
                form.getOlympicGamesId(),
                form.getMaxSecondRunParticipants()
        );
    }

    private SlalomCompetitionUpdateRequest toUpdateSlalomRequest(AdminSlalomCompetitionForm form) {
        return new SlalomCompetitionUpdateRequest(
                form.getName(),
                form.getGenderCategory(),
                form.getMinimumAge(),
                form.getCompetitionDate(),
                form.getMaxSecondRunParticipants()
        );
    }

    private BiathlonCompetitionCreateRequest toCreateBiathlonRequest(AdminBiathlonCompetitionForm form) {
        return new BiathlonCompetitionCreateRequest(
                form.getName(),
                form.getGenderCategory(),
                form.getMinimumAge(),
                form.getCompetitionDate(),
                form.getOlympicGamesId(),
                form.getPenaltyPerMissSeconds(),
                form.getNumberOfShootings(),
                form.getNumberOfLaps()
        );
    }

    private BiathlonCompetitionUpdateRequest toUpdateBiathlonRequest(AdminBiathlonCompetitionForm form) {
        return new BiathlonCompetitionUpdateRequest(
                form.getName(),
                form.getGenderCategory(),
                form.getMinimumAge(),
                form.getCompetitionDate(),
                form.getPenaltyPerMissSeconds(),
                form.getNumberOfShootings(),
                form.getNumberOfLaps()
        );
    }

    private AdminSlalomCompetitionForm toSlalomForm(CompetitionDetailsResponse competition) {
        AdminSlalomCompetitionForm form = new AdminSlalomCompetitionForm();
        form.setName(competition.name());
        form.setGenderCategory(competition.genderCategory());
        form.setMinimumAge(competition.minimumAge());
        form.setCompetitionDate(competition.competitionDate());
        form.setOlympicGamesId(competition.olympicGamesId());
        form.setMaxSecondRunParticipants(competition.maxSecondRunParticipants());
        return form;
    }

    private AdminBiathlonCompetitionForm toBiathlonForm(CompetitionDetailsResponse competition) {
        AdminBiathlonCompetitionForm form = new AdminBiathlonCompetitionForm();
        form.setName(competition.name());
        form.setGenderCategory(competition.genderCategory());
        form.setMinimumAge(competition.minimumAge());
        form.setCompetitionDate(competition.competitionDate());
        form.setOlympicGamesId(competition.olympicGamesId());
        form.setPenaltyPerMissSeconds(competition.penaltyPerMissSeconds());
        form.setNumberOfShootings(competition.numberOfShootings());
        form.setNumberOfLaps(competition.numberOfLaps());
        return form;
    }
}



