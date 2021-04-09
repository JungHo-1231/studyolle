package com.inflearn.studyolle.studyolle.main;


import com.inflearn.studyolle.studyolle.account.AccountService;
import com.inflearn.studyolle.studyolle.account.CurrentUser;
import com.inflearn.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private AccountService accountService;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if (account != null) {
            model.addAttribute(account);
        }

        return "/index";
    }

    @GetMapping("/login")
    public String login(){

        return "login";
    }
}
