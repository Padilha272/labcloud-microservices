package com.labcloud.result.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * No Sample Service, NÃO temos acesso ao banco de usuários.
 * 
 * O UserDetailsServiceImpl apenas cria um UserDetails "stub" com base no email
 * extraído do JWT. A validação real é feita pelo JwtAuthenticationFilter.
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
