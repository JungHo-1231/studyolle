package com.inflearn.studyolle.studyolle.account;

import com.inflearn.studyolle.studyolle.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByEmailAndEmailCheckToken(String email, String token);

}
