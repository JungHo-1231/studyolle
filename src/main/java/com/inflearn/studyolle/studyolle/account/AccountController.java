package com.inflearn.studyolle.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;

    @InitBinder("signUpForm") // SingupFomr을 따라간다. 변수 명은 바꿔도 상관이 없다.
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


        accountService.save(signUpForm);


        return "redirect:/";
    }
}
