package com.inflearn.studyolle.studyolle.settings.validator;

import com.inflearn.studyolle.studyolle.account.AccountRepository;
import com.inflearn.studyolle.studyolle.domain.Account;
import com.inflearn.studyolle.studyolle.settings.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.annotation.Annotation;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(NicknameForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;

        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());

        if (byNickname != null) {
            errors.rejectValue("nickname", "duplicated nickname", "이미 등록된 닉네임입니다.");
        };
    }
}
