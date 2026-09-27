package com.labcloud.auth.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SlugUtils - Testes Unitários")
public class SlugUtilsTest {



    @Test 
    @DisplayName("Deve gerar slug correto de 'Laboratório de Biotecnologia'")
    void shouldGenerateSlugFromLaboratory() {
        String result = SlugUtils.generateSlug("Laboratório de Biotecnologia");
        assertThat(result).isEqualTo("laboratorio-de-biotecnologia");
    }

    @ParameterizedTest (name= "[{index}] ''{0}'' → ''{1}''")
    @CsvSource({
        "'Lab A', 'lab-a'",
        "'Test@#$%', 'test'",
        "'DNA & RNA', 'dna-rna'",
        "'Já foi', 'ja-foi'",
        "'Análise', 'analise'",
        "'Café', 'cafe'",
        "'Ação', 'acao'"
    })

    @DisplayName("Dege gerar slugs de vários inputs")
    void shouldGenerateSlugs(String input, String expected) {
        assertThat(SlugUtils.generateSlug(input)).isEqualTo(expected);
    }

    @Test 
    @DisplayName("Deve retornar String vazia para null")
    void shouldReturnEmptyForNull(){
        assertThat(SlugUtils.generateSlug(null)).isEmpty();
    }

    @Test 
    @DisplayName("Deve retornar String vazia para String vazia")
    void shoudReturnNullForNull(){
        assertThat(SlugUtils.generateSlug("")).isEmpty();
    }
    
}
