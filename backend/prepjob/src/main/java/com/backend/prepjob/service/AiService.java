package com.backend.prepjob.service;

import com.backend.prepjob.dto.AllReportsResponse;
import com.backend.prepjob.dto.InterviewAnalysisResponse;
import com.backend.prepjob.dto.ReportResponse;
import com.backend.prepjob.dto.ResumeHtmlResponse;
import com.backend.prepjob.model.InterviewReports;
import com.backend.prepjob.model.User;
import com.backend.prepjob.repo.ReportRepo;
import com.backend.prepjob.repo.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;
    private final UserRepo userRepo;
    private final ReportRepo reportRepo;
    public AiService(WebClient webClient, UserRepo userRepo, ReportRepo reportRepo) {
        this.webClient = webClient;
        this.userRepo = userRepo;
        this.reportRepo = reportRepo;
    }

    private Map<String, Object> interviewReportResponseSchema(){

        Map<String, Object> questionSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "question", Map.of("type", "string"),
                        "intention", Map.of("type", "string"),
                        "answer", Map.of("type", "string")
                ),
                "required", List.of("question", "intention", "answer")
        );

        Map<String, Object> skillGapSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "skill", Map.of("type", "string"),
                        "severity", Map.of(
                                "type", "string",
                                "enum", List.of("low", "medium", "high")
                        )
                ),
                "required", List.of("skill", "severity")
        );

        Map<String, Object> preparationPlanSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "day", Map.of("type", "integer"),
                        "focus", Map.of("type", "string"),
                        "tasks", Map.of(
                                "type", "array",
                                "items", Map.of("type", "string")
                        )
                ),
                "required", List.of("day", "focus", "tasks")
        );

        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "matchScore", Map.of(
                                "type", "number",
                                "minimum", 0,
                                "maximum", 100
                        ),
                        "technicalQuestions", Map.of(
                                "type", "array",
                                "items", questionSchema
                        ),
                        "behavioralQuestions", Map.of(
                                "type", "array",
                                "items", questionSchema
                        ),
                        "skillGaps", Map.of(
                                "type", "array",
                                "items", skillGapSchema
                        ),
                        "preparationPlan", Map.of(
                                "type", "array",
                                "items", preparationPlanSchema
                        ),
                        "title", Map.of("type", "string")
                ),
                "required", List.of(
                        "matchScore",
                        "technicalQuestions",
                        "behavioralQuestions",
                        "skillGaps",
                        "preparationPlan",
                        "title"
                )
        );
    }


    public InterviewAnalysisResponse generateInterviewReport(String resume, String selfDescription, String jobDescription){

        String prompt = """
                Generate an interview report for a candidate with following details
                
                Resume = %s
                
                Self Description = %s
                
                Job Description = %s
                
                Below are the descriptions of the required fields:
                
                {
                  "matchScore": 85,
                  "technicalQuestions": [
                    {
                      "question": "The technical question that can be asked in the interview",
                      "intention": "The intention of the interviewer behind asking this question",
                      "answer": "How to answer this question, what points to cover, what approach to take"
                    }
                  ],
                  "behavioralQuestions": [
                    {
                      "question": "The behavioral question that can be asked in the interview",
                      "intention": "The intention of the interviewer behind asking this question",
                      "answer": "How to answer this question, what points to cover, what approach to take"
                    }
                  ],
                  "skillGaps": [
                    {
                      "skill": "The skill which the candidate is lacking",
                      "severity": "low | medium | high"
                    }
                  ],
                  "preparationPlan": [
                    {
                      "day": 1,
                      "focus": "The main focus of this day, e.g. data structures, system design, mock interviews",
                      "tasks": [
                        "A task to be done on this day"
                      ]
                    }
                  ],
                  "title": "The title of the job for which the interview report is generated"
                }
                
                Rules:
                - matchScore must be a number between 0 and 100.
                - technicalQuestions must contain interview technical questions with intention and answer, give all the possible technical questions.
                - behavioralQuestions must contain behavioral interview questions with intention and answer, give all the behaviourdla questions.
                - skillGaps severity must be only one of: low, medium, high and please analyse all the gaps.
                - preparationPlan must be day-wise starting from day 1.
                - Give a elaborated preparation plan and keeping achievable targets on a single day, please don't put .
                multiple technologies to be learnt in a single day.
                - preparationPlan must be a realistic day-by-day roadmap.
                
                - Assume the candidate can dedicate approximately 2-4 hours per day for preparation.
                
                - Each day should focus on only ONE major topic or ONE closely related subtopic.
                
                - Large topics such as Node.js, React, Spring Boot, System Design, SQL, PostgreSQL, Microservices, Docker, Kubernetes, etc. must be spread across multiple days.
                
                - Do NOT assume a candidate can master an entire technology in a single day.
                
                - For beginner or intermediate level topics, allocate multiple consecutive days covering:
                  1. Fundamentals
                  2. Intermediate concepts
                  3. Advanced concepts
                  4. Hands-on practice
                  5. Revision and mock interview questions
                
                - Every task should be realistically achievable within a single day.
                
                - The roadmap should build progressively from fundamentals to advanced concepts.
                
                - If a technology requires 5-10 days of preparation, distribute it across 5-10 separate days.
                
                - Include dedicated revision days and mock interview practice days.
                
                - Do not combine unrelated technologies in the same day.
                
                - For example:
                  Day 1: Node.js fundamentals
                  Day 2: Node.js modules and async programming
                  Day 3: Express.js basics
                  Day 4: Express.js middleware and routing
                  Day 5: Build a small REST API
                  Day 6: SQL fundamentals
                  Day 7: SQL joins and aggregations
                
                - The preparation plan should continue until all identified skill gaps are addressed.
                - Return only JSON.
                - Do not include markdown.
                - Do not include explanation outside JSON.
                """.formatted(resume, selfDescription, jobDescription);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", interviewReportResponseSchema()
                )
        );

        String geminiResponse = callGemini(requestBody);

        try{
            String jsonText = extractTextFromGeminiResponse(geminiResponse);

            return objectMapper.readValue(
                    jsonText,
                    InterviewAnalysisResponse.class
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse gemini response",e);
        }
    }


    private String callGemini(Map<String, Object> requestBody){

        return webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String extractTextFromGeminiResponse(String response) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(response);
        return rootNode.path("candidates").get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text").asText();


    }

    private String writeJson(Object value) {
        try {
            if (value == null) {
                return "[]";
            }
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    private <T> T readJson(String json, TypeReference<T> typeReference) {
        try {
            if (json == null || json.isBlank()) {
                return objectMapper.readValue("[]", typeReference);
            }
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON from database", e);
        }
    }

    private ReportResponse mapToReportResponse(InterviewReports savedReport) {
        ReportResponse reportResponse = new ReportResponse();

        reportResponse.setId(savedReport.getId());
        reportResponse.setTitle(savedReport.getTitle());
        reportResponse.setMatchScore(savedReport.getMatchScore());
        reportResponse.setSelfDescription(savedReport.getSelfDescription());
        reportResponse.setJobDescription(savedReport.getJobDescription());
        reportResponse.setResume(savedReport.getResume());
        reportResponse.setUserId(savedReport.getUserId());
        reportResponse.setResumeGenerated(savedReport.isResumeGenerated());

        reportResponse.setTechnicalQuestions(
                readJson(savedReport.getTechnicalQuestionsJson(), new TypeReference<>() {})
        );

        reportResponse.setBehavioralQuestions(
                readJson(savedReport.getBehavioralQuestionsJson(), new TypeReference<>() {})
        );

        reportResponse.setSkillGaps(
                readJson(savedReport.getSkillGapsJson(), new TypeReference<>() {})
        );

        reportResponse.setPreparationPlan(
                readJson(savedReport.getPreparationPlanJson(), new TypeReference<>() {})
        );

        return reportResponse;
    }

    public ReportResponse generateReport(MultipartFile resume, String selfDescription, String jobDescription){
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();


        String username = authentication.getName();
        User user = userRepo
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String userId = user.getId();

        String resumeContent = extractTextFromResumePdf(resume);
        InterviewAnalysisResponse geminiResponse = generateInterviewReport(resumeContent, selfDescription, jobDescription);
        InterviewReports interviewReport = new InterviewReports();

        interviewReport.setResume(resumeContent);
        interviewReport.setSelfDescription(selfDescription);
        interviewReport.setJobDescription(jobDescription);
        interviewReport.setMatchScore(geminiResponse.getMatchScore());
        interviewReport.setSkillGapsJson(writeJson(geminiResponse.getSkillGaps()));
        interviewReport.setBehavioralQuestionsJson(writeJson(geminiResponse.getBehavioralQuestions()));
        interviewReport.setPreparationPlanJson(writeJson(geminiResponse.getPreparationPlan()));
        interviewReport.setTechnicalQuestionsJson(writeJson(geminiResponse.getTechnicalQuestions()));
        interviewReport.setTitle(geminiResponse.getTitle());
        interviewReport.setUserId(userId);
        interviewReport.setCreatedAt(LocalDateTime.now());
        InterviewReports savedReport = reportRepo.save(interviewReport);

        return mapToReportResponse(savedReport);
    }


    private String extractTextFromResumePdf(MultipartFile resume)
    {
        try (PDDocument document = PDDocument.load(resume.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ReportResponse getReportById(String reportId) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();


        String username = authentication.getName();
        User user = userRepo
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String userId = user.getId();
        InterviewReports savedReport = reportRepo.findByIdAndUserId(reportId, userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview report not found."
                        ));

        return mapToReportResponse(savedReport);
    }

    public List<AllReportsResponse> getAllReports() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();


        String username = authentication.getName();
        User user = userRepo
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String userId = user.getId();
        List<InterviewReports> allReports = reportRepo.findByUserIdOrderByCreatedAtDesc(userId);

        List<AllReportsResponse> reportResponses =
                allReports.stream()
                        .map(report -> {
                            AllReportsResponse response =
                                    new AllReportsResponse();

                            response.setId(report.getId());
                            response.setTitle(report.getTitle());
                            response.setMatchScore(report.getMatchScore());
                            response.setCreatedAt(report.getCreatedAt());
                            response.setUserId(report.getUserId());

                            return response;
                        })
                        .toList();

        return reportResponses;
    }

    private Map<String, Object> resumeHtmlResponseSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "html", Map.of(
                                "type", "string",
                                "description", "The complete HTML content of the resume"
                        )
                ),
                "required", List.of("html")
        );
    }

    private byte[] generatePdfFromHtml(String html) {

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            PdfRendererBuilder builder =
                    new PdfRendererBuilder();

            builder.withHtmlContent(html, null);

            builder.toStream(outputStream);

            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate PDF",
                    e
            );
        }
    }

    public byte[] generateResumePdf(String reportId) {
        InterviewReports savedReport = reportRepo.findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview report not found"
                        ));

        if (savedReport.getGeneratedResumeHtml() != null &&
                !savedReport.getGeneratedResumeHtml().isBlank()) {

            return generatePdfFromHtml(savedReport.getGeneratedResumeHtml());
        }

        String resume = savedReport.getResume();
        String jobDescription = savedReport.getJobDescription();
        String selfDescription = savedReport.getSelfDescription();

        String prompt = """ 
                Generate a professional ATS-friendly resume based on:
                
                                                         Resume: %s
                                                         Self Description: %s
                                                         Job Description: %s
                
                                                         IMPORTANT OUTPUT FORMAT:
                
                                                         * Return ONLY a valid JSON object.
                                                         * The JSON must contain exactly one field named "html".
                                                         * Do not include markdown, explanations, or code fences.
                                                         * The value of "html" must be complete HTML that can be directly converted to PDF.
                
                                                         STRICT PAGE REQUIREMENTS:
                
                                                         * The generated resume MUST fit on exactly ONE A4 page when converted to PDF.
                                                         * Assume page size: A4 (210mm × 297mm).
                                                         * Maximum content length: 550-700 words.
                                                         * Never exceed 700 words.
                                                         * Prioritize the most relevant experience, skills, and achievements for the provided job description.
                                                         * Remove redundant information.
                                                         * Keep the professional summary to 2-3 lines maximum.
                                                         * Include only the most impactful projects and achievements.
                                                         * Limit experience section to the most relevant responsibilities and measurable achievements.
                                                         * Use concise bullet points (maximum 1 line each when possible).
                                                         * Maximum 4 bullet points per job/project.
                                                         * Maximum 10 skills.
                                                         * Keep the job description in the context while creating the resume.
                
                                                         HTML/CSS REQUIREMENTS:
                
                                                         * Use inline CSS only.
                                                         * Set body margin to 12mm.
                                                         * Use font-family: Arial, Helvetica, sans-serif.
                                                         * Base font size: 12px.
                                                         * Section heading size: 14px.
                                                         * Candidate name size: 18px.
                                                         * Line-height: 1.2.
                                                         * Avoid excessive whitespace, padding, and margins.
                                                         * Use a clean single-column layout.
                                                         * Do not use tables for the main layout.
                                                         * Do not use images, icons, SVGs, avatars, or graphics.
                                                         * Use subtle colors only for headings.
                                                         * Ensure the HTML is compatible with PDF rendering libraries.
                                                         * Don't put any text in italics.
                                                         * Highlight or bold the important terms and parts.
                
                                                         CONTENT QUALITY:
                
                                                         * Tailor the resume specifically to the provided job description.
                                                         * Optimize for ATS parsing.
                                                         * Quantify achievements wherever possible.
                                                         * Write naturally and professionally.
                                                         * Avoid generic AI-generated phrases such as:
                                                           "Results-driven professional"
                                                           "Passionate developer"
                                                           "Highly motivated individual"
                                                         * Focus on measurable business and technical impact.
                
                                                         Before returning the HTML:
                
                                                         1. Estimate whether it will fit on a single A4 page.
                                                         2. If it exceeds one page, reduce content rather than reducing readability.
                                                         3. Prioritize relevance to the job description over completeness.
                The generated HTML must be valid XHTML.
                All special characters must be properly escaped.
                Replace:
                & with &amp;
                < with &lt; when used as text
                > with &gt; when used as text
                " with &quot; inside attribute values
               
                Do NOT use named HTML entities such as:
                &ndash;
                &mdash;
                &nbsp;
                &copy;
                &reg;
                &trade;
                
                Instead use either:
                - Unicode characters directly
                - Numeric entities (e.g. &#8211;)
                
                The HTML must be parseable by OpenHTMLToPDF without XML parsing errors.
                
                Return valid XHTML.
                        Self-close all void tags:
                        <meta />
                        <br />
                        <hr />
                        <img />
                        <link />
                        <input />"""
                .formatted(resume, selfDescription, jobDescription);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", resumeHtmlResponseSchema()
                )
        );

        String geminiResponse = callGemini(requestBody);
        try
        {
            String jsonText = extractTextFromGeminiResponse(geminiResponse);

            ResumeHtmlResponse htmlResponse =
                    objectMapper.readValue(jsonText, ResumeHtmlResponse.class);

            String generatedHtml = htmlResponse.getHtml();

            savedReport.setGeneratedResumeHtml(generatedHtml);
            savedReport.setResumeGenerated(true);
            reportRepo.save(savedReport);

            return generatePdfFromHtml(generatedHtml);
        }
        catch(JsonProcessingException e)
        {
            throw new RuntimeException("failed to parse gemini response for resume" + e);
        }
    }
}
