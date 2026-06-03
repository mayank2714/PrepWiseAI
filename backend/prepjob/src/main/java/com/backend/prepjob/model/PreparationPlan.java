package com.backend.prepjob.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PreparationPlan {

    @NotNull(message = "Day is required")
    private Integer day;

    @NotBlank(message = "Focus is required")
    private String focus;

    private List<String> tasks;
}
