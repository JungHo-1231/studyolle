package com.inflearn.studyolle.studyolle.controller;

import com.inflearn.studyolle.studyolle.account.AccountRepository;
import com.inflearn.studyolle.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @DisplayName("회원 가입 화면 보이는지 테스트 ")
    @Test
    void sign_up() throws Exception {
        mockMvc.perform(get("/sign-up")

        )
        .andExpect(view().name("/account/sign-up"))
        .andExpect(model().attributeExists("signUpForm"))
        .andExpect(unauthenticated())
        .andDo(print())
        ;
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname","mooon")
                .param("email","email...")
                .param("password","12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/account/sign-up"));
    }


    @Test
    @DisplayName("회원 가입 처리 - 올바른 입력값")
    void signUpSubmit() throws Exception {
        String email = "jung@email.com";
        String password = "12345678";
        mockMvc.perform(post("/sign-up")
                .param("nickname", "jung")
                .param("email", email)
                .param("password", password)
                .with(csrf())
        )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("jung"))

        ;


        Account account = accountRepository.findByEmail(email);
        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        assertNotEquals(account.getPassword(), password);

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("회원 가입 인증 메일 확인 - 입력값 오류")
    void verify_with_wrong_input() throws Exception {

        mockMvc.perform(get("/check-email-token")
                            .param("token", "대충 이상한 값")
                            .param("email", "대충 이상한 이메일")
                            .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("error"))
        .andExpect(view().name("/account/checked-email"))
        .andExpect(unauthenticated())

       ;
    }

    @Test
    @DisplayName("회원 가입 인증 메일 확인 - 올바른 입력값")
    void verity_with_right_input() throws Exception {
        String email = "jung@email.com";
        String password = "12345678";

        Account account = Account.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .nickname("jung")
                .build();


        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail())
                .with(csrf())
        )
                .andDo(print())
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(view().name("/account/checked-email"))
                .andExpect(authenticated().withUsername("jung"))
                ;
    }

}