package com.labcloud.auth.services;

import com.labcloud.auth.dto.request.AuthRequest;
import com.labcloud.auth.dto.request.JoinRequest;
import com.labcloud.auth.dto.request.RegisterRequest;
import com.labcloud.auth.dto.response.AuthResponse;
import com.labcloud.auth.enums.UserRole;
import com.labcloud.auth.exception.DuplicateResourceException;
import com.labcloud.auth.exception.ResourceNotFoundException;
import com.labcloud.auth.models.Laboratory;
import com.labcloud.auth.models.User;
import com.labcloud.auth.repositories.LaboratoryRepository;
import com.labcloud.auth.repositories.UserRepository;
import com.labcloud.auth.util.SlugUtils;
import com.labcloud.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVITE_CODE_CHARS = "ABCDEFGHIJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ====== LOGIN ======
    @Transactional
    public AuthResponse login(AuthRequest request) {
        log.info("Tentativa de login: {}", request.getEmail());

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmailWithLaboratory(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", request.getEmail()));

        user.updateLastLogin();
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getTenantId(),
                user.getRole().name());

        log.info("Login realizado com sucesso: {}", user.getEmail());

        return buildAuthResponse(user, token);
    }

    // Registro (cria novo lab + admin)
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registrando novo laboratório e usuário admin: {} para o lab: {}", request.getEmail(),
                request.getLaboratoryName());

        // 1. Verifica se o usuário já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuário", "email", request.getEmail());
        }

        // 2. Gera o slug (tenantId) baseado no nome do laboratório
        String tenantId = SlugUtils.generateSlug(request.getLaboratoryName());

        // 3. Bloqueia se o nome/tenant existir no banco
        if (laboratoryRepository.existsByTenantId(tenantId)) {
            throw new DuplicateResourceException("Laboratório", "nome/slug", request.getLaboratoryName());
        }

        // 4. Gera o código de convite único
        String inviteCode = generateInviteCode();

        // Cria o laboratório
        Laboratory laboratory = Laboratory.builder().name(request.getLaboratoryName()).active(true).build();
        laboratory.updateTenantId(tenantId);
        laboratory.updateInviteCode(inviteCode);

        Laboratory savedLab = laboratoryRepository.save(laboratory);
        log.info("Laboratório criado com sucesso: {} - {}", savedLab.getId(), savedLab.getName());

        // Cria o usuário administrador vinculado ao laboratório recém-criado
        User user = User.builder().name(request.getName()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())).role(UserRole.ADMIN) // O criador é sempre
                                                                                              // ADMIN do próprio
                                                                                              // laboratório
                .active(true).laboratory(savedLab).build();
        user.updateTenantId(savedLab.getTenantId());

        User savedUser = userRepository.save(user);
        log.info("Usuário admin criado: {} - {}", savedUser.getId(), savedUser.getEmail());

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getTenantId(),
                savedUser.getRole().name());
        return buildAuthResponse(savedUser, token);
    }

    // ====== JOIN (entrar em lab existente com código) ======
    @Transactional
    public AuthResponse join(JoinRequest request) {
        log.info("Tentando entrar em laboratório com código: {}", request.getInviteCode());

        Laboratory laboratory = laboratoryRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratório", "código", request.getInviteCode()));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuário", "email", request.getEmail());
        }

        User user = User.builder().name(request.getName()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())).role(UserRole.RESEARCHER) // ← Não é ADMIN!
                .active(true).laboratory(laboratory).build();
        user.updateTenantId(laboratory.getTenantId());

        User savedUser = userRepository.save(user);
        log.info("Usuário entrou no lab: {} - {}", savedUser.getEmail(), laboratory.getName());

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getTenantId(),
                savedUser.getRole().name());

        return buildAuthResponse(savedUser, token);
    }

    // ====== MÉTODOS AUXILIARES ======

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder().token(token).userId(user.getId()).name(user.getName()).email(user.getEmail())
                .role(user.getRole().name()).tenantId(user.getTenantId())
                .laboratoryName(user.getLaboratory() != null ? user.getLaboratory().getName() : null).build();
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

    private String generateInviteCode() {
        String inviteCode;

        do {
            StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
            for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
                sb.append(INVITE_CODE_CHARS.charAt(RANDOM.nextInt(INVITE_CODE_CHARS.length())));
            }
            inviteCode = sb.toString();
        } while (laboratoryRepository.existsByInviteCode(inviteCode));

        return inviteCode;
    }
}