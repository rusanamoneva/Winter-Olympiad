package com.inf.winter_olympiad.controller.view;

import com.inf.winter_olympiad.dto.athlete.AthleteProfileForm;
import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.service.AthleteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AthleteViewControllerTest {

    @Mock
    private AthleteService athleteService;

    @InjectMocks
    private AthleteViewController athleteViewController;

    @Test
    void getProfileShouldPopulateModelAndReturnView() {
        AthleteResponse athlete = athleteResponse();
        ExtendedModelMap model = new ExtendedModelMap();
        when(athleteService.getCurrentAthlete()).thenReturn(athlete);

        String view = athleteViewController.getProfile(model);

        assertEquals("athlete/profile", view);
        assertEquals(athlete, model.getAttribute("athlete"));
        assertTrue(model.containsAttribute("athleteForm"));
    }

    @Test
    void updateProfileShouldRedirectWhenValid() {
        AthleteProfileForm form = validForm();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "athleteForm");
        ExtendedModelMap model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = athleteViewController.updateProfile(form, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/athlete/profile", view);
        verify(athleteService).updateCurrentAthlete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deactivateProfileShouldCallServiceAndRedirectToLogin() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = athleteViewController.deactivateProfile(redirectAttributes);

        assertEquals("redirect:/login", view);
        verify(athleteService).deleteCurrentAthlete();
    }

    private AthleteResponse athleteResponse() {
        return new AthleteResponse(
                1L,
                "Ana",
                "Ivanova",
                "BG",
                Gender.FEMALE,
                LocalDate.of(2002, 1, 1),
                11L,
                "ana"
        );
    }

    private AthleteProfileForm validForm() {
        AthleteProfileForm form = new AthleteProfileForm();
        form.setFirstName("Ana");
        form.setLastName("Ivanova");
        form.setCountry("BG");
        form.setGender(Gender.FEMALE);
        form.setBirthDate(LocalDate.of(2002, 1, 1));
        return form;
    }
}

