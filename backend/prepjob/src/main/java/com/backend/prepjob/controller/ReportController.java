package com.backend.prepjob.controller;

import com.backend.prepjob.dto.AllReportsResponse;
import com.backend.prepjob.dto.ReportResponse;
import com.backend.prepjob.service.AiService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {

    private final AiService aiService;

    @PostMapping("/")
    public ReportResponse generateInterviewReport(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("selfDescription") String selfDescription,
            @RequestParam("jobDescription") String jobDescription
    ){

        System.out.println("request received" + jobDescription);
        return aiService.generateReport(resume, selfDescription, jobDescription);
    }

    @GetMapping("/{reportId}")
    public ReportResponse getReportById(@PathVariable String reportId){
        return aiService.getReportById(reportId);
    }

    @GetMapping("/allReports")
    public List<AllReportsResponse> getAllReports(){
        return aiService.getAllReports();
    }

    @PostMapping ("/downloadResume/{reportId}")
    public ResponseEntity<byte[]> generateResumePdf(@PathVariable String reportId){

        byte[] pdfBytes =  aiService.generateResumePdf(reportId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=resume_" +
                                reportId +
                                ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
