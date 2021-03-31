package com.inflearn.studyolle.studyolle.account;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SignUpForm {

    @NotEmpty
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 8, max = 50)
    private String password;
}

