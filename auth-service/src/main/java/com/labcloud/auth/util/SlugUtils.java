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

        String normalized = Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+","");

        return normalized.toLowerCase()
                .replaceAll("","-")
                .replaceAll("[^a-z0-9-]", "");

}

}