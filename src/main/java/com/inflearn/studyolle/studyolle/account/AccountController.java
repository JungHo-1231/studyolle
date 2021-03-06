package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm") // SignUpForm 따라간다. 변수 명은 바꿔도 상관이 없다.
    public void initBinder(WebDataBinder webDataBinder) {
        // 회원 중복 검증 (닉네임, 이메일)
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String singUpForm(Model model) {
        model.addAttribute(new SignUpForm());

        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm,
                               Errors errors) {

        if (errors.hasErrors()) {
            return "/account/sign-up";
        }

//        이렇게도 가능하다.
//        signUpFormValidator.validate(signUpForm, errors);
//        if (errors.hasErrors()){
//            return "/account/sign-up";
//        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(@RequestParam("token") String token,
                                  @RequestParam("email") String email, Model model) {

        Account account = accountRepository.findByEmail(email);

        String view = "account/checked-email";
        if (account == null) {
            model.addAttribute("error", "wrong.emil");
            return view;
        }

//        if (!account.getEmailCheckToken().equals(token)){
        if (!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }

        accountService.completeSignup(account);

        model.addAttribute("nickname", account.getNickname());
        model.addAttribute("numberOfUser", accountRepository.count());

        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model){
        model.addAttribute("email", account.getEmail());

        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model){
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());

            return "account/check-email";
        }

        accountService.sendSighUpConfirmEmail(account);

        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname,
                              Model model, @CurrentUser Account
                              account){

        Account byNickname = accountRepository.findByNickname(nickname);

        if (byNickname == null) {
            throw new IllegalIdentifierException(nickname + " 에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute(byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));

        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginForm(){

        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model,
                                     RedirectAttributes attributes){

        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }

        if (!account.canSendConfirmEmail()){
            model.addAttribute("error", "이메일 로그인은 1시간 뒤에 사용할 수 있습니다.");
            return "account/email-login";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "이메일 인증 메일을 발송했습니다.");
        return "redirect:/email-login";
    }


    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model){
        Account account = accountRepository.findByEmail(email);

        String view = "account/logged-in-by-email";

        if (account == null || !account.isValidToken(token)){
            model.addAttribute("error", "로그인 할 수 없습니다.");
            return  view;
        }

        accountService.login(account);
        return view;
    }



}
















