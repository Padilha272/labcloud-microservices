package com.labcloud.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class SecurityUtils {

    /**
     * Retorna o email do usuário autenticado (do JWT). Funciona em TODOS os
     * serviços.
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Nenhum usuário autenticado");
        }

        return authentication.getName();
    }

    /**
     * Retorna o userId do JWT (adicionado pelo JwtAuthenticationFilter).
     */
    public static String getCurrentUserId() {
        Object userId = RequestContextHolder.currentRequestAttributes().getAttribute("userId", 0);

        if (userId == null) {
            throw new RuntimeException("userId não encontrado no contexto");
        }

        return userId.toString();
    }

    public static String getCurrentTenantId() {
        Object tenantId = RequestContextHolder.currentRequestAttributes().getAttribute("tenantId", 0);

        if (tenantId == null) {
            throw new RuntimeException("tenantId não encontrado no contexto");
        }

        return tenantId.toString();
    }

    public static String getCurrentUserRole() {
        Object role = RequestContextHolder.currentRequestAttributes().getAttribute("role", 0);

        if (role == null) {
            throw new RuntimeException("role não encontrado no contexto");
        }

        return role.toString();
    }
}