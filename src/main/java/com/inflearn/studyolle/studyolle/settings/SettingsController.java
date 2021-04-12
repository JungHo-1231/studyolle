package com.inflearn.studyolle.studyolle.settings;

import com.inflearn.studyolle.studyolle.account.AccountService;
import com.inflearn.studyolle.studyolle.account.CurrentUser;
import com.inflearn.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";
    static final String SETTINGS_PROFILE_PASSWORD_NAME = "/settings/password";
    static final String SETTINGS_PROFILE_PASSWORD_URL = "settings/password";
    static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }


    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
//        model.addAttribute(new Profile(account));

        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, Model model, @Valid Profile profile,
                                Errors errors, RedirectAttributes attributes){

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;

    }

    @GetMapping(SETTINGS_PROFILE_PASSWORD_NAME)
    public String passwordChangeForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());

        return SETTINGS_PROFILE_PASSWORD_URL;
    }

    @PostMapping(SETTINGS_PROFILE_PASSWORD_NAME)
    public String passwordChange(@CurrentUser Account account,
                                 @Valid PasswordForm passwordForm, Errors errors , Model model, RedirectAttributes attributes) throws Exception {

        if (errors.hasErrors()){
            return SETTINGS_PROFILE_PASSWORD_URL;
        }

        accountService.updatePassword(account, passwordForm);
        attributes.addFlashAttribute("message", "패스워드가 수정되었습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String notificationsForm(@CurrentUser Account account, Model model){
//        model.addAttribute(new Notifications(account));

        model.addAttribute(modelMapper.map(account , Notifications.class));
        return "settings/notifications";
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String notificationUpdate(@CurrentUser Account account,Model model,@Valid Notifications notifications, Errors errors,
                                     RedirectAttributes attributes){
        if (errors.hasErrors()){
            return "settings/notifications";
        }
        accountService.updateNotification(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정이 업데이트 되었습니다.");

        return "redirect:" + SETTINGS_NOTIFICATIONS_URL;
    }

}
