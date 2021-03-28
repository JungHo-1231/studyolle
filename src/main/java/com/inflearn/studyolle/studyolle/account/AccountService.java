package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AccountRepository accountRepository;

    public long save(SignUpForm signUpForm) {

        String encodePassword = bCryptPasswordEncoder.encode(signUpForm.getPassword());

        Account account = Account.builder()
                .nickname(signUpForm.getNickname())
                .email(signUpForm.getEmail())
                .password(encodePassword)
                .build();

        Account savedAccount = accountRepository.save(account);

        return savedAccount.getId();
    }
}
