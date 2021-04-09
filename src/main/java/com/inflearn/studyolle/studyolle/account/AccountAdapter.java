package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
public class AccountAdapter extends User {

    private Account account;

    public AccountAdapter(Account account){
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }


}
