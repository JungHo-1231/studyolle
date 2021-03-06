package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import com.inflearn.studyolle.studyolle.domain.Tag;
import com.inflearn.studyolle.studyolle.domain.Zone;
import com.inflearn.studyolle.studyolle.settings.form.NicknameForm;
import com.inflearn.studyolle.studyolle.settings.form.Notifications;
import com.inflearn.studyolle.studyolle.settings.form.PasswordForm;
import com.inflearn.studyolle.studyolle.settings.form.Profile;
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
import java.util.Optional;
import java.util.Set;


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
        sendSighUpConfirmEmail(newAccount);
        return newAccount;
    }

    public void sendSighUpConfirmEmail(Account newAccount) {

        newAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("???????????????, ?????? ?????? ??????");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail()
        );

        javaMailSender.send(mailMessage);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        signUpForm.setPassword(bCryptPasswordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    // ????????? ?????? ???????????? ?????????
    // ???????????? ?????? ???????????? ????????? ?????? ?????? ??????????????? ???????????? ?????????
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

    public void nickNameUpdate(Account account, NicknameForm nicknameForm) {
        modelMapper.map(nicknameForm, account);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject("???????????????, ????????? ??????");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail()
        );
        javaMailSender.send(mailMessage);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a-> a.getTags().add(tag));


        //????????? ??????
//        accontRepository.getOne()
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> findAccount = accountRepository.findById(account.getId());
        return findAccount.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }
}
