package com.backend.prepjob.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillGap {

    @NotBlank(message = "Skill is required")
    private String skill;

    @NotBlank(message = "Severity is required")
    private String severity;
}
