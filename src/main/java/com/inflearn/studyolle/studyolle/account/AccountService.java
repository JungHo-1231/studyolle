package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import com.inflearn.studyolle.studyolle.settings.Notifications;
import com.inflearn.studyolle.studyolle.settings.PasswordForm;
import com.inflearn.studyolle.studyolle.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final ModelMapper modelMapper;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {

        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSighUpConfirmEmail(newAccount);

        return newAccount;

    }

    public void sendSighUpConfirmEmail(Account newAccount) {

        newAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("스터디올래, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail()
        );

        javaMailSender.send(mailMessage);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        String encodePassword = bCryptPasswordEncoder.encode(signUpForm.getPassword());

        Account account = Account.builder()
                .nickname(signUpForm.getNickname())
                .email(signUpForm.getEmail())
                .password(encodePassword)
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyCreatedByWeb(true)
                .build();

        accountRepository.save(account);

        return account;
    }

    // 정석이 아닌 방법으로 로그인
    // 정석대로 하기 위해서는 암호화 되지 않은 비밀번호가 필요하기 때문에
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(emailOrNickname);

        if (account == null){
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    public void completeSignup(Account account) {
        account.completeSighUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
//        account.setBio(profile.getBio());
//        account.setLocation(profile.getLocation());
//        account.setOccupation(profile.getOccupation());
//        account.setUrl(profile.getUrl());
//        account.setProfileImage(profile.getProfileImage());

        modelMapper.map(profile, account);
        accountRepository.save(account);

    }

    public void updatePassword(Account account, PasswordForm passwordForm) throws Exception {
        String newPassword = passwordForm.getNewPassword();

        String encode = bCryptPasswordEncoder.encode(newPassword);
        account.setPassword(encode);

        accountRepository.save(account);
    }

    public void updateNotification(Account account, Notifications notifications) {

//        account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
//        account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
//        account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());
//        account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
//        account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
//        account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());

        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }
}
