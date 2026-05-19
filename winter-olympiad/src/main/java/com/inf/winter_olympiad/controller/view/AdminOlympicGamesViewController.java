package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesAdminForm;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesCreateRequest;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesUpdateRequest;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
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
@RequestMapping("/admin/olympics")
@RequiredArgsConstructor
public class AdminOlympicGamesViewController {

    private final OlympicGamesService olympicGamesService;

    @GetMapping
    public String getOlympicsManagement(Model model) {
        model.addAttribute("olympics", olympicGamesService.getAllOlympics());
        return "admin/olympics/list";
    }

    @GetMapping("/new")
    public String getCreateForm(Model model) {
        model.addAttribute("olympicsForm", new OlympicGamesAdminForm());
        model.addAttribute("pageTitle", "Create Olympic Games");
        model.addAttribute("submitLabel", "Create");
        model.addAttribute("formAction", "/admin/olympics");
        return "admin/olympics/form";
    }

    @PostMapping
    public String createOlympics(
            @Valid @ModelAttribute("olympicsForm") OlympicGamesAdminForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, "Create Olympic Games", "Create", "/admin/olympics");
            return "admin/olympics/form";
        }

        try {
            olympicGamesService.createOlympics(toCreateRequest(form));
            redirectAttributes.addFlashAttribute("successMessage", "Olympic games created successfully.");
            return "redirect:/admin/olympics";
        } catch (BusinessRuleViolationException ex) {
            bindingResult.reject("businessRule", ex.getMessage());
            populateFormModel(model, "Create Olympic Games", "Create", "/admin/olympics");
            return "admin/olympics/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String getEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            OlympicGamesResponse olympics = olympicGamesService.getOlympicsById(id);
            OlympicGamesAdminForm form = toForm(olympics);
            model.addAttribute("olympicsForm", form);
            model.addAttribute("pageTitle", "Edit Olympic Games");
            model.addAttribute("submitLabel", "Update");
            model.addAttribute("formAction", "/admin/olympics/" + id);
            return "admin/olympics/form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/olympics";
        }
    }

    @PostMapping("/{id}")
    public String updateOlympics(
            @PathVariable Long id,
            @Valid @ModelAttribute("olympicsForm") OlympicGamesAdminForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, "Edit Olympic Games", "Update", "/admin/olympics/" + id);
            return "admin/olympics/form";
        }

        try {
            olympicGamesService.updateOlympics(id, toUpdateRequest(form));
            redirectAttributes.addFlashAttribute("successMessage", "Olympic games updated successfully.");
            return "redirect:/admin/olympics";
        } catch (BusinessRuleViolationException ex) {
            bindingResult.reject("businessRule", ex.getMessage());
            populateFormModel(model, "Edit Olympic Games", "Update", "/admin/olympics/" + id);
            return "admin/olympics/form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/olympics";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteOlympics(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            olympicGamesService.deleteOlympics(id);
            redirectAttributes.addFlashAttribute("successMessage", "Olympic games deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/olympics";
    }

    private void populateFormModel(Model model, String pageTitle, String submitLabel, String formAction) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("formAction", formAction);
    }

    private OlympicGamesAdminForm toForm(OlympicGamesResponse olympics) {
        OlympicGamesAdminForm form = new OlympicGamesAdminForm();
        form.setName(olympics.name());
        form.setLocation(olympics.location());
        form.setStartDate(olympics.startDate());
        form.setEndDate(olympics.endDate());
        return form;
    }

    private OlympicGamesCreateRequest toCreateRequest(OlympicGamesAdminForm form) {
        return new OlympicGamesCreateRequest(
                form.getName(),
                form.getLocation(),
                form.getStartDate(),
                form.getEndDate()
        );
    }

    private OlympicGamesUpdateRequest toUpdateRequest(OlympicGamesAdminForm form) {
        return new OlympicGamesUpdateRequest(
                form.getName(),
                form.getLocation(),
                form.getStartDate(),
                form.getEndDate()
        );
    }
}

