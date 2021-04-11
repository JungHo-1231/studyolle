package com.inflearn.studyolle.studyolle.settings;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        String newPassword = passwordForm.getNewPassword();
        String newPasswordConfirm = passwordForm.getNewPasswordConfirm();

        if (!newPassword.equals(newPasswordConfirm)){
            errors.rejectValue("newPassword", "password and Confirm password not same"
            ,  "패스워드와 새 패스워드 값이 일치하지 않습니다."
            );
        }


    }
}
