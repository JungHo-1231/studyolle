package com.inflearn.studyolle.studyolle.main;

import com.inflearn.studyolle.studyolle.account.AccountRepository;
import com.inflearn.studyolle.studyolle.account.AccountService;
import com.inflearn.studyolle.studyolle.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;


    String username = "jungho";
    String password = "12345678";
    String email = "jungho@email.com";

    @BeforeEach
    void beforeEach(){

        SignUpForm signUpForm = new SignUpForm();

        signUpForm.setNickname(username);
        signUpForm.setPassword(password);
        signUpForm.setEmail(email);

        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일로 로그인 성공")
    void 이메일로_로그인()throws Exception{
        //given

        //when

        //then
        mockMvc.perform(post("/login")
                .param("username", email)
                .param("password", password)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(username))
        ;
    }

    @Test
    @DisplayName("유저네임 로그인 성공")
    void 유저네임으로이메일()throws Exception{
        //given

        //when

        //then
        mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(username))
        ;
    }

    @Test
    @DisplayName("로그인 실패")
    void 로그인_실패()throws Exception{
        //given

        //when

        //then
        mockMvc.perform(post("/login")
                .param("username", "11111")
                .param("password", password)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated())
        ;
    }

    @Test
    @DisplayName("로그아웃")
    void 로그아웃()throws Exception{
        //given

        //when

        //then
        mockMvc.perform(post("/logout")

                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated())
        ;
    }


}