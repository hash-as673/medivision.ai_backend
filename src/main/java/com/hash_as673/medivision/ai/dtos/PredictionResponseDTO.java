package com.hash_as673.medivision.ai.dtos;

import lombok.Data;

@Data
public class PredictionResponseDTO {
    private String prediction;
    private String explanation;

    // Custom constructor
    public PredictionResponseDTO(String prediction, String explanation) {
        this.prediction = prediction;
        // this.explanation = explanation;
    }
}