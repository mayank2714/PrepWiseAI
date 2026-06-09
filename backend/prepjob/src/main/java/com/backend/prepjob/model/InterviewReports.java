package com.backend.prepjob.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_reports")
public class InterviewReports {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    @Column(columnDefinition = "LONGTEXT")
    private String jobDescription;

    @Column(columnDefinition = "LONGTEXT")
    private String resume;

    @Column(columnDefinition = "LONGTEXT")
    private String selfDescription;


    @Min(0)
    @Max(100)
    private Integer matchScore;

    @Column(columnDefinition = "LONGTEXT")
    private String technicalQuestionsJson;

    @Column(columnDefinition = "LONGTEXT")
    private String behavioralQuestionsJson;

    @Column(columnDefinition = "LONGTEXT")
    private String skillGapsJson;

    @Column(columnDefinition = "LONGTEXT")
    private String preparationPlanJson;

    @NotBlank(message = "Job title is required")
    private String title;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(columnDefinition = "LONGTEXT")
    private String generatedResumeHtml;

    private boolean isResumeGenerated = false;
//    private LocalDateTime updatedAt;
}
