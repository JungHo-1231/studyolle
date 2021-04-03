package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm") // SignUpForm 따라간다. 변수 명은 바꿔도 상관이 없다.
    public void initBinder(WebDataBinder webDataBinder){
        // 회원 중복 검증 (닉네임, 이메일)
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String singUpForm(Model model){
        model.addAttribute( new SignUpForm());

        return "/account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm,
                               Errors errors){

        if (errors.hasErrors()){
            return "/account/sign-up";
        }

//        이렇게도 가능하다.
//        signUpFormValidator.validate(signUpForm, errors);
//        if (errors.hasErrors()){
//            return "/account/sign-up";
//        }

        accountService.processNewAccount(signUpForm);

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(@RequestParam("token") String token,
                                  @RequestParam("email") String email , Model model){

        Account account = accountRepository.findByEmail(email);

        String view = "/account/checked-email";
        if (account == null){
            model.addAttribute("error", "wrong.emil");
            return view;
        }

        if (!account.getEmailCheckToken().equals(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());

        model.addAttribute("nickname", account.getNickname());
        model.addAttribute("numberOfUser", accountRepository.count());

        return view;
    }
}
