package com.labcloud.sample.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class SampleResponse {
    private String id;
    private String tenantId;
    private String name;
    private String type;
    private LocalDateTime collectionDate;
    private String collectionMethod;
    private Double quantity;
    private String unit;
    private String storageConditions;
    private String location;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Referência desnormalizadas
    private String experimentId;
    private String experimentName;
    private String createdBy;
    private String createdByName;


    



}
