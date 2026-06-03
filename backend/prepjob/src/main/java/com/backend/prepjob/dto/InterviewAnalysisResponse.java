package com.backend.prepjob.dto;

import com.backend.prepjob.model.BehaviouralQuestion;
import com.backend.prepjob.model.PreparationPlan;
import com.backend.prepjob.model.SkillGap;
import com.backend.prepjob.model.TechnicalQuestion;
import lombok.Data;

import java.util.List;

@Data
public class InterviewAnalysisResponse {

    private Integer matchScore;
    private List<TechnicalQuestion> technicalQuestions;
    private List<BehaviouralQuestion> behavioralQuestions;
    private List<SkillGap> skillGaps;
    private List<PreparationPlan> preparationPlan;
    private String title;

}