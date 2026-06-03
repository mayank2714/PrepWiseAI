package com.backend.prepjob.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllReportsResponse {

    private String id;
    private String title;
    private Integer matchScore;
    private LocalDateTime createdAt;
    private String userId;
}

