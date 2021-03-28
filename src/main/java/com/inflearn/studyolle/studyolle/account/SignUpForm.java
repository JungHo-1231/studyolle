package com.inflearn.studyolle.studyolle.account;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignUpForm {

    @NotEmpty
    @Size(min = 3, max = 20)
    private String nickname;

    @NotNull
    private String email;

    @NotEmpty
    @Size(min = 8, max = 50)
    private String password;
}

