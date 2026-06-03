package com.backend.prepjob.dto;

import com.backend.prepjob.model.BehaviouralQuestion;
import com.backend.prepjob.model.PreparationPlan;
import com.backend.prepjob.model.SkillGap;
import com.backend.prepjob.model.TechnicalQuestion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class ReportResponse {

    private String id;
    private String jobDescription;
    private String resume;
    private String selfDescription;
    private String userId;

    private Integer matchScore;
    private List<TechnicalQuestion> technicalQuestions;
    private List<BehaviouralQuestion> behavioralQuestions;
    private List<SkillGap> skillGaps;
    private List<PreparationPlan> preparationPlan;
    private String title;
    private boolean isResumeGenerated;
}
