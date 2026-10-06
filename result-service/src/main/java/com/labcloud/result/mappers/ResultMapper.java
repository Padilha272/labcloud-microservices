package com.labcloud.result.mappers;

import org.springframework.stereotype.Component;

import com.labcloud.result.dto.request.ResultRequest;
import com.labcloud.result.dto.response.ResultResponse;
import com.labcloud.result.models.Result;

@Component
public class ResultMapper {

    public Result toEntity(ResultRequest request) {

        if (request == null)
            return null;

        return Result.builder().parameter(request.getParameter()).value(request.getValue()).unit(request.getUnit())
                .measurementDate(request.getMeasurementDate()).instrument(request.getInstrument())
                .method(request.getMethod()).observations(request.getObservations())
                .isValid(request.getIsValid() != null ? request.getIsValid() : true)
                .qualityControl(request.getQualityControl()).sampleId(request.getSampleId()).build();

    }

    public ResultResponse toResponse(Result result) {

        if (result == null)
            return null;

        return ResultResponse.builder().id(result.getId()).parameter(result.getParameter()).value(result.getValue())
                .unit(result.getUnit()).measurementDate(result.getMeasurementDate()).instrument(result.getInstrument())
                .method(result.getMethod()).observations(result.getObservations()).isValid(result.getIsValid())
                .qualityControl(result.getQualityControl()).tenantId(result.getTenantId())
                .createdAt(result.getCreatedAt()).updatedAt(result.getUpdatedAt()).sampleId(result.getSampleId())
                .sampleName(result.getSampleName()).experimentId(result.getExperimentId())
                .experimentName(result.getExperimentName()).createdBy(result.getCreatedBy())
                .createdByName(result.getCreatedByName()).build();

    }

    public void updateEntity(ResultRequest request, Result result) {
        if (request == null || result == null)
            return;

        if (request.getParameter() != null) {
            result.setParameter(request.getParameter());
        }
        if (request.getValue() != null) {
            result.setValue(request.getValue());
        }
        if (request.getUnit() != null) {
            result.setUnit(request.getUnit());
        }
        if (request.getMeasurementDate() != null) {
            result.setMeasurementDate(request.getMeasurementDate());
        }
        if (request.getInstrument() != null) {
            result.setInstrument(request.getInstrument());
        }
        if (request.getMethod() != null) {
            result.setMethod(request.getMethod());
        }
        if (request.getObservations() != null) {
            result.setObservations(request.getObservations());
        }
        if (request.getIsValid() != null) {
            result.setIsValid(request.getIsValid());
        }
        if (request.getQualityControl() != null) {
            result.setQualityControl(request.getQualityControl());
        }
    }

}
