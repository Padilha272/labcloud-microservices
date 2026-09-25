package com.labcloud.auth.services;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        //Salvar
        User saved = userRepository.save(user);
        log.info("Usuário criado com sucesso: {} - {}", saved.getId(), saved.getEmail());

        return userMapper.toResponse(saved);
}

    @Transactional(readOnly = true)
    public UserResponse findById(String id) {
        log.info("Buscando usuário pelo Id: {}",id);

         User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "ID", id));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email){
        log.info("Buscando usuário pelo email: {}",email);


        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Usuário","Email", email) );
        
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        log.info("Buscando todos os usuários");

        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findByLaboratory(String laboratoryId) {
        log.info("Procurando os usuários do laboratório: {}",laboratoryId);


        if (!laboratoryRepository.existsById(laboratoryId)) {
            throw new ResourceNotFoundException("Laboratorio","Id",laboratoryId);
        }

        return userRepository.findByLaboratoryId(laboratoryId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<UserResponse> findByTenantId(String tenantId) {
            log.info("Buscando usuários pelo TenantId: {}",tenantId);

            return userRepository.findByTenantId(tenantId).stream()
                        .map(userMapper::toResponse)
                        .collect(Collectors.toList());

        
    }


    @Transactional(readOnly = true)
    public List<UserResponse> findByLaboratoryAndRole(String laboratoryId, UserRole role) {
            log.info("Buscando todos os usuários do laboratório {} com cargo {}",laboratoryId,role);

            return userRepository.findByLaboratoryIdAndRole(laboratoryId, role).stream()
                        .map(userMapper::toResponse)
                        .collect(Collectors.toList());

    }

    //Update 
    @Transactional
    public  UserResponse update (String id, UserRequest request) {
            log.info("Atualizando o usuário: {}",id);

        //Buscar usuário
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "ID", id));

        //Caso o email tenha sido alterado, verifique se há duplicata
        if (!user.getEmail().equals(request.getEmail()) &&
                  userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuário","Email",request.getEmail());
        }


        //Se laboratoryId mudou, atualize
        if (request.getLaboratoryId() != null &&
                    !user.getLaboratory().getId().equals(request.getLaboratoryId())) {

           Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Laboratório", "ID", request.getLaboratoryId()));
            user.setLaboratory(laboratory);
            user.updateTenantId(laboratory.getTenantId());
        
        }

        //Os outro campos, atualizar pelo método update
        userMapper.updateEntity(request, user);


        //Caso venha uma senha, criptografar
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }


        User updated = userRepository.save(user);
        log.info("Usuário atualizado com sucesso: {}",updated.getId());

        return userMapper.toResponse(updated);
    }


    @Transactional 
    public void delete (String id) {
        log.info("Desativando usuário: {}",id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário","id",id));

        user.setActive(false);

        userRepository.save(user);

        log.info("Usuário desativado : {}",id);
    }

    public void hardDelete(String id) {
        log.info("Removendo usuário permanentemente: {}",id);

        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException("Usuário", "ID", id);
        }    

        userRepository.deleteById(id);

        log.info("Usuário removido permantemente : {}",id);

    }

     @Transactional
    public void updateLastLogin(String email) {
        log.info("Atualizando último login do usuário: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));

        user.updateLastLogin();
        userRepository.save(user);
    }


}
