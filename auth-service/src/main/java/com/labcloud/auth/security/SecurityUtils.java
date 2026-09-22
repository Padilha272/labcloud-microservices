package com.labcloud.auth.security;

import com.labcloud.auth.models.User;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Nenhum usuário autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        throw new RuntimeException("Usuário não encontrado no contexto");
    }

    public static String getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentTenantId() {
        return getCurrentUser().getTenantId();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public static String getCurrentUserRole() {
        return getCurrentUser().getRole().name();
    }

}
