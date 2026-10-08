package com.labcloud.common.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Stub de UserDetailsService.
 * 
 * A validação real do usuário é feita pelo Auth Service. Este service apenas
 * cria um UserDetails mínimo com o email do JWT.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Cria um UserDetails simples (sem senha, sem roles específicas)
        // A autenticação real vem do JWT
        return User.builder().username(email).password("") // Sem senha (não é usado)
                .authorities(Collections.emptyList()).build();
    }
}
