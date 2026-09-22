package com.labcloud.auth.security;

import com.labcloud.auth.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // Esta classe tem a função de carregar o usuário do banco

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Carregando usuário por email: {}", email);

        return userRepository.findByEmailWithLaboratory(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado" + email));
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(String userId) {
        log.debug("Carregando usuário por id: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado" + userId));
    }

}
