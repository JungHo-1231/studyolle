package com.inflearn.studyolle.studyolle.settings;

import com.inflearn.studyolle.studyolle.WithAccount;
import com.inflearn.studyolle.studyolle.account.AccountRepository;
import com.inflearn.studyolle.studyolle.account.AccountService;
import com.inflearn.studyolle.studyolle.account.SignUpForm;
import com.inflearn.studyolle.studyolle.domain.Account;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;



//    @BeforeEach
//    void beforeEach(){
//
//        String username = "jungho";
//        String password = "12345678";
//        String email = "jungho@email.com";
//        SignUpForm signUpForm = new SignUpForm();
//
//        signUpForm.setNickname(username);
//        signUpForm.setPassword(password);
//        signUpForm.setEmail(email);
//
//        accountService.processNewAccount(signUpForm);
//    }

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("jungho")
    @DisplayName("프로필 수정하기 - 입력값 정상")
//    @WithUserDetails(value = "jungho", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateProfile()throws Exception{

        String bio = "짧은 소개를 작성해주세요";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                            .param("bio", bio)
                            .with(csrf())
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                        .andExpect(flash().attributeExists("message"))
                        .andDo(print())
        ;

        Account jungho = accountRepository.findByNickname("jungho");
        assertEquals(jungho.getBio(), bio);
    }

    @Test
    @WithAccount("jungho")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    void updateProfile_error()throws Exception{

        String bio = "길ㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹㄹ게ㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔ 소개를 작성해주세요";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andDo(print())
        ;

        Account jungho = accountRepository.findByNickname("jungho");
        assertNull(jungho.getBio());
    }

    @Test
    @WithAccount("jungho")
    @DisplayName("프로필 수정폼 - 로그인 된 사용자")
    void profile_view()throws Exception{

        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL)
        )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andDo(print())
        ;
    }


    @Test
    @DisplayName("프로필 수정폼 - 로그인 안된 사용자")
    void profile_view_without_login()throws Exception{

        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andDo(print())
        ;
    }
    @Test
    @WithAccount("jungho")
    void 패스워드_view_출력()throws Exception{
        //given

        //when

        //then
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_PASSWORD_NAME))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
                .andDo(print())
                ;

    }

    @Test
    @WithAccount("jungho")
    @DisplayName("패스워드 수정 - 성공")
    void 패스워드_수정_성공()throws Exception{
        //given

        //when

        //then
        String password = "11111111";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_PASSWORD_NAME)
                .param("newPassword" , password)
                .param("newPasswordConfirm", password)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"))

                ;

        Account jungho = accountRepository.findByNickname("jungho");
        assertTrue(bCryptPasswordEncoder.matches(password, jungho.getPassword()));
    }


    @Test
    @WithAccount("jungho")
    @DisplayName("패스워드 수정 - 실패 - 패스워드 불일치")
    void 패스워드_수정_실패_불일치()throws Exception{
        //given

        //when

        //then
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_PASSWORD_NAME)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
}