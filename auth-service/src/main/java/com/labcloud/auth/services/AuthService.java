package com.labcloud.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labcloud.auth.dto.request.AuthRequest;
import com.labcloud.auth.dto.request.RegisterRequest;
import com.labcloud.auth.dto.response.AuthResponse;
import com.labcloud.auth.enums.UserRole;
import com.labcloud.auth.exception.DuplicateResourceException;
import com.labcloud.auth.exception.ResourceNotFoundException;
import com.labcloud.auth.models.Laboratory;
import com.labcloud.auth.models.User;
import com.labcloud.auth.repositories.LaboratoryRepository;
import com.labcloud.auth.repositories.UserRepository;
import com.labcloud.auth.security.JwtService;
import com.labcloud.auth.util.SlugUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class AuthService {

    private final UserRepository userRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional 
    public AuthResponse login(AuthRequest request){
        log.info("Tentativa de login: {}", request.getEmail());

        //Autenticar pro meio do Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())

        );

        //Buscar usuário (com laboratório carregado)
        User user = userRepository.findByEmailWithLaboratory(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário","email",request.getEmail()));


        //Atualizar último login
        user.updateLastLogin();
        userRepository.save(user);

        //Gerar token JWT
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getTenantId(),
                user.getRole().name()
        );

        log.info("Login realizado com sucesso: {}", user.getEmail());



        //Retornar resposta
            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .tenantId(user.getTenantId())
                    .laboratoryName(user.getLaboratory() != null ? user.getLaboratory().getName() : null)
                    .build();
    }

    //Registro de novo laboratório e admin
    @Transactional 
    public AuthResponse register (RegisterRequest request) {
        log.info("Registrando novo laboratório e usuário admin: {}",request.getEmail());

        //Verificar email duplicado
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Usuário","email",request.getEmail());
        }

        //Criar laboratório
        String tenantId = generateTenantId(request.getLaboratoryName());

        Laboratory laboratory = Laboratory.builder()
                .name(request.getLaboratoryName())
                .active(true)
                .build();
        laboratory.updateTenantId(tenantId);

         Laboratory savedLab = laboratoryRepository.save(laboratory);
        log.info("Laboratório criado: {} - {}", savedLab.getId(), savedLab.getName());

        // 3. Criar usuário admin
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ADMIN)
                .active(true)
                .laboratory(savedLab)
                .build();
        user.updateTenantId(savedLab.getTenantId());

        User savedUser = userRepository.save(user);
        log.info("Usuário admin criado: {} - {}", savedUser.getId(), savedUser.getEmail());

        //Gerar token JWT
        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getTenantId(),
                savedUser.getRole().name()
        );

        log.info("Registro realizado com sucesso: {} - Lab: {}", savedUser.getEmail(), savedLab.getName());

        //Retornar resposta
        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .tenantId(savedUser.getTenantId())
                .laboratoryName(savedLab.getName())
                .build();
    }

        
    
        



    private String generateTenantId(String name) {
        String base = SlugUtils.generateSlug(name);

        String tenantId = base;
        int counter = 1;

        while (laboratoryRepository.existsByTenantId(tenantId)) {
            tenantId = base + "-" + counter;
            counter++;
        }

        return tenantId;
    }

}