package com.labcloud.auth.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.labcloud.auth.dto.request.UserRequest;
import com.labcloud.auth.dto.response.UserResponse;
import com.labcloud.auth.enums.UserRole;
import com.labcloud.auth.exception.DuplicateResourceException;
import com.labcloud.auth.exception.ResourceNotFoundException;
import com.labcloud.auth.mappers.UserMapper;
import com.labcloud.auth.models.Laboratory;
import com.labcloud.auth.models.User;
import com.labcloud.auth.repositories.LaboratoryRepository;
import com.labcloud.auth.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final LaboratoryRepository laboratoryRepository;
    private final PasswordEncoder passwordEncoder; 

   @Transactional
    public UserResponse create(UserRequest request) {
        log.info("Criando novo usuário: {}", request.getEmail());

        //Verificar email duplicado
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuário", "email", request.getEmail());
        }

        //Buscar laboratório
        Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratório", "ID", request.getLaboratoryId()));

        //Converter para entidade
        User user = userMapper.toEntity(request);
        user.updateTenantId(laboratory.getTenantId());
        user.setLaboratory(laboratory);
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // Criptografar
        user.setActive(true);

        //Caso o Role padrão não foi definido, definir como RESEARCHER
        if (user.getRole() == null) {
            user.setRole(UserRole.RESEARCHER);
        }

        // 5. Salvar
        User saved = userRepository.save(user);
        log.info("Usuário criado com sucesso: {} - {}", saved.getId(), saved.getEmail());

        return userMapper.toResponse(saved);
}

}
