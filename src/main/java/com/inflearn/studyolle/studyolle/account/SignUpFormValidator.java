package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target;

        // 닉네임, 이메일 중복 검사

        boolean existsByNickname = accountRepository.existsByNickname(((SignUpForm) target).getNickname());
        if (existsByNickname){
            errors.rejectValue("nickname", "invalid.nickname",
                    new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }


        boolean byEmail = accountRepository.existsByEmail(signUpForm.getEmail());
        if (byEmail){
            errors.rejectValue("email", "invalid.email",
                    new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }
    }
}
