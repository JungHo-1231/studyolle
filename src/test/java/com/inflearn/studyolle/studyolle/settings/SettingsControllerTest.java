package com.inflearn.studyolle.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inflearn.studyolle.studyolle.WithAccount;
import com.inflearn.studyolle.studyolle.account.AccountRepository;
import com.inflearn.studyolle.studyolle.account.AccountService;
import com.inflearn.studyolle.studyolle.domain.Account;
import com.inflearn.studyolle.studyolle.domain.Tag;
import com.inflearn.studyolle.studyolle.settings.form.TagForm;
import com.inflearn.studyolle.studyolle.tag.TagRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.inflearn.studyolle.studyolle.settings.SettingsController.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;

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
    @DisplayName("태그 수정 폼")
    void 태그_수정_폼()throws Exception{
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tag"))
                .andExpect(model().attributeExists("whitelist"))

        ;
    }

    @Test
    @WithAccount("jungho")
    @DisplayName("계정에 태그 추가")
    void 계정에_태그_추가()throws Exception{
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("테스트");


        //given
        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf())

        )
                .andDo(print())
                .andExpect(status().isOk())
                ;

        Tag findTag = tagRepository.findByTitle(tagForm.getTagTitle());

        assertThat(findTag).isNotNull();
        Account jungho = accountRepository.findByNickname("jungho");
        assertTrue(jungho.getTags().contains(findTag));

    }

    @Test
    @WithAccount("jungho")
    @DisplayName("계정에 태그 삭제")
    void 계정에_태그_삭제()throws Exception{
        Account junho = accountRepository.findByNickname("jungho");

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("테스트");

        Tag savedTag = tagRepository.save(Tag.builder().title("테스트").build());
        accountService.addTag(junho, savedTag);

        assertTrue(junho.getTags().contains(savedTag));

        //given
        mockMvc.perform(post(ROOT + SETTINGS + TAGS +"/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf())
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;

        assertFalse(junho.getTags().contains(savedTag));
    }

    @Test
    @WithAccount("jungho")
    @DisplayName("프로필 수정하기 - 입력값 정상")
//    @WithUserDetails(value = "jungho", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateProfile()throws Exception{

        String bio = "짧은 소개를 작성해주세요";
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                            .param("bio", bio)
                            .with(csrf())
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
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
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                .param("bio", bio)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
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

        mockMvc.perform(get(ROOT + SETTINGS + PROFILE)
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andDo(print())
        ;
    }


    @Test
    @DisplayName("프로필 수정폼 - 로그인 안된 사용자")
    void profile_view_without_login()throws Exception{

        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))

                .andExpect(status().is3xxRedirection())
                .andDo(print())
        ;
    }
    @Test
    @WithAccount("jungho")
    void 패스워드_view_출력()throws Exception{
        //given

        //when

        //then
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
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
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword" , password)
                .param("newPasswordConfirm", password)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
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
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
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
