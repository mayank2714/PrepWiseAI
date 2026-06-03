package com.backend.prepjob.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BehaviouralQuestion {

    @NotBlank(message = "Behavioral question is required")
    private String question;

    @NotBlank(message = "Intention is required")
    private String intention;

    @NotBlank(message = "Answer is required")
    private String answer;
}
