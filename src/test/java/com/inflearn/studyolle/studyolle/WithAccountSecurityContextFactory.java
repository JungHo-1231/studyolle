package com.inflearn.studyolle.studyolle;

import com.inflearn.studyolle.studyolle.account.AccountService;
import com.inflearn.studyolle.studyolle.account.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String nickname = withAccount.value();

        String username = "jungho";
        String password = "12345678";
        String email = "jungho@email.com";
        SignUpForm signUpForm = new SignUpForm();

        signUpForm.setNickname(username);
        signUpForm.setPassword(password);
        signUpForm.setEmail(email);

        accountService.processNewAccount(signUpForm);

        UserDetails userDetails = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
