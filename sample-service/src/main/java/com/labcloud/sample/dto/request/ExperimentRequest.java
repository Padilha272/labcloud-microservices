package com.labcloud.sample.dto.request;

import java.time.LocalDateTime;

import com.labcloud.sample.enums.ExperimentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class ExperimentRequest {

    @NotBlank(message = "Nome do experimento é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @NotNull(message = "Status é obrigatório")
    private ExperimentStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Size(max = 500, message = "Objetivo deve ter no máximo 500 caracteres")
    private String objective;

    @Size(max = 500, message = "Metodologia deve ter no máximo 500 caracteres")
    private String methodology;

    @NotNull(message = "ID do laboratório é obrigatório")
    private String laboratoryId;

}
