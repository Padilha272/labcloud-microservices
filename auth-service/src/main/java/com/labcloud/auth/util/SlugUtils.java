package com.labcloud.auth.util;

import java.text.Normalizer;

public class SlugUtils {

    /*
    Esta classe tem por finalidade transformar String com acentos, espaços, 
    letras maiúsculas e transformar no padrão slug
    */
    private SlugUtils() {

    }

     public static String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        //Remover acentos
        String normalized = Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+","");

        //Converter para minúsculo, trocar espa~ps inválidos por hífen e remover hifen duplicado   
        return normalized.toLowerCase()
                .replaceAll("[^a-z0-9-]","-")// Substitui sequências de não-alfanuméricos por um único hífen
                .replaceAll("^-|-$", ""); // Remove hífens sobressalentes do início e do fim

}

}