package com.backend.prepjob.repo;

import com.backend.prepjob.model.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface BlackListedTokenRepo extends JpaRepository<BlackListToken, String> {
    boolean existsByToken(String token);
}
