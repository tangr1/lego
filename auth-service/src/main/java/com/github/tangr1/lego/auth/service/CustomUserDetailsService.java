package com.github.tangr1.lego.auth.service;

import com.github.tangr1.lego.auth.entity.Account;
import com.github.tangr1.lego.auth.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Account account = accountRepository.findByName(name);
        if (account == null) {
            throw new UsernameNotFoundException(String.format("用户名%s的用户不存在", name));
        }
        return account;
    }
}
