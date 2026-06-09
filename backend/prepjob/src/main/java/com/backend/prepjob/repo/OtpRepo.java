package com.backend.prepjob.repo;

import com.backend.prepjob.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepo extends JpaRepository<Otp, String> {
    Optional<Otp> findByEmail(String email);
}
