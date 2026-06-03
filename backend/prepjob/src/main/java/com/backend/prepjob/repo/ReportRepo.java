package com.backend.prepjob.repo;

import com.backend.prepjob.model.InterviewReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepo extends JpaRepository<InterviewReports, String> {
    Optional<InterviewReports> findByIdAndUserId(
            String id,
            String userId
    );

    List<InterviewReports> findByUserIdOrderByCreatedAtDesc(String userId);
}
