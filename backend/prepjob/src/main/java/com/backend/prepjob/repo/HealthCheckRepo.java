package com.backend.prepjob.repo;

import com.backend.prepjob.model.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthCheckRepo extends JpaRepository<HealthCheck, String> {


    Optional<HealthCheck> findFirstBy();
}
